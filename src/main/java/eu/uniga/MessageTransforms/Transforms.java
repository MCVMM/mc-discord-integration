package eu.uniga.MessageTransforms;

import eu.uniga.MessageTransforms.MinecraftToDiscord.IMinecraftToDiscordTransform;
import eu.uniga.MessageTransforms.MinecraftToMinecraft.IMinecraftToMinecraftTransform;
import net.minecraft.text.TranslatableText;

import java.util.ArrayList;
import java.util.List;

public class Transforms
{
	public Transforms(SurrogatePairsDictionary dictionary)
	{
		_minecraftToDiscordTransforms.add(new eu.uniga.MessageTransforms.MinecraftToDiscord.EmojiTransform(dictionary));
		_minecraftToMinecraftTransforms.add(new eu.uniga.MessageTransforms.MinecraftToMinecraft.EmojiTransform(dictionary));
	}
	
	private final List<IMinecraftToDiscordTransform> _minecraftToDiscordTransforms = new ArrayList<>();
	private final List<IMinecraftToMinecraftTransform> _minecraftToMinecraftTransforms = new ArrayList<>();
	
	public String MinecraftToDiscord(String text)
	{
		for (IMinecraftToDiscordTransform transform : _minecraftToDiscordTransforms) text = transform.Transform(text);
		
		return text;
	}
	
	public TranslatableText MinecraftToMinecraft(TranslatableText text)
	{
		for (IMinecraftToMinecraftTransform transform : _minecraftToMinecraftTransforms) text = transform.Transform(text);
		
		return text;
	}
}
