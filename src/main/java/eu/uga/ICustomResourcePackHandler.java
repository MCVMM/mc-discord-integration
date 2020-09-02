package eu.uga;

import java.net.URL;
import java.util.Map;

public interface ICustomResourcePackHandler
{
	void UpdateDictionary(Map<String, Integer> dictionary);
	void ChangeResourcePack(URL url, String name, String sha1);
}
