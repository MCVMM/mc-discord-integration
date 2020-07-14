package eu.uniga.mixins

import java.util.UUID

import eu.uniga.DiscordIntegrationMod
import net.minecraft.server.MinecraftServer
import net.minecraft.text.{Text, TranslatableText}
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import org.spongepowered.asm.mixin.injection.{At, Inject}

@Mixin(Array(classOf[MinecraftServer]))
class MinecraftServerMixin {

  @Inject(at = Array(new At("RETURN")), method = Array("sendSystemMessage"))
  def sendMessage(text: Text, id: UUID, callback: CallbackInfo): Unit = {

    // Format the message (from translatable text eg.) and send it to the Discord channel
    DiscordIntegrationMod.bot.sendChatMessage(format(text))
  }

  private def format(text: Text): String =
    // TODO: do a proper formatting based on the text class (as Text is general interface implemented by a shit ton of concrete classes in MC)
    text.getString
}
