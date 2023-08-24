package ai.reveng.toolkit;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Path;
import org.junit.jupiter.api.Test;

import ai.reveng.toolkit.utils.ResourceUtils;

class REAITConfigTest {

	@Test
	void testReadConfigFromTomlFile() {
		Path configFile = ResourceUtils.getResourcePath("reai-test-config.toml");
		Config rc = new Config(configFile.toString());
		assertEquals("l1br3", rc.getApiKey());
		assertEquals("https://api.reveng.ai", rc.getHost());
		assertEquals("binnet-0.1", rc.getModel().toString());
	}

}