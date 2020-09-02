package eu.uga.MessageTransforms.Nodes;

import com.discord.core.node.StyledTextNode;
import eu.uga.MessageTransforms.Colors;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;

public class URLNode<F> extends StyledTextNode<F, Style>
{
	public URLNode(String url)
	{
		super(Style.EMPTY
						.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url))
						.withColor(Colors.URLColor)
						.withFormatting(Formatting.UNDERLINE), url);
	}
}