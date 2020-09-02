package eu.uga;

import net.minecraft.text.TranslatableText;

import java.util.UUID;

public interface IMinecraftChatHandler
{
	void OnMinecraftMessage(TranslatableText message, UUID sender);
}
