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

  // TODO: do a proper formatting based on the text class (as Text is general interface implemented by a shit ton of concrete classes in MC)
  private def format(text: Text): String = text match {
    // If it is instance of TranslatableText, prepend emoji based on the translation key
    case translatable: TranslatableText => (messageEmoji(translatable.getKey) + " " + text.getString).trim
    // Otherwise just return the text
    case _ => text.getString
  }

  // https://gist.github.com/jirkavrba/c21f987a6b2ce5631c111e4cec204c1b
  private def messageEmoji(key: String): String = Map(
      "chat.type.advancement" -> "\uD83C\uDFC6", // :trophy:
      "multiplayer.player.joined" -> "➡️", // :arrow_right:
      "multiplayer.player.left" -> "⬅️", // :arrow_left:
      "death" -> "\uD83D\uDC80", // :skull:
  )
    // Find the first key that is the prefix of the message
    .find(entry =>  key.startsWith(entry._1))
    // Map it to the emoji
    .map(entry => entry._2)
    // Or return empty string if not found
    .getOrElse("")
}
