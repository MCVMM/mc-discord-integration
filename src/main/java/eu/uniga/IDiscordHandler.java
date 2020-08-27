package eu.uniga;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public interface IDiscordHandler
{
	void OnDiscordMessage(@NotNull Member member, @NotNull Message message);
	void OnEmojiChange();
}
