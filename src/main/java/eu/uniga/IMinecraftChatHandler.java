package eu.uniga;

import net.minecraft.text.TranslatableText;

import java.util.UUID;

public interface IMinecraftChatHandler
{
	void OnMinecraftMessage(TranslatableText message, UUID sender);
}
