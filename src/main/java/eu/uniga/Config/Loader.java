package eu.uniga.Config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import eu.uniga.NewDiscordIntegrationMod;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.*;
import java.nio.file.Path;

public class Loader
{
	private static final String Location = "uniga-discord-integration2.json";
	private static Config _config;
	private static final Logger _logger = LogManager.getLogger(NewDiscordIntegrationMod.Name);
	
	static Config GetConfig()
	{
		if (_config == null) Load();
		
		return _config;
	}
	
	static Config ReloadConfig()
	{
		Load();
		
		return _config;
	}
	
	private static void Load()
	{
		Path absolutePath = FabricLoader.getInstance().getConfigDir().resolve(Location);
		Gson json = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();
		Reader reader = null;
		
		// Try to read config file
		try
		{
			reader = new BufferedReader(new FileReader(absolutePath.toFile()));
			_config = json.fromJson(reader, Config.class);
			reader.close();
			
			if (_config == null) throw new IOException("Json is corrupted");
		}
		// If it fails, set config to default and try to write it
		catch (Exception e)
		{
			_logger.warn("UniGa config file not found or corrupted, creating default");
			
			try
			{
				WriteDefault();
			}
			catch (Exception f)
			{
				_logger.error("Cannot write UniGa config file to: " + absolutePath.toString());
			}
		}
		
		// Now we have loaded config or default it it failed
	}
	
	private static void WriteDefault() throws IOException
	{
		_config = Config.GetDefault();
		
		Save();
	}
	
	private static void Save() throws IOException
	{
		Path absolutePath = FabricLoader.getInstance().getConfigDir().resolve(Location);
		Gson json = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(absolutePath.toFile(), false));
		
		json.toJson(_config, Config.class, writer);
		writer.close();
	}
}
