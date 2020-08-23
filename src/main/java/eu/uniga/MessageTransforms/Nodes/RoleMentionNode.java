package eu.uniga.MessageTransforms.Nodes;

import com.discord.core.node.StyledTextNode;
import eu.uniga.MessageTransforms.FormattingContext;
import net.dv8tion.jda.api.entities.Role;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.TextColor;
import org.jetbrains.annotations.NotNull;

public class RoleMentionNode<F extends FormattingContext> extends StyledTextNode<FormattingContext, Style>
{
	protected final long _id;
	
	public RoleMentionNode(long id)
	{
		super();
		
		_id = id;
	}
	
	@NotNull
	@Override
	public LiteralText format(FormattingContext formattingContext)
	{
		Role role = formattingContext.GetClient().getRoleById(_id);
		
		// Role is valid
		if (role != null)
		{
			setStyle(getStyle()
							.withColor(TextColor.fromRgb(role.getColorRaw()))
							.withBold(true)
							.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText(role.getAsMention())))
							.withInsertion(role.getAsMention()));
			setText("@" + role.getName());
		}
		// Not a valid role
		else setText("@deleted-role");
		
		return super.format(formattingContext);
	}
}
