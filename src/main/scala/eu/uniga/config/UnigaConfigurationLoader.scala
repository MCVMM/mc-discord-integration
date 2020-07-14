package eu.uniga.config

import java.io.{BufferedWriter, File, FileWriter}

import com.typesafe.config.ConfigFactory
import net.fabricmc.loader.api.FabricLoader
import org.slf4j.{Logger, LoggerFactory}

object UnigaConfigurationLoader {

  private val logger: Logger = LoggerFactory.getLogger("UnigaConfigurationLoader")

  private val config: File = new File(
    FabricLoader.getInstance.getConfigDirectory,
    "/uniga-discord-integration.json"
  )

  /**
   * Load the configuration, if the configuration file is not present create a default one
   */
  def loadConfiguration(): UnigaConfiguration =
    if (config.exists) parseConfiguration() else createDefaultConfiguration()


  private def parseConfiguration(): UnigaConfiguration = {
    val source = ConfigFactory.parseFile(config)
    try {
      return UnigaConfiguration(
        source.getString("token"),
        source.getLong("channel"),
      )
    }
    // Silently ignore exceptions as they can only be from an invalid configuration file which is the fallback
    // noinspection DangerousCatchAll
    catch { case _: Throwable => }

    logger.error(s"Invalid plugin configuration found. Check the ${config.getAbsolutePath} against example in README.md. Using default configuration.")
    createDefaultConfiguration()
  }

  private def createDefaultConfiguration(): UnigaConfiguration = {

    logger.warn(s"Using default configuration. Either ${config.getAbsolutePath} is missing or contains invalid content.")

    val default = UnigaConfiguration("Discord token from https://discord.com/developers/applications", 0L)

    // Write the default configuration so it can be easily modified.
    if (!config.exists) {
      val writer = new BufferedWriter(new FileWriter(config))

      writer.write(default.toJson)
      writer.close()
    }

    default
  }
}
