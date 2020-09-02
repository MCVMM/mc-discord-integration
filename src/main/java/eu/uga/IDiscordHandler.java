package eu.uga;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;

public interface IDiscordHandler
{
	void OnDiscordMessage(@NotNull Member member, @NotNull Message message);
	void OnEmojiChange();
}
