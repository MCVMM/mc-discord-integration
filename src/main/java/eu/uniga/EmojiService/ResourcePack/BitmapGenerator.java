package eu.uniga.EmojiService.ResourcePack;

import eu.uniga.DiscordIntegrationMod;
import net.dv8tion.jda.api.entities.Emote;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

public final class BitmapGenerator
{
	private final int EmoteSize;
	private final Logger _logger = LogManager.getLogger(DiscordIntegrationMod.Name);
	
	private final Collection<Emote> _emotes;
	private final int _bitmapSize;
	
	public BitmapGenerator(int emoteSize, Collection<Emote> emotes)
	{
		_emotes = emotes;
		_bitmapSize = (int)Math.ceil(Math.sqrt(_emotes.size()));
		EmoteSize = emoteSize;
	}
	
	/**
	 * Downloads all emotes, and makes atlas from it
	 * @return Emote atlas as image
	 */
	public BufferedImage GetEmoteBitmapAtlas()
	{
		BufferedImage atlasImage = new BufferedImage(_bitmapSize * EmoteSize, _bitmapSize * EmoteSize, BufferedImage.TYPE_INT_ARGB);
		Graphics graphics = atlasImage.getGraphics();
		int position = 0;
		
		for (Emote emote : _emotes)
		{
			Image emoteImage = GetEmojiImage(emote);
			
			if (emoteImage != null) CopyImage(emoteImage, graphics, position);
			
			position++;
		}
		
		graphics.dispose();
		
		return atlasImage;
	}
	
	/***
	 * Gets all used UTF-16 surrogate pairs in atlas
	 * @return 2D array with UTF-16 surrogate pairs
	 */
	public int[][] GetEmoteCodepointAtlas()
	{
		int codepoint = 0xE000; // PUA block in BMP, 6400 code points, should be enough (That's 104MP image)
		int position = 0;
		int[][] codepointAtlas = new int[_bitmapSize][_bitmapSize];
		
		for (Emote emote : _emotes)
		{
			Point atlasPosition = GetAtlasPosition(position);
			
			codepointAtlas[atlasPosition.y][atlasPosition.x] = ToUtf16(codepoint);
			
			codepoint++;
			position++;
		}
		
		return codepointAtlas;
	}
	
	/***
	 * Gets all used UTF-16 surrogate pairs with translation to short names (may be duplicates)
	 * @return UTF-16 surrogate pairs to short names map
	 */
	public Map<Integer, String> GetSurrogatePairsTranslation()
	{
		Map<Integer, String> translation = new HashMap<>();
		
		int codepoint = 0xE000; // PUA block in BMP, 6400 code points, should be enough (That's 104MP image)
		
		for (Emote emote : _emotes)
		{
			translation.put(ToUtf16(codepoint), emote.getName());
			codepoint++;
		}
		
		return translation;
	}
	
	/***
	 * Gets all used UTF-16 surrogate pairs with translation to short names (may be duplicates)
	 * @return UTF-16 surrogate pairs to short names map
	 */
	public Map<String, Integer> GetEmoteIDsTranslation()
	{
		Map<String, Integer> translation = new HashMap<>();
		
		int codepoint = 0xE000; // PUA block in BMP, 6400 code points, should be enough (That's 104MP image)
		
		for (Emote emote : _emotes)
		{
			translation.put(emote.getAsMention(), ToUtf16(codepoint));
			codepoint++;
		}
		
		return translation;
	}
	
	// Downloads emote image, or null if it failed
	private Image GetEmojiImage(Emote emote)
	{
		Image image;
		
		// Download image
		try
		{
			URL url = new URL(emote.getImageUrl());
			
			URLConnection urlConnection = url.openConnection();
			// Default "Java" user-agent returns HTTP 403
			urlConnection.setRequestProperty("User-Agent", null);
			urlConnection.connect();
			
			InputStream urlStream = urlConnection.getInputStream();
			image = ImageIO.read(urlStream);
		}
		catch (IOException e)
		{
			_logger.warn("Error downloading {} {}", emote.getImageUrl(), e.getLocalizedMessage());
			
			return null;
		}
		
		return image;
	}
	
	// Gets position in atlas (2D array)
	private Point GetAtlasPosition(int position)
	{
		return new Point(position % _bitmapSize, position / _bitmapSize);
	}
	
	// Copies emote image into it's position in atlas
	private void CopyImage(Image source, Graphics destination, int position)
	{
		Point atlasPosition = GetAtlasPosition(position);
		destination.drawImage(source,  atlasPosition.x * EmoteSize, atlasPosition.y * EmoteSize, EmoteSize, EmoteSize, null);
	}
	
	// Converts UTF codepoint into UTF-16 surrogate pair (or one code unit)
	private int ToUtf16(int codepoint)
	{
		// https://stackoverflow.com/questions/6240055/manually-converting-unicode-codepoints-into-utf-8-and-utf-16
		if (codepoint < 0xD800) return codepoint;
		if (codepoint < 0xE000) throw new IllegalArgumentException("Reserved codepoint");
		if (codepoint < 0x10000) return codepoint;
		if (codepoint < 0x110000)
		{
			codepoint = codepoint - 0x10000;
			codepoint = (codepoint & 0b1111111111) | ((codepoint & 0b11111111110000000000) << 6);
			codepoint = codepoint | 0b11011000000000001101110000000000;
			
			return codepoint;
		}
		
		throw new IllegalArgumentException("Out of range codepoint");
	}
}
