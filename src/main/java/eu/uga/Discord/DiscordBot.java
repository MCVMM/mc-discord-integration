package eu.uga.Discord;

import eu.uga.Config.Config;
import eu.uga.DiscordIntegrationMod;
import eu.uga.IDiscordHandler;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.api.events.emote.GenericEmoteEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;
import java.util.HashSet;
import java.util.Set;

public class DiscordBot extends ListenerAdapter
{
	private final Config _config;
	private final JDA _jda;
	private final Set<TextChannel> _channels = new HashSet<>();
	private final Logger _logger = LogManager.getLogger(DiscordIntegrationMod.Name);
	private final IDiscordHandler _discordHandler;
	
	public DiscordBot(Config config, IDiscordHandler discordHandler) throws LoginException, InterruptedException
	{
		_config = config;
		_discordHandler = discordHandler;
		_jda = JDABuilder.createDefault(_config.GetToken()).build().awaitReady();
		_jda.addEventListener(this);
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
			
			synchronized (_channels)
			{
				_channels.add(textChannel);
			}
		}
		
		// Signal change of emote
		_discordHandler.OnEmojiChange();
	}
	
	public void Stop()
	{
		_jda.shutdown();
	}
	
	public JDA GetClient()
	{
		return _jda;
	}
	
	public void SendMessage(String author, String message, long skip)
	{
		synchronized (_channels)
		{
			for (TextChannel textChannel : _channels)
			{
				if (!textChannel.canTalk()) continue;
				if (textChannel.getIdLong() == skip) continue;
				
				try
				{
					textChannel.sendMessage("<" + author + "> " + message).queue();
				} catch (Exception ignored) { }
			}
		}
	}
	
	public void SendMessage(String author, String message)
	{
		SendMessage(author, message, 0);
	}
	
	public void SetChannelTopicPlayerCount(String[] current, int max)
	{
		StringBuilder topicBuilder = new StringBuilder();
		
		topicBuilder.append(String.format("**Minecraft server: %d/%d players -** \n", current.length, max));
		
		for (String player : current)
		{
			topicBuilder.append(player).append("\n");
		}
		
		String topic = topicBuilder.substring(0, Math.min(topicBuilder.length(), 1023));
		
		synchronized (_channels)
		{
			for (TextChannel textChannel : _channels)
			{
				// Skip channels that we can not manage
				if (!textChannel.getGuild().getSelfMember().getPermissions(textChannel).contains(Permission.MANAGE_CHANNEL)) continue;
				
				
				try
				{
					textChannel.getManager().setTopic(topic).queue();
				} catch (Exception ignored) { }
			}
		}
	}
	
	public void SetStatusPlayerCount(int current, int max)
	{
		String text = String.format("Minecraft server: %d/%d players", current, max);
		
		_jda.getPresence().setActivity(Activity.listening(text));
	}
	
	@Override
	public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event)
	{
		if (event.isWebhookMessage() || event.getAuthor().isBot()) return;
		
		_discordHandler.OnDiscordMessage(event.getMember(), event.getMessage());
	}
	
	@Override
	public void onTextChannelDelete(@NotNull TextChannelDeleteEvent event)
	{
		synchronized (_channels)
		{
			_channels.remove(event.getChannel());
		}
		
		_discordHandler.OnEmojiChange();
	}
	
	@Override
	public void onGenericEmote(@NotNull GenericEmoteEvent event)
	{
		_discordHandler.OnEmojiChange();
	}
}
