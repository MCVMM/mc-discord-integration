package eu.uniga.config

/**
 * Base wrapper around discord integration configuration.
 *
 * @param token Discord API token for running the bot
 */
case class UnigaConfiguration(
                        token: String,
                        channel: Long,
                      ) {
  // Basic conversion to JSON
  def toJson: String =
    s"""
       |{
       |  "token": "$token",
       |  "channel": $channel
       |}
       |""".stripMargin
}
