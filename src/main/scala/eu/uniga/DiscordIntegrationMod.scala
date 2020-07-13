package eu.uniga

import eu.uniga.config.UnigaConfigurationLoader
import eu.uniga.discord.DiscordBot
import eu.uniga.exceptions.InvalidConfigurationException
import net.fabricmc.api.ModInitializer
import org.slf4j.{Logger, LoggerFactory}

object DiscordIntegrationMod {
  var bot: DiscordBot = _
}

class DiscordIntegrationMod extends ModInitializer {

  private val logger: Logger = LoggerFactory.getLogger("DiscordIntegrationMod")

  override def onInitialize(): Unit = {
    try {
      // Load the bot configuration and setup Discord connection via JDA
      DiscordIntegrationMod.bot = new DiscordBot(UnigaConfigurationLoader.loadConfiguration())
    }
    catch {
      case InvalidConfigurationException(message) => logger.error(message)
    }
  }
}

