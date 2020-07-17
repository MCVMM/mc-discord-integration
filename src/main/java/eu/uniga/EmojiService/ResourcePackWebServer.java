package eu.uniga.EmojiService;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.file.Files;

public class ResourcePackWebServer
{
	private HttpServer _server;
	private final Logger _logger = LoggerFactory.getLogger(this.getClass().getName());
	
	public ResourcePackWebServer(int port)
	{
		try
		{
			_server = HttpServer.create(new InetSocketAddress(port), 0);
		}
		catch (IOException e)
		{
			_logger.error("Cannot start resource pack server", e);
		}
		
		_server.createContext("/resource-pack", new Handler());
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
	
	static class Handler implements HttpHandler
	{
		@Override
		public void handle(HttpExchange exchange) throws IOException
		{
			if (!Files.exists(ResourcePackService.ResourcePackLocation))
			{
				exchange.sendResponseHeaders(404, 0);
				exchange.getResponseBody().close();
				
				return;
			}
			
			exchange.sendResponseHeaders(200, Files.size(ResourcePackService.ResourcePackLocation));
			OutputStream outputStream = exchange.getResponseBody();
			Files.copy(ResourcePackService.ResourcePackLocation, outputStream);
			outputStream.close();
		}
	}
}
