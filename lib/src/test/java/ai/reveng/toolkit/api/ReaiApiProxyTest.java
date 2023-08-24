package ai.reveng.toolkit.api;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ai.reveng.toolkit.Config;

class ReaiApiProxyTest {
	private Path workingDir;
	private ReaiApiProxy apiProxy;
	private Map<String, String> headers;
	private Map<String, String> params;
	private Config rc;

	@BeforeEach
	public void init() {
		workingDir = Path.of("", "src/test/resources");
		Path configFile = this.workingDir.resolve("reai-dev-config.toml");
		rc = new Config(configFile.toString());
		assertEquals("f77d0f9e-c7f2-4652-bd53-256c1aa3bc78", rc.getApiKey());
		assertEquals("https://api.reveng.ai", rc.getHost());
		assertEquals("binnet-0.1", rc.getModel().toString());

		apiProxy = new ReaiApiProxy(rc.getHost());

		headers = new HashMap<>();
		headers.put("Authorization", rc.getApiKey());
		headers.put("User-Agent", "REAIT Java Tests");

		params = new HashMap<>();
	}

	@Test
	void testSendEcho() {
		ApiResponse res = apiProxy.echo(headers);
		assertEquals(200, res.getStatusCode());
	}

	@Test
	void testUpload() {
		Path binPath = workingDir.resolve("false");
		File bin = binPath.toFile();
		assertTrue(bin.exists());

		params.put("model", rc.getModel().toString());
		params.put("file_options", "ELF");
		params.put("file_name", bin.getName());
		params.put("base_vaddr", "00400000");
		
		// Upload the file
		ApiResponse res = apiProxy.upload(params, binPath, headers);
		assertEquals(200, res.getStatusCode());
		
		String binHash = res.getJsonObject().getString("sha_256_hash");
			
		// wait until status returns complete
		String status = "";
		do {
			status = apiProxy.status(binHash, headers).getJsonObject().getString("status");
			try {
				// during testing this takes ~ 1 1/2 minutes, so this is a good compromise
                Thread.sleep(30000); // Sleep for 30000 milliseconds (30 seconds)
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
		} while (!status.equalsIgnoreCase("complete"));
			
		// delete the file
		res = apiProxy.delete(binHash, headers);
		assertEquals(200, res.getStatusCode());
	}
}