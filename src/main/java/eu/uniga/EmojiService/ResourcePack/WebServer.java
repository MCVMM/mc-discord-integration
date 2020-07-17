package eu.uniga.EmojiService.ResourcePack;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.file.Files;

public final class WebServer
{
	private HttpServer _server;
	private final Logger _logger = LoggerFactory.getLogger(this.getClass().getName());
	
	public WebServer(int port)
	{
		try
		{
			_server = HttpServer.create(new InetSocketAddress(port), 0);
		}
		catch (IOException e)
		{
			_logger.warn("Cannot start resource pack server", e);
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
		private static final Logger _logger = LoggerFactory.getLogger(HttpHandler.class.getName());
		
		@Override
		public void handle(HttpExchange exchange)
		{
			if (!Files.exists(Service.ResourcePackLocation))
			{
				try
				{
					exchange.sendResponseHeaders(404, 0);
					exchange.getResponseBody().close();
				} catch (IOException e)
				{
					_logger.warn("Connection error", e);
				}
				
				return;
			}
			
			OutputStream outputStream = exchange.getResponseBody();
			
			try
			{
				exchange.sendResponseHeaders(200, Files.size(Service.ResourcePackLocation));
				Files.copy(Service.ResourcePackLocation, outputStream);
			}
			catch (IOException e)
			{
				_logger.warn("Error reading resource pack", e);
			}
			
			try
			{
				outputStream.close();
			}
			catch (IOException e)
			{
				_logger.warn("Connection error", e);
			}
			
		}
	}
}
