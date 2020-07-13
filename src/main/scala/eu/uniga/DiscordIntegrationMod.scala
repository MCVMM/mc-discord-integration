package eu.uniga

import eu.uniga.config.UnigaConfigurationLoader
import eu.uniga.discord.DiscordBot
import eu.uniga.exceptions.InvalidConfigurationException
import net.fabricmc.api.ModInitializer
import org.slf4j.{Logger, LoggerFactory}

class DiscordIntegrationMod extends ModInitializer {

  private lazy val logger: Logger = LoggerFactory.getLogger("DiscordIntegrationMod")

  override def onInitialize(): Unit = {
    try {
      val configuration = UnigaConfigurationLoader.loadConfiguration()
      val bot = new DiscordBot(configuration)
    }
    catch {
      case InvalidConfigurationException(message) => logger.error(message)
    }
  }

}
