package eu.uga;

import eu.uga.Config.Config;
import eu.uga.Discord.DiscordBot;
import eu.uga.EmojiService.EmojiService;
import eu.uga.EmojiService.SurrogatePairsDictionary;
import eu.uga.EmojiService.WebServer.SimpleWebServer;
import eu.uga.MessageTransforms.FormattingContext;
import eu.uga.MessageTransforms.MessagesTransforms;
import eu.uga.Utils.Formatting;
import eu.uga.Utils.TickExecuter;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.minecraft.network.MessageType;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class DiscordIntegrationMod implements Runnable, IMinecraftChatHandler, IDiscordHandler, ICustomResourcePackHandler
{
	public static final String Name = "DiscordIntegrationMod";
	private static DiscordIntegrationMod _instance = null;
	
	private final Logger _logger = LogManager.getLogger(Name);
	private final MinecraftServer _minecraftServer;
	private final TickExecuter _tickExecuter;
	private final SurrogatePairsDictionary _dictionary;
	private Config _config;
	private DiscordBot _discordBot;
	private MessagesTransforms _messagesTransforms;
	
	// Synchronization
	private final List<Pair<TranslatableText, UUID>> _enqueuedMinecraftMessages = new ArrayList<>();
	private final List<Pair<Member, Message>> _enqueuedDiscordMessages = new ArrayList<>();
	private boolean _emotesChanged = false;
	private final Object _emotesChangedLock = new Object();
	private Map<String, Integer> _dictionaryUpdate = null;
	
	// Custom emoji
	private SimpleWebServer _webServer;
	private EmojiService _emojiService;
	
	private DiscordIntegrationMod(MinecraftServer minecraftServer)
	{
		_minecraftServer = minecraftServer;
		_tickExecuter = new TickExecuter();
		_dictionary = new SurrogatePairsDictionary();
	}
	
	public static DiscordIntegrationMod CreateInstance(MinecraftServer minecraftServer)
	{
		if (_instance != null) throw new IllegalStateException("Instance already exists");
		
		_instance = new DiscordIntegrationMod(minecraftServer);
		
		return _instance;
	}
	
	public static DiscordIntegrationMod GetInstance()
	{
		if (_instance == null) throw new IllegalStateException("Instance does not exist");
		
		return _instance;
	}
	
	@Override
	public void run()
	{
		Init();
		
		while (!Thread.interrupted())
		{
			if (!Process()) break;
		}
		
		Stop();
	}
	
	// Load config and init all parts of the mod
	private void Init()
	{
		_config = Config.GetConfig();
		
		try
		{
			_discordBot = new DiscordBot(_config, this);
		}
		catch (Exception e)
		{
			_logger.error("Cannot start Discord integration mod", e);
		}
		
		_messagesTransforms = new MessagesTransforms(_dictionary, new FormattingContext(_discordBot.GetClient(), _dictionary));
		
		// Try to start emoji related stuff, if it fails, continue without it
		try
		{
			if (_config.GetCustomEmoji().AreEmotesEnabled()) EnableCustomEmotes();
		}
		catch (Exception e)
		{
			_logger.error("Cannot start custom emoji services: {}", e.getLocalizedMessage());
		}
		
		_tickExecuter.AddToExecuteEveryTick(minecraftServer ->
		{
			// Discord limit once per 10 seconds
			if (minecraftServer.getTicks() % 210 != 0) return;
			
			_discordBot.SetStatusPlayerCount(minecraftServer.getCurrentPlayerCount(), minecraftServer.getMaxPlayerCount());
		});
		
		_tickExecuter.AddToExecuteEveryTick(minecraftServer ->
		{
			// Discord limit twice per 10 minutes
			if (minecraftServer.getTicks() % 6100 != 0) return;
			
			_discordBot.SetChannelTopicPlayerCount(minecraftServer.getPlayerNames(), minecraftServer.getMaxPlayerCount());
		});
		
		_discordBot.Start();
	}
	
	private void Stop()
	{
		_webServer.Stop();
		_emojiService.Stop();
		_discordBot.Stop();
	}
	
	private void EnableCustomEmotes() throws MalformedURLException
	{
		// Start integrated webserver
		_webServer = new SimpleWebServer(_config.GetCustomEmoji().GetWebserverPort(), EmojiService.ResourcePackLocation);
		_webServer.Start();
		
		// Add delegate for reloading resource pack
		_emojiService = new EmojiService(_config.GetCustomEmoji(), this, _discordBot.GetClient());
	}
	
	private boolean Process()
	{
		while (!ShouldProcess())
		{
			synchronized (this)
			{
				try
				{
					wait();
				}
				catch (InterruptedException e)
				{
					return false;
				}
			}
		}
		
		// At this point, at least one of these have at least one thing to do
		ProcessMinecraftMessages();
		ProcessDiscordMessages();
		ProcessDictionary();
		ProcessEmojiChange();
		
		return true;
	}
	
	
	private boolean ShouldProcess()
	{
		synchronized (_enqueuedMinecraftMessages)
		{
			synchronized (_enqueuedDiscordMessages)
			{
				synchronized (_emotesChangedLock)
				{
					return  !_enqueuedMinecraftMessages.isEmpty() ||
									!_enqueuedDiscordMessages.isEmpty() 	||
									_emotesChanged;
				}
			}
		}
	}
	
	private void ProcessMinecraftMessages()
	{
		synchronized (_enqueuedMinecraftMessages)
		{
			synchronized (_dictionary)
			{
				_enqueuedMinecraftMessages.forEach(pair ->
				{
					TranslatableText oldMessage = pair.getLeft();
					UUID uuid = pair.getRight();
					
					TranslatableText newMessage = new TranslatableText("chat.type.text", oldMessage.getArgs()[0], _messagesTransforms.FromString((String)oldMessage.getArgs()[1]));
					
					// Send message to Minecraft on server thread
					_tickExecuter.ExecuteNextTick(minecraftServer ->
					{
						minecraftServer.sendSystemMessage(newMessage, uuid);
						minecraftServer.getPlayerManager().sendToAll(new GameMessageS2CPacket(newMessage, MessageType.CHAT, uuid));
					});
					
					// Send message to each of Discord servers
					_discordBot.SendMessage(((LiteralText)oldMessage.getArgs()[0]).asString(), _messagesTransforms.MinecraftToDiscord((String)oldMessage.getArgs()[1]));
				});
				
				_enqueuedMinecraftMessages.clear();
			}
		}
	}
	
	private void ProcessDiscordMessages()
	{
		synchronized (_enqueuedDiscordMessages)
		{
			synchronized (_dictionary)
			{
				_enqueuedDiscordMessages.forEach(pair ->
				{
					Member member = pair.getLeft();
					Message message = pair.getRight();
					List<String> discordParts = Formatting.FormatDiscord(member, message);
					TranslatableText formattedMessage = Formatting.FormatMinecraft(member, message, _messagesTransforms);
					UUID senderUUID = new UUID(0, member.getIdLong());
					
					// Resend message to other Discord channels, skip original
					for (String part : discordParts)
					{
						_discordBot.SendMessage(member.getEffectiveName(), part, message.getChannel().getIdLong());
					}
					
					// Send message to Minecraft on server thread
					_tickExecuter.ExecuteNextTick(minecraftServer ->
					{
						minecraftServer.sendSystemMessage(formattedMessage, senderUUID);
						minecraftServer.getPlayerManager().sendToAll(new GameMessageS2CPacket(formattedMessage, MessageType.CHAT, senderUUID));
					});
				});
				
				_enqueuedDiscordMessages.clear();
			}
		}
	}
	
	private void ProcessDictionary()
	{
		synchronized (_dictionary)
		{
			if (_dictionaryUpdate == null) return;
			
			_dictionary.Set(_dictionaryUpdate);
			_dictionaryUpdate = null;
		}
	}
	
	private void ProcessEmojiChange()
	{
		synchronized (_emotesChangedLock)
		{
			if (_emojiService == null) return;
			if (_emotesChanged) _emojiService.StartNow();
			_emotesChanged = false;
		}
	}
	
	// Run only on Minecraft server thread
	public void MinecraftTick(MinecraftServer minecraftServer)
	{
		_tickExecuter.RunAll(minecraftServer);
	}
	
	@Override
	public void OnMinecraftMessage(TranslatableText message, UUID sender)
	{
		Pair<TranslatableText, UUID> pair = new Pair<>(message, sender);
		
		synchronized (_enqueuedMinecraftMessages)
		{
			_enqueuedMinecraftMessages.add(pair);
		}
		
		synchronized (this)
		{
			notify();
		}
	}
	
	@Override
	public void OnDiscordMessage(@NotNull Member member, @NotNull Message message)
	{
		if (!_config.GetChannels().contains(message.getChannel().getIdLong())) return;
		Pair<Member, Message> pair = new Pair<>(member, message);
		
		synchronized (_enqueuedDiscordMessages)
		{
			_enqueuedDiscordMessages.add(pair);
		}
		
		synchronized (this)
		{
			notify();
		}
	}
	
	@Override
	public void UpdateDictionary(Map<String, Integer> dictionary)
	{
		synchronized (_dictionary)
		{
			_dictionaryUpdate = dictionary;
		}
		
		synchronized (this)
		{
			notify();
		}
	}
	
	@Override
	public void OnEmojiChange()
	{
		synchronized (_emotesChangedLock)
		{
			_emotesChanged = true;
		}
		
		synchronized (this)
		{
			notify();
		}
	}
	
	@Override
	public void ChangeResourcePack(URL url, String name, String sha1)
	{
		_tickExecuter.ExecuteNextTick(minecraftServer ->
		{
			try
			{
				String fullUrl = (new URL(url, name)).toString();
				
				// Schedule resource pack change to server thread
				_minecraftServer.setResourcePack(fullUrl, sha1);
				_webServer.SetContext(name);
				
				// Force reload of resource pack to all players
				if (_config.GetCustomEmoji().ShouldForceReloadResourcePack())
				{
					_minecraftServer.getPlayerManager().getPlayerList().forEach(player -> player.sendResourcePackUrl(fullUrl, sha1));
				}
			} catch (MalformedURLException ignored) { }
		});
	}
}
