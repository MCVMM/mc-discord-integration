package eu.uniga.discord

import java.util.UUID
import java.util.stream.Collectors

import eu.uniga.DiscordIntegrationMod
import net.dv8tion.jda.api.entities.{TextChannel, VoiceChannel}
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.fabricmc.fabric.api.event.server.{ServerStartCallback, ServerStopCallback, ServerTickCallback}
import net.minecraft.network.MessageType
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket
import net.minecraft.server.MinecraftServer
import net.minecraft.text.{LiteralText, Style, TranslatableText}

import scala.collection.mutable

class DiscordMessageEventListener(private val channel: TextChannel) extends ListenerAdapter {

  // Set containing all messages that has been sent since the last dispatch
  private val messagesToDispatch = mutable.Set[String]()

  ServerTickCallback.EVENT.register((server: MinecraftServer) => {
    val manager = server.getPlayerManager

    // Handle every 20 ticks (~= 1s)
    if (server.getTicks % 20 == 0) {
      if (messagesToDispatch.nonEmpty) {
        // Send all queued messages
        for (message <- messagesToDispatch) {
          // TODO: proper name and message separation
          // TODO: message attachments
          //val text = DiscordIntegrationMod.transforms.DiscordToMinecraft(new TranslatableText("chat.type.text", message.split(": ")(0), message.split(": ")(1)))
          val text = new TranslatableText("chat.type.text", message.split(": ")(0), DiscordIntegrationMod.transforms.FromString("\n" + message.split(": ")(1)))
          val packet = new GameMessageS2CPacket(
            text.setStyle(Style.EMPTY),
            MessageType.CHAT,
            UUID.randomUUID()
          )

          manager.sendToAll(packet)
        }

        // Clear the queue
        messagesToDispatch.clear()
      }
    }

    // There seems to be some issue with renaming the channel so often
    // Maybe some rate limiting? (But the Discord documentation states 10000 events / minute)
    // TODO: Resolve this and remove the conditional terminator
    // Maybe change the bot's activity, dunno
    if (false) {

      // Every 200 ticks (~ 10s) update the channel title
      if (server.getTicks % 200 == 0) {
        // Change the status channel name to the players count
        val name = s"${manager.getCurrentPlayerCount}-of-${server.getMaxPlayerCount}-players-online"
        val players = manager.getPlayerList
          .stream
          .map(_.getDisplayName.getString)
          .collect(Collectors.joining(","))

        channel.getManager
          .setName(name)
          .setTopic(players)
          .queue()
      }
    }
  })

  override def onGuildMessageReceived(event: GuildMessageReceivedEvent): Unit = {
    // Only handle event in the chat channel and not sent by bots
    if (!shouldHandle(event)) return

    val sender = event.getAuthor.getAsTag
    val content = event.getMessage.getContentRaw

    // TODO: message formatting, emoji resolving?
    messagesToDispatch.add(s"$sender: $content")
  }

  private def shouldHandle(event: GuildMessageReceivedEvent): Boolean =
    event.getChannel.getId == channel.getId && !event.getAuthor.isBot

}
