package eu.uga.MessageTransforms.Nodes;

import com.discord.core.node.StyledTextNode;
import eu.uga.MessageTransforms.FormattingContext;
import eu.uga.MessageTransforms.Colors;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import org.jetbrains.annotations.NotNull;

public class ChannelMentionNode<F extends FormattingContext> extends StyledTextNode<FormattingContext, Style>
{
	protected final long _id;
	
	public ChannelMentionNode(long id)
	{
		super();
		
		_id = id;
	}
	
	@NotNull
	@Override
	public LiteralText format(FormattingContext formattingContext)
	{
		TextChannel textChannel = formattingContext.GetClient().getTextChannelById(_id);
		
		setStyle(getStyle());
		
		// If text channel exists and is valid
		if (textChannel != null)
		{
			// Mention can be styled
			setStyle(getStyle()
							.withColor(Colors.ChannelMentionColor)
							.withBold(true)
							.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText(textChannel.getAsMention())))
							.withInsertion(textChannel.getAsMention()));
			setText("#" + textChannel.getName());
			
			return super.format(formattingContext);
		}
		
		
		GuildChannel channel = formattingContext.GetClient().getGuildChannelById(_id);
		
		// If it is not text channel, but still valid channel
		if (channel != null)
		{
			setStyle(getStyle()
							.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText("<#" + _id + ">")))
							.withInsertion("<#" + _id + ">"));
			setText("#" + channel.getName());
			
			return super.format(formattingContext);
		}
		
		// Not a valid channel
		setText("#deleted-channel");
		return super.format(formattingContext);
	}
}
