package eu.uniga.EmojiService.WebServer;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import eu.uniga.EmojiService.EmojiService;
import eu.uniga.NewDiscordIntegrationMod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;

public final class SimpleWebServer
{
	private HttpServer _server;
	private final Logger _logger = LogManager.getLogger(NewDiscordIntegrationMod.Name);
	private String _context;
	private Path _file;
	private HttpContext _httpContext;
	private Handler _handler;
	
	public SimpleWebServer(int port, Path file) throws IOException
	{
		_file = file;
		
		try
		{
			_server = HttpServer.create(new InetSocketAddress(port), 0);
		}
		catch (Exception e)
		{
			_logger.warn("Cannot start resource pack server: {}", e.getLocalizedMessage());
		}
		
		_handler = new Handler();
		_server.setExecutor(null);
	}
	
	public void Start()
	{
		_server.start();
	}
	
	public void Stop()
	{
		_server.stop(5);
	}
	
	public void SetContext(String context)
	{
		_context = context;
		HttpContext httpContext = _server.createContext(_context, _handler);
		if (_httpContext != null) _server.removeContext(_httpContext);
		_httpContext = httpContext;
	}
	
	class Handler implements HttpHandler
	{
		private final Logger _logger = LogManager.getLogger(NewDiscordIntegrationMod.Name);
		
		@Override
		public void handle(HttpExchange exchange)
		{
			if (!Files.exists(EmojiService.ResourcePackLocation))
			{
				try
				{
					exchange.sendResponseHeaders(404, 0);
					exchange.getResponseBody().close();
				} catch (IOException e)
				{
					_logger.warn("Connection error: {}", e.getLocalizedMessage());
				}
				
				return;
			}
			
			OutputStream outputStream = exchange.getResponseBody();
			
			try
			{
				exchange.sendResponseHeaders(200, Files.size(_file));
				Files.copy(EmojiService.ResourcePackLocation, outputStream);
			}
			catch (IOException e)
			{
				_logger.warn("Error reading resource pack: {}", e.getLocalizedMessage());
			}
			
			try
			{
				outputStream.close();
			}
			catch (IOException e)
			{
				_logger.warn("Connection error: {}", e.getLocalizedMessage());
			}
		}
	}
}
