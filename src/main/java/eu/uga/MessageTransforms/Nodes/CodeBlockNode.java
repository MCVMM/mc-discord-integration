package eu.uga.MessageTransforms.Nodes;

import com.discord.core.node.StyledTextNode;
import eu.uga.MessageTransforms.Colors;
import net.minecraft.text.Style;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class CodeBlockNode<F> extends StyledTextNode<F, Style>
{
	protected final String _language;
	
	public CodeBlockNode(@Nullable String language, @Nonnull String code)
	{
		super(Style.EMPTY.withColor(Colors.CodeBlockColor), code);
		_language = language;
	}
}
