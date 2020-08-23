package eu.uniga.MessageTransforms.Nodes;

import com.discord.core.node.StyledTextNode;
import eu.uniga.MessageTransforms.Colors;
import eu.uniga.MessageTransforms.FormattingContext;
import net.dv8tion.jda.api.entities.User;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import org.jetbrains.annotations.NotNull;

public class UserMentionNode<F extends FormattingContext> extends StyledTextNode<FormattingContext, Style>
{
	protected final long _id;
	
	public UserMentionNode(long id)
	{
		_id = id;
	}
	
	@NotNull
	@Override
	public LiteralText format(FormattingContext formattingContext)
	{
		User user = formattingContext.GetClient().getUserById(_id);
		
		// User is valid
		if (user != null)
		{
			setStyle(getStyle()
							.withColor(Colors.UserMentionColor)
							.withBold(true)
							.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText(user.getAsMention())))
							.withInsertion(user.getAsMention()));
			setText("@" + user.getName());
		}
		else setText("@invalid-user");
		
		return super.format(formattingContext);
	}
}
