package eu.uniga.mixins;

import eu.uniga.MessageEvents.Events;
import eu.uniga.MessageEvents.IMinecraftChatMessage;
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
			// Chat message sent by player
			case CHAT:
				// For some reason, this is called multiple times, and proper call have 3 args
				if (args.size() == 3)
				{
					IMinecraftChatMessage chatMessage = Events.GetChatMessageCallback();
					if (chatMessage == null) return;
					
					args.set(0, chatMessage.OnMessageSent((TranslatableText)message, senderUuid));
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