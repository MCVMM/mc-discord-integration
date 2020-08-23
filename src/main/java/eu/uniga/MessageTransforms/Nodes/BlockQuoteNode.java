package eu.uniga.MessageTransforms.Nodes;

import com.discord.core.node.StyleNode;
import eu.uniga.MessageTransforms.Colors;
import net.minecraft.text.Style;

public class BlockQuoteNode<F> extends StyleNode<F, Style>
{
	public BlockQuoteNode()
	{
		super(Style.EMPTY.withColor(Colors.BlockQuoteColor));
	}
}