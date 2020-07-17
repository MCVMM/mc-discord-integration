package eu.uniga.EmojiService.ResourcePack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public final class Zip
{
	private final Logger _logger = LoggerFactory.getLogger(this.getClass().getName());
	private final ZipOutputStream _stream;
	
	public Zip() throws IOException
	{
		Path tmpFile = Paths.get(Service.ResourcePackLocation.toString() + ".tmp");
		
		OutputStream stream = Files.newOutputStream(tmpFile, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
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
		
		Files.delete(Service.ResourcePackLocation);
		if (!new File(Service.ResourcePackLocation.toString() + ".tmp").
						renameTo(new File(Service.ResourcePackLocation.toString()))) throw new IOException("Rename failed");
	}
}
