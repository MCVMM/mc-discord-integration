package eu.uga.MessageTransforms.Nodes;

import com.discord.core.node.StyleNode;
import eu.uga.MessageTransforms.Colors;
import eu.uga.Utils.MinecraftStyle;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;

public class SpoilerNode<F> extends StyleNode<F, Style>
{
	public SpoilerNode()
	{
		super(MinecraftStyle.NoStyle
						.withFormatting(Formatting.OBFUSCATED)
						.withColor(Colors.SpoilerColor));
	}
	
	@NotNull
	@Override
	public LiteralText format(F formattingContext)
	{
		if (!hasChildren()) return new LiteralText("");
		
		LiteralText hoverText = new LiteralText("");
		getChildren().forEach(child -> hoverText.append(child.format(formattingContext)));
		
		String obfuscated = GetStringRecursively(new StringBuilder(), hoverText).toString();
		LiteralText text = new LiteralText(obfuscated);
		
		text.setStyle(getStyle().withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText)));
		
		return text;
	}
	
	private StringBuilder GetStringRecursively(StringBuilder stringBuilder, Text text)
	{
		stringBuilder.append(text.asString().replaceAll("\\s+?", "_"));
		
		text.getSiblings().forEach(child -> GetStringRecursively(stringBuilder, child));
		
		return stringBuilder;
	}
}
