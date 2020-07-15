package eu.uniga.EmojiService;

import java.util.*;

import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Guild;

public class EmojiStore
{
	private final Object _emotesLock = new Object();
	private final Object _serversLock = new Object();
	private final Set<Guild> _servers;
	private final Set<Emote> _emotes;
	private final Timer _timer;
	
	public EmojiStore()
	{
		_servers = new HashSet<>();
		_emotes = new HashSet<>();
		_timer = new Timer(true);
	}
	
	public void AddServer(Guild server)
	{
		_servers.add(server);
	}
	
	public void Start()
	{
		// TODO: Move period to config
		//_timer.scheduleAtFixedRate(new Grabber(), 0, 2 * 60 * 60* 1000);
		_timer.scheduleAtFixedRate(new Grabber(), 0, 10 * 1000);
	}
	
	public void Stop()
	{
		_timer.cancel();
		_timer.purge();
	}
	
	
	public class Grabber extends TimerTask
	{
		@Override
		public void run()
		{
			// TODO: on emoji remove
			_emotes.clear();
			int added = GetEmotes();
			if (added == 0) return;
			
			
		}
		
		private int GetEmotes()
		{
			int added = 0;
			
			synchronized (_serversLock)
			{
				for (Guild server : _servers)
				{
					added += GetEmotes(server);
				}
			}
			
			return added;
		}
		
		private int GetEmotes(Guild server)
		{
			List<Emote> emotes = server.getEmotes();
			int added = 0;
			
			synchronized (_emotesLock)
			{
				for (Emote emote : emotes)
				{
					added += _emotes.add(emote) ? 1 : 0;
				}
			}
			
			return added;
		}
	}
}