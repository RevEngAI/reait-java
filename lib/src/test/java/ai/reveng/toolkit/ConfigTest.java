package ai.reveng.toolkit;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class REAITConfigTest {
	private Path workingDir;
	
	@BeforeEach
	public void init() {
		this.workingDir = Path.of("", "src/test/resources");
	}

	@Test
	void testReadConfigFromTomlFile() {
		Path configFile = this.workingDir.resolve("reai-test-config.toml");
		Config rc = new Config(configFile.toString());
		assertEquals("l1br3", rc.getApiKey());
		assertEquals("https://api.reveng.ai", rc.getHost());
		assertEquals("binnet-0.1", rc.getModel().toString());
	}

}