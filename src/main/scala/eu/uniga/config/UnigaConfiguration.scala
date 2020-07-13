package eu.uniga.config

/**
 * Base wrapper around discord integration configuration.
 *
 * @param token Discord API token for running the bot
 * @param chatChannelId ID of the discord channel, that should receive in-game messages from Minecraft
 * @param statusChannelId ID of the discord channel, which name should be updated to server status
 */
case class UnigaConfiguration(
                        token: String,
                        chatChannelId: Long,
                        statusChannelId: Long,
                      ) {
  // Basic conversion to JSON
  def toJson: String =
    s"""
       |{
       |  "token": "${token}",
       |  "chatChannelId": ${chatChannelId},
       |  "statusChannelId": ${statusChannelId}
       |}
       |""".stripMargin
}
