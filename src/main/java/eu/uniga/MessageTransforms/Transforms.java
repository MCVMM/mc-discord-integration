package eu.uniga.MessageTransforms;

import com.discord.core.markdown.SimpleMarkdownRules;
import com.discord.core.node.Node;
import com.discord.core.parser.Parser;
import eu.uniga.DiscordIntegrationMod;
import eu.uniga.MessageTransforms.Rules.*;
import net.minecraft.text.LiteralText;

import java.util.List;

public class Transforms
{
	private final TextEmojiTransform _minecraftToDiscordTransform;
	private final Parser<FormattingContext, Node<FormattingContext>, ParseState> _parser;
	
	public Transforms(SurrogatePairsDictionary dictionary)
	{
		_minecraftToDiscordTransform = new TextEmojiTransform(dictionary);
		_parser =  new Parser<FormattingContext, Node<FormattingContext>, ParseState>()
						.addRule(new BlockQuoteRule<>())
						.addRule(new CodeBlockRule<>())
						.addRule(new InlineCodeBlockRule<>())
						.addRule(new SpoilerRule<>())
						.addRule(new URLRule<>(false))
						.addRule(new URLRule<>(true))
						.addRule(new DiscordEmojiRule<>())
						.addRule(new EmojiRule<>())
						.addRule(new UnicodeEmojiRule<>())
						.addRule(new EscapeEscapeRule<>())
						.addRule(new ChannelMentionRule<>())
						.addRule(new RoleMentionRule<>())
						.addRule(new UserMentionRule<>())
						.addRule(new MultipleMentionRule<>())
						.addRules(SimpleMarkdownRules.createSimpleMarkdownRules());
	}
	
	public String MinecraftToDiscord(String text)
	{
		return _minecraftToDiscordTransform.Transform(text);
	}
	
	private static class ParseState implements IParserState<ParseState>
	{
		private final boolean _isInQuote;
		
		public ParseState(boolean isInQuote)
		{
			_isInQuote = isInQuote;
		}
		
		@Override
		public ParseState NewBlockQuoteState(boolean isInQuote)
		{
			return new ParseState(isInQuote);
		}
		
		@Override
		public boolean IsInQuote()
		{
			return _isInQuote;
		}
	}
	
	public LiteralText FromString(CharSequence text)
	{
		List<Node<FormattingContext>> output = _parser.parse(text, new ParseState(false));
		
		LiteralText formattedText = new LiteralText("");
		
		output.forEach(node -> formattedText.append(node.format(DiscordIntegrationMod.formattingContext())));
		
		return formattedText;
	}
}
