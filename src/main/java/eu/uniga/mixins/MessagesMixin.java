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
				if (args.size() == 3) args.set(0, DiscordIntegrationMod.tmpEmoji().Transform((TranslatableText)message));
				break;
			case SYSTEM:
				break;
			// ?
			case GAME_INFO:
				break;
		}
	}
}