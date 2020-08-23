package eu.uniga.MessageTransforms.Nodes;

import com.discord.core.node.StyledTextNode;
import eu.uniga.MessageTransforms.FormattingContext;
import eu.uniga.Utils.CodePoints;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class EmojiNode<F extends FormattingContext> extends StyledTextNode<FormattingContext, Style>
{
	protected Boolean _animated;
	protected String _shortName;
	protected String _name;
	protected Long _snowflake;
	protected String _unicode;
	
	private EmojiNode(@Nullable Boolean animated, @Nullable String shortName, @Nullable String name, @Nullable Long snowflake, @Nullable String unicode)
	{
		super();
		
		_animated = animated;
		_shortName = shortName;
		_name = name;
		_snowflake = snowflake;
		_unicode = unicode;
	}
	
	public static <F extends FormattingContext> EmojiNode<F> FromShortName(@Nullable Boolean animated, String shortName)
	{
		return new EmojiNode<F>(animated, shortName, null, null, null);
	}
	
	public static <F extends FormattingContext> EmojiNode<F> FromDiscordName(boolean animated, String name, long snowflake)
	{
		return new EmojiNode<F>(animated, null, name, snowflake, null);
	}
	
	public static <F extends FormattingContext> EmojiNode<F> FromUnicode(@Nonnull String unicode)
	{
		return new EmojiNode<F>(null, null, null, null, unicode);
	}
	
	@NotNull
	@Override
	public LiteralText format(FormattingContext formattingContext)
	{
		String fullName;
		String shortName;
		
		// We have unicode surrogate pair
		if (_unicode != null)
		{
			fullName = _unicode;
			shortName = formattingContext.GetDictionary().GetShortNameFromDiscord(_unicode);
			
			// We do not have shortname for this emote
			if (shortName == null)
			{
				setStyle(getStyle()
								.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText(":???:").setStyle(Style.EMPTY.withItalic(true))))
								.withInsertion(_unicode));
				setText(_unicode);
				
				return super.format(formattingContext);
			}
		}
		// We have name and snowflake
		else if (_shortName == null)
		{
			fullName = "<" + (_animated ? "a" : "") + ":" + _name + ":" + _snowflake.toString() + ">";
			_shortName = formattingContext.GetDictionary().GetShortNameFromDiscord(fullName);
			
			// Not valid emote, show original text ("fullName")
			if (_shortName == null)
			{
				setText(fullName);
				
				return super.format(formattingContext);
			}
			
			// Valid emote, get shortname for hover event
			shortName = _shortName;
		}
		// We have only (our) shortname (without ':')
		else
		{
			shortName = ":" + _shortName + ":";
			fullName = formattingContext.GetDictionary().GetDiscordFromShortName(shortName);
			
			// Not valid emote, show original text ("shortname")
			if (fullName == null)
			{
				setText(shortName);
				
				return super.format(formattingContext);
			}
		}
		
		// Always valid
		Integer surrogatePair = formattingContext.GetDictionary().GetSurrogatePairFromDiscord(fullName);
		
		setText(CodePoints.Utf16ToString(surrogatePair));
		setStyle(getStyle()
						.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText(shortName)))
						.withInsertion(shortName));
		
		return super.format(formattingContext);
	}
}
