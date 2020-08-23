package eu.uniga.MessageTransforms.DiscordToMinecraft;

import net.minecraft.text.TranslatableText;

public interface IDiscordToMinecraftTransform
{
	TranslatableText Transform(TranslatableText text);
}
