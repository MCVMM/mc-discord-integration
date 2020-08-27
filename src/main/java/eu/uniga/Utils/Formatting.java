package eu.uniga.Utils;

import eu.uniga.MessageTransforms.MessagesTransforms;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.minecraft.entity.EntityType;
import net.minecraft.text.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Formatting
{
	private static List<String> Split(@NotNull String text, int length)
	{
		List<String> chunks = new ArrayList<>();
		int current = 0;
		
		while (current < text.length())
		{
			String chunk = text.substring(current, Math.min(current + length, text.length()));
			chunks.add(chunk);
			
			current += length;
		}
		
		return chunks;
	}
	
	public static List<String> FormatDiscord(@NotNull Member member, @NotNull Message message)
	{
		List<Message.Attachment> attachments = message.getAttachments();
		StringBuilder stringBuilder = new StringBuilder();
		
		for (Message.Attachment attachment : attachments)
		{
			stringBuilder.append(attachment.getUrl()).append("\n");
		}
		
		stringBuilder.append(message.getContentDisplay());
		
		return Split(stringBuilder.toString(), 1900);
	}
	
	public static TranslatableText FormatMinecraft(@NotNull Member member, @NotNull Message message, MessagesTransforms messagesTransforms)
	{
		List<Message.Attachment> attachments = message.getAttachments();
		LiteralText minecraftFormattedAttachments = null;
		if (attachments.size() != 0) minecraftFormattedAttachments = new LiteralText("");
		
		for (Message.Attachment attachment : attachments)
		{
			minecraftFormattedAttachments.append(new LiteralText("[" + attachment.getFileName() + "] ").setStyle(Style.EMPTY
							.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText(attachment.getUrl())))
							.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, attachment.getUrl()))
							.withColor(TextColor.fromRgb(0xFF0000))));
		}
		
		LiteralText minecraftText = messagesTransforms.FromString(message.getContentRaw());
		if (minecraftFormattedAttachments != null) minecraftText.getSiblings().add(0, minecraftFormattedAttachments);
		
		LiteralText formattedName = (LiteralText)new LiteralText(member.getEffectiveName()).setStyle(Style.EMPTY
						.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ENTITY, new HoverEvent.EntityContent(EntityType.FISHING_BOBBER, new UUID(0, member.getIdLong()), new LiteralText(member.getEffectiveName()))))
						.withInsertion(member.getAsMention())
						.withColor(TextColor.fromRgb(member.getColorRaw())));
		
		return new TranslatableText(
						"chat.type.text",
						formattedName,
						minecraftText);
	}
}
