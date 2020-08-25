package eu.uniga;

import eu.uniga.Config.Config;
import eu.uniga.Discord.DiscordBot;
import eu.uniga.Discord.DiscordMessagesHandler;
import eu.uniga.EmojiService.EmojiService;
import eu.uniga.MessageTransforms.FormattingContext;
import eu.uniga.EmojiService.SurrogatePairsDictionary;
import eu.uniga.MessageTransforms.MessagesTransforms;
import eu.uniga.Utils.TickExecuter;
import eu.uniga.EmojiService.WebServer.SimpleWebServer;
import eu.uniga.MessageEvents.Events;
import eu.uniga.MessageEvents.IMinecraftChatMessage;
import net.dv8tion.jda.api.entities.TextChannel;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

public class NewDiscordIntegrationMod implements ModInitializer, IMinecraftChatMessage
{
	public static final String Name = "DiscordIntegrationMod";
	private final Logger _logger = LogManager.getLogger(Name);
	private MinecraftServer _minecraftServer;
	private final TickExecuter _tickExecuter = new TickExecuter();
	private DiscordBot _discordBot;
	private SimpleWebServer _webServer;
	private SurrogatePairsDictionary _dictionary;
	private MessagesTransforms _messagesTransforms;
	private EmojiService _emojiService;
	
	@Override
	public void onInitialize()
	{
		ServerLifecycleEvents.SERVER_STARTING.register(this::Start);
		ServerLifecycleEvents.SERVER_STOPPED.register(this::Stop);
		ServerTickEvents.START_SERVER_TICK.register(this::Tick);
	}
	
	private void Start(MinecraftServer minecraftServer)
	{
		// Currently does nothing
		if (minecraftServer == null) return;
		
		_minecraftServer = minecraftServer;
		
		try
		{
			// Start translation dictionary
			_dictionary = new SurrogatePairsDictionary();
			
			// Load the bot configuration and setup Discord connection via JDA
			_discordBot = new DiscordBot(Config.GetConfig());
			
			// Create new message transforms
			_messagesTransforms = new MessagesTransforms(_dictionary, new FormattingContext(_discordBot.GetClient(), _dictionary));
			
			// Add messages callback
			_discordBot.SetMessageHandler(new DiscordMessagesHandler(_minecraftServer, _discordBot, _tickExecuter, _messagesTransforms));
			
			// Try to start emoji related stuff, if it fails, continue without it
			try
			{
				if (Config.GetConfig().GetCustomEmoji().AreEmotesEnabled()) EnableCustomEmotes();
			}
			catch (Exception e)
			{
				_logger.error("Cannot start custom emoji services: {}", e.getLocalizedMessage());
			}
			
			// Register Minecraft chat messages callback
			Events.SetMinecraftChatMessageCallback(this);
			
			// Start bot (starts adding channels)
			_discordBot.Start();
		}
		catch (Exception e)
		{
			_logger.error("Cannot start Discord integration mod", e);
		}
	}
	
	private void EnableCustomEmotes() throws IOException
	{
		// Start integrated webserver
		_webServer = new SimpleWebServer(Config.GetConfig().GetCustomEmoji().GetWebserverPort(), EmojiService.ResourcePackLocation);
		_webServer.Start();
		
		// Add delegate for registering new channels on the fly
		_emojiService = new EmojiService(Config.GetConfig().GetCustomEmoji().GetSize(), new EmojiService.IResourcePackReloadable()
		{
			// **Callback from emote thread**
			@Override
			public void Reload(String url, String sha1)
			{
				_tickExecuter.ExecuteNextTick(() -> ChangeResourcePack(url, sha1));
			}
			
			// **Callback from emote thread**
			@Override
			public void UpdateDictionary(Map<String, Integer> dictionary)
			{
				// Emotes changed, swap dictionary, on server thread
				_tickExecuter.ExecuteNextTick(() -> _dictionary.Set(dictionary));
			}
		});
		
		// Add delegate, on channel added register channel to emoji service
		_discordBot.SetEmojiCallback(new DiscordBot.EmojiCallback()
		{
			@Override
			public void OnChannelAdded(@NotNull TextChannel channel)
			{
				_emojiService.AddGuild(channel.getGuild());
			}
			
			// **Callback from JDA thread**
			@Override
			public void OnChannelRemoved(@NotNull TextChannel channel)
			{
				_emojiService.RemoveGuild(channel.getGuild());
			}
			
			// **Callback from JDA thread**
			@Override
			public void OnEmoteChange()
			{
				// Probably thread safe
				_emojiService.StartNow();
			}
		});
		
		// TODO: make on event
		_emojiService.Start(30 * 60 * 1000);
	}
	
	private void Stop(MinecraftServer minecraftServer)
	{
		if (_webServer != null) _webServer.Stop();
		if (_emojiService != null) _emojiService.Stop();
		_discordBot.Stop();
	}
	
	private void Tick(MinecraftServer minecraftServer)
	{
		_tickExecuter.RunAll();
	}
	
	private void ChangeResourcePack(String url, String sha1)
	{
		// Schedule resource pack change to server thread
		_minecraftServer.setResourcePack(url, sha1);
		String fileName = url.substring(url.lastIndexOf('/'));
		_webServer.SetContext(fileName);
		
		// Force reload of resource pack to all players
		if (Config.GetConfig().GetCustomEmoji().ShouldForceReloadResourcePack())
		{
			_minecraftServer.getPlayerManager().getPlayerList().forEach(player -> player.sendResourcePackUrl(url, sha1));
		}
	}
	
	@Override
	public TranslatableText OnMessageSent(TranslatableText message, UUID sender)
	{
		_discordBot.SendMessage(((LiteralText)message.getArgs()[0]).asString(), _messagesTransforms.MinecraftToDiscord((String)message.getArgs()[1]));
		return new TranslatableText("chat.type.text", message.getArgs()[0], _messagesTransforms.FromString((String)message.getArgs()[1]));
	}
}
