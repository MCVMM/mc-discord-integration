package eu.uga.MessageTransforms.Nodes;

import com.discord.core.node.StyledTextNode;
import eu.uga.MessageTransforms.Colors;
import eu.uga.MessageTransforms.FormattingContext;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;

import javax.annotation.Nonnull;

public class MultipleMentionNode<F extends FormattingContext> extends StyledTextNode<FormattingContext, Style>
{
	public MultipleMentionNode(@Nonnull String roleName)
	{
		setStyle(getStyle()
						.withColor(Colors.MultipleMentionColor)
						.withBold(true)
						.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText("@" + roleName)))
						.withInsertion("@" + roleName));
		setText("@" + roleName);
	}
}
