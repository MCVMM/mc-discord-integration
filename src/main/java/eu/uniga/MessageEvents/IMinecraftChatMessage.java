package eu.uniga.MessageEvents;

import net.minecraft.text.TranslatableText;

import java.util.UUID;

public interface IMinecraftChatMessage
{
	TranslatableText OnMessageSent(TranslatableText message, UUID sender);
}
