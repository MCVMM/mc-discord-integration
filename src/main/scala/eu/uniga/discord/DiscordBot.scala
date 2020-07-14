package eu.uniga.discord

import eu.uniga.config.UnigaConfiguration
import eu.uniga.exceptions.InvalidConfigurationException
import net.dv8tion.jda.api.{JDABuilder, Permission}
import net.dv8tion.jda.api.entities.{TextChannel, VoiceChannel}

class DiscordBot(configuration: UnigaConfiguration) {

  private var channel: TextChannel = _

  // The discord client used to connect to Uniga and repost in-game message as well as update the server status
  private val client = JDABuilder.createDefault(configuration.token).build().awaitReady()

  {
    // Bind desired channels based on the provided configuration
    channel = client.getTextChannelById(configuration.channel)

    if (!validateChannels)
      throw InvalidConfigurationException(
        """
          |Invalid channel configuration.
          |Check whether the id is configured correctly and the bot can manage,
          |read and send messages to the channel.""".stripMargin)

    // Register the message event listener, that redirects the messages from Discord to Minecraft
    client.addEventListener(new DiscordMessageEventListener(channel))
  }
  // Send MC message to the channel
  def sendChatMessage(message: String): Unit =
    if (!message.isEmpty) channel.sendMessage(message).queue()

  private def validateChannels: Boolean =
    channel.isInstanceOf[TextChannel] &&
    channel.getGuild.isMember(client.getSelfUser) &&
    // Bot can send messages
    channel.canTalk &&
    // Bot can change the channel name
    channel.getGuild
      .getMember(client.getSelfUser)
      .getPermissions(channel)
      .contains(Permission.MANAGE_CHANNEL)
}