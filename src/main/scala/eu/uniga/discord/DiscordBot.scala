package eu.uniga.discord

import eu.uniga.config.UnigaConfiguration
import eu.uniga.exceptions.InvalidConfigurationException
import net.dv8tion.jda.api.{JDABuilder, Permission}
import net.dv8tion.jda.api.entities.{TextChannel, VoiceChannel}

class DiscordBot(configuration: UnigaConfiguration) {

  private var chatChannel: TextChannel = _

  // The status channel, which name is used to represent the current server status
  private var statusChannel: VoiceChannel = _

  // The discord client used to connect to Uniga and repost in-game message as well as update the server status
  private val client = JDABuilder.createDefault(configuration.token).build().awaitReady()

  {
    // Bind desired channels based on the provided configuration
    chatChannel = client.getTextChannelById(configuration.chatChannelId)
    statusChannel = client.getVoiceChannelById(configuration.statusChannelId)

    if (!validateChannels)
      throw InvalidConfigurationException(
        """
          |Invalid channels configuration.
          |Check whether the ids are configured correctly and the bot can manage the status channel,
          |message the chat channel.""".stripMargin)
  }

  // Change the status channel's name to the provided parameter
  def setStatusChannelName(name: String): Unit =
    statusChannel.getManager.setName(name).queue()

  // Send MC message to the chat channel
  def sendChatMessage(message: String): Unit =
    chatChannel.sendMessage(message).queue()

  private def validateChannels: Boolean =
    chatChannel.isInstanceOf[TextChannel] &&
    statusChannel.isInstanceOf[VoiceChannel] &&
    // Are both channels in the guild that the bot is in?
    chatChannel.getGuild.isMember(client.getSelfUser) &&
    statusChannel.getGuild.isMember(client.getSelfUser) &&
    // Validate required permissions
    chatChannel.canTalk &&
    // Bot can change the status channel name
    statusChannel.getGuild
      .getMember(client.getSelfUser)
      .getPermissions(statusChannel)
      .contains(Permission.MANAGE_CHANNEL)
}