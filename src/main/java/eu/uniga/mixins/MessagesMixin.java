package eu.uniga.mixins;

import eu.uniga.DiscordIntegrationMod;
import eu.uniga.IMinecraftChatHandler;
import net.minecraft.network.MessageType;
import net.minecraft.server.PlayerManager;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(PlayerManager.class)
public class MessagesMixin
{
	private final IMinecraftChatHandler _messageHandler = DiscordIntegrationMod.GetInstance();
	
	@Inject(method = "broadcastChatMessage", at = @At("HEAD"), cancellable = true)
	private void injectMethod(Text message, MessageType type, UUID senderUuid, CallbackInfo info)
	{
		switch (type)
		{
			// Chat message sent by player
			case CHAT:
				_messageHandler.OnMinecraftMessage((TranslatableText)message, senderUuid);
				
				// Cancel execution and handle the message asynchronously
				info.cancel();
				break;
			case SYSTEM:
				break;
			// Not used?
			case GAME_INFO:
				break;
		}
	}
	/*
	@ModifyArgs(at = @At("INVOKE"), method = "broadcastChatMessage")
	private void broadcastChatMessage(Args args, Text message, MessageType type, UUID senderUuid) {
	
	}*/
}