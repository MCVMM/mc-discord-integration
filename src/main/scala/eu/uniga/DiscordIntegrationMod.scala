package eu.uniga

import java.util

import eu.uniga.EmojiService.ResourcePack.EmojiService
import eu.uniga.EmojiService.ResourcePack.EmojiService.IResourcePackReloadable
import eu.uniga.MessageTransforms.MinecraftToMinecraft.EmojiTransform
import eu.uniga.MessageTransforms.SurrogatePairsDictionary
import eu.uniga.Utils.TickExecuter
import eu.uniga.Web.SimpleWebServer
import eu.uniga.config.UnigaConfigurationLoader
import eu.uniga.discord.DiscordBot
import eu.uniga.exceptions.InvalidConfigurationException
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.{ServerLifecycleEvents, ServerTickEvents}
import net.minecraft.server.MinecraftServer
import org.slf4j.{Logger, LoggerFactory}

object DiscordIntegrationMod {
  var bot: DiscordBot = _
  var webServer : SimpleWebServer = _
  var emojiService : EmojiService = _
  var minecraftServer : MinecraftServer = _
  var dictionary : SurrogatePairsDictionary = _
  var tmpEmoji : EmojiTransform = _
  var tmpEmoji2 : eu.uniga.MessageTransforms.DiscordToMinecraft.EmojiTransform = _
}

class DiscordIntegrationMod extends ModInitializer {

  private val logger: Logger = LoggerFactory.getLogger("DiscordIntegrationMod")
  private val tickExecuter: TickExecuter = new TickExecuter()

  override def onInitialize(): Unit = {
    ServerLifecycleEvents.SERVER_STARTING.register(this.start)
    ServerLifecycleEvents.SERVER_STOPPING.register(this.stop)
    ServerTickEvents.START_SERVER_TICK.register(this.onTick)
  }

  def start(minecraftServer: MinecraftServer): Unit = {
    if (minecraftServer == null) return

    DiscordIntegrationMod.minecraftServer = minecraftServer

    try {
      // Load the bot configuration and setup Discord connection via JDA
      DiscordIntegrationMod.bot = new DiscordBot(UnigaConfigurationLoader.loadConfiguration())
    }
    catch {
      case InvalidConfigurationException(message) => logger.error(message)
    }

    // Start integrated webserver
    DiscordIntegrationMod.webServer = new SimpleWebServer(80, EmojiService.ResourcePackLocation)
    DiscordIntegrationMod.webServer.Start()

    // Start translation dictionary
    DiscordIntegrationMod.dictionary = new SurrogatePairsDictionary()
    DiscordIntegrationMod.tmpEmoji = new EmojiTransform(DiscordIntegrationMod.dictionary)
    DiscordIntegrationMod.tmpEmoji2 = new eu.uniga.MessageTransforms.DiscordToMinecraft.EmojiTransform(DiscordIntegrationMod.dictionary)

    // Add delegate for registering new channels on the fly
    DiscordIntegrationMod.emojiService = new EmojiService(new IResourcePackReloadable {
      override def Reload(url: String, sha1: String): Unit = changeResourcePack(url, sha1)

      override def UpdateDictionary(dictionary: util.Map[String, Integer]): Unit = updateDictionary(dictionary)
    })
    DiscordIntegrationMod.bot.addDelegate(DiscordIntegrationMod.emojiService.AddChannel)
    DiscordIntegrationMod.emojiService.Start(30 * 1000)

    // Start bot (which will add channel)
    DiscordIntegrationMod.bot.start()
  }

  def stop(minecraftServer: MinecraftServer) : Unit = {
    DiscordIntegrationMod.webServer.Stop()
    DiscordIntegrationMod.emojiService.Stop()
  }

  private def onTick(minecraftServer: MinecraftServer) : Unit = {
    tickExecuter.RunAll()
  }

  private def changeResourcePack(url: String, sha1: String): Unit = {
    tickExecuter.Add(() => {
      DiscordIntegrationMod.minecraftServer.setResourcePack(url, sha1)
      val fileName = url.substring(url.lastIndexOf('/'))
      DiscordIntegrationMod.webServer.SetContext(fileName)
      DiscordIntegrationMod.minecraftServer.getPlayerManager.getPlayerList.forEach(player => player.sendResourcePackUrl(url, sha1))
    } )
  }

  private def updateDictionary(dictionary: util.Map[String, Integer]): Unit = {
    DiscordIntegrationMod.dictionary.Set(dictionary)
  }
}

