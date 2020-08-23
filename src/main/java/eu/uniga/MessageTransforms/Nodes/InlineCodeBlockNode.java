package eu.uniga.MessageTransforms.Nodes;

import com.discord.core.node.StyledTextNode;
import eu.uniga.MessageTransforms.Colors;
import net.minecraft.text.Style;

public class InlineCodeBlockNode<F> extends StyledTextNode<F, Style>
{
	public InlineCodeBlockNode(String code)
	{
		super(Style.EMPTY.withColor(Colors.InlineCodeBlockColor), code);
	}
}
