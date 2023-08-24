package ai.reveng.toolkit.utils;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ResourceUtils {
	/**
	 * Returns path to the given filename
	 * @param filename name file inside the resource directory
	 * @return Path to the provided file
	 */
	public static Path getResourcePath(String filename) {
		URL resourceUrl = ResourceUtils.class.getClassLoader().getResource(filename);
		if (resourceUrl == null) {
            throw new RuntimeException("Resource not found: " + filename);
        }
		
		URI resourceUri;
		try {
			resourceUri = resourceUrl.toURI();
		} catch (URISyntaxException e) {
			 throw new RuntimeException("Invalid URI");
		}
		
		return Paths.get(resourceUri);
	}
}
