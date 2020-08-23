package eu.uniga.mixins;

import eu.uniga.DiscordIntegrationMod;
import net.minecraft.network.MessageType;
import net.minecraft.server.PlayerManager;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.UUID;

@Mixin(PlayerManager.class)
public class MessagesMixin
{
	@ModifyArgs(at = @At("INVOKE"), method = "broadcastChatMessage")
	private void broadcastChatMessage(Args args, Text message, MessageType type, UUID senderUuid) {
		switch (type)
		{
			case CHAT:
				// For some reason, this is called multiple times, and proper call have 3 args
				if (args.size() == 3)
				{
					DiscordIntegrationMod.bot().sendChatMessage(DiscordIntegrationMod.transforms().MinecraftToDiscord(message.getString()));
					args.set(0, DiscordIntegrationMod.transforms().MinecraftToMinecraft((TranslatableText)message));
				}
				break;
			case SYSTEM:
				break;
			// Not used?
			case GAME_INFO:
				break;
		}
	}
}