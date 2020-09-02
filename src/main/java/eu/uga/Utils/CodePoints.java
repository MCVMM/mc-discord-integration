package eu.uga.Utils;

import org.jetbrains.annotations.NotNull;

public class CodePoints
{
	public static @NotNull String Utf16ToEscapedString(int utf16)
	{
		String out = "";
		
		short left = (short)((utf16 & 0xFFFF0000) >> 16);
		short right = (short)utf16;
		
		if (left != 0) out += String.format("\\u%s", String.format("%04x", left));
		out += String.format("\\u%s", String.format("%04x", right));
		
		return out;
	}
	
	public static @NotNull String Utf16ToString(int utf16)
	{
		short left = (short)((utf16 & 0xFFFF0000) >> 16);
		short right = (short)utf16;
		
		String out = "";
		if (left != 0) out += (char)left;
		out += (char)right;
		
		return out;
	}
}
