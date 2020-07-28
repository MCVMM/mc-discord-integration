package eu.uniga.EmojiService.ResourcePack;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public final class Zip
{
	private final Logger _logger = LogManager.getLogger();
	private final ZipOutputStream _stream;
	
	public Zip() throws IOException
	{
		Path tmpFile = Paths.get(EmojiService.ResourcePackLocation.toString() + ".tmp");
		
		
		OutputStream stream = Files.newOutputStream(tmpFile, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
		_stream = new ZipOutputStream(stream);
	}
	
	public void Add(String path, byte[] bytes) throws IOException
	{
		ZipEntry zipEntry = new ZipEntry(path);
		_stream.putNextEntry(zipEntry);
		_stream.write(bytes);
		_stream.closeEntry();
	}
	
	public void Finish() throws IOException
	{
		if (_stream != null) _stream.close();
		else _logger.warn("Closing already closed ZIP");
		
		if (Files.exists(EmojiService.ResourcePackLocation)) Files.delete(EmojiService.ResourcePackLocation);
		if (!new File(EmojiService.ResourcePackLocation.toString() + ".tmp").
						renameTo(new File(EmojiService.ResourcePackLocation.toString()))) throw new IOException("Rename failed");
	}
	
	public String GetSha1()
	{
		String hash = "";
		
		try
		{
			byte[] array = Files.readAllBytes(EmojiService.ResourcePackLocation);
			
			try
			{
				hash = SHAsum(array);
			}
			catch (NoSuchAlgorithmException e)
			{
				_logger.warn("SHA-1 does not exist", e);
			}
		}
		catch (IOException e)
		{
			_logger.warn("Cannot read resource pack and make SHA-1", e);
		}
		
		return hash;
	}
	
	private static String SHAsum(byte[] convertme) throws NoSuchAlgorithmException
	{
		MessageDigest md = MessageDigest.getInstance("SHA-1");
		return byteArray2Hex(md.digest(convertme));
	}
	
	private static String byteArray2Hex(final byte[] hash)
	{
		Formatter formatter = new Formatter();
		for (byte b : hash)
		{
			formatter.format("%02x", b);
		}
		return formatter.toString();
	}
}
