package eu.uniga.Utils;

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
	
	static
	{
		try
		{
			Constructor<Style> constructor = Style.class.getDeclaredConstructor(
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
			constructor.setAccessible(true);
			NoStyle = constructor.newInstance(TextColor.fromRgb(0xFFFFFF), false, false, false, false, false, null, null, null, null);
		}
		catch (Exception e)
		{
			LogManager.getLogger().error(e);
		}
	}
}
