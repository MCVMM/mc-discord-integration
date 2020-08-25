package eu.uniga.Discord;

import eu.uniga.Config.Config;
import eu.uniga.NewDiscordIntegrationMod;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.api.events.emote.GenericEmoteEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;
import java.util.ArrayList;
import java.util.List;

public class DiscordBot extends ListenerAdapter
{
	public static abstract class EmojiCallback
	{
		public void OnChannelAdded(@NotNull TextChannel channel) { }
		public void OnChannelRemoved(@NotNull TextChannel channel) { }
		public void OnEmoteChange() { }
	}
	
	public interface IMessageCallback
	{
		void OnMessage(@NotNull Member author, @NotNull Message message);
	}
	
	private final Config _config;
	private final JDA _jda;
	private final List<TextChannel> _channels = new ArrayList<>();
	private final Logger _logger = LogManager.getLogger(NewDiscordIntegrationMod.Name);
	private EmojiCallback _emojiCallback = new EmojiCallback() { };
	private final IMessageCallback _messageCallback;
	
	public DiscordBot(Config config, IMessageCallback messageCallback) throws LoginException, InterruptedException
	{
		_config = config;
		_jda = JDABuilder.createDefault(_config.GetToken()).build().awaitReady();
		_jda.addEventListener(this);
		_messageCallback = messageCallback;
	}
	
	public void Start()
	{
		// Connect to all text channels
		for (long channelId : _config.GetChannels())
		{
			TextChannel textChannel = _jda.getTextChannelById(channelId);
			
			// If channel is not text channel, skip it
			if (textChannel == null)
			{
				_logger.warn("Cannot connect to channel id {}", channelId);
				
				continue;
			}
			
			Thread t = Thread.currentThread();
			synchronized (_channels)
			{
				_channels.add(textChannel);
			}
			_emojiCallback.OnChannelAdded(textChannel);
		}
	}
	
	public void Stop()
	{
		_jda.shutdown();
	}
	
	public JDA GetClient()
	{
		return _jda;
	}
	
	public void SendMessage(String author, String message)
	{
		for (TextChannel textChannel : _channels)
		{
			if (!textChannel.canTalk()) continue;
			
			try
			{
				textChannel.sendMessage("<" + author + "> " + message).queue();
			}
			catch (Exception ignored) { }
		}
	}
	
	public void SetChannelTopic(String topic)
	{
		for (TextChannel textChannel : _channels)
		{
			// Skip channels that we can not manage
			if (!textChannel.getGuild().getSelfMember().getPermissions(textChannel).contains(Permission.MANAGE_CHANNEL)) continue;
			
			try
			{
				textChannel.getManager().setTopic(topic).queue();
			}
			catch (Exception ignored) { }
		}
	}
	
	public void SetEmojiCallback(EmojiCallback emojiCallback)
	{
		_emojiCallback = emojiCallback;
	}
	
	@Override
	public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event)
	{
		super.onGuildMessageReceived(event);
		
		if (event.isWebhookMessage() || event.getAuthor().isBot()) return;
		
		_messageCallback.OnMessage(event.getMember(), event.getMessage());
	}
	
	@Override
	public void onTextChannelDelete(@NotNull TextChannelDeleteEvent event)
	{
		super.onTextChannelDelete(event);
		
		synchronized (_channels)
		{
			_channels.remove(event.getChannel());
		}
		_emojiCallback.OnChannelRemoved(event.getChannel());
	}
	
	@Override
	public void onGenericEmote(@NotNull GenericEmoteEvent event)
	{
		super.onGenericEmote(event);
		
		_emojiCallback.OnEmoteChange();
	}
}
