package eu.uniga.Utils;

import eu.uniga.NewDiscordIntegrationMod;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Style;
import net.minecraft.text.TextColor;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;

import java.lang.reflect.Constructor;

public class MinecraftStyle
{
	public static Style NoStyle;
	private static Constructor<Style> _constructor;
	
	static
	{
		try
		{
			_constructor = Style.class.getDeclaredConstructor(
							TextColor.class,
							Boolean.class,
							Boolean.class,
							Boolean.class,
							Boolean.class,
							Boolean.class,
							ClickEvent.class,
							HoverEvent.class,
							String.class,
							Identifier.class);
			_constructor.setAccessible(true);
			NoStyle = _constructor.newInstance(TextColor.fromRgb(0xFFFFFF), false, false, false, false, false, null, null, null, null);
		}
		catch (Exception e)
		{
			LogManager.getLogger(NewDiscordIntegrationMod.Name).error(e);
		}
	}
}
