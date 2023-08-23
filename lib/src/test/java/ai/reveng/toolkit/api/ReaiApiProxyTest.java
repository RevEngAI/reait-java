package ai.reveng.toolkit.api;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ai.reveng.toolkit.Config;

class ReaiApiProxyTest {
	private Path workingDir;
	private IApiRequester apiRequester;
	private Map<String, String> headers;

	@BeforeEach
	public void init() {
		this.workingDir = Path.of("", "src/test/resources");
		Path configFile = this.workingDir.resolve("reai-dev-config.toml");
		Config rc = new Config(configFile.toString());
		assertEquals("f77d0f9e-c7f2-4652-bd53-256c1aa3bc78", rc.getApiKey());
		assertEquals("https://api.reveng.ai", rc.getHost());
		assertEquals("binnet-0.1", rc.getModel().toString());

		apiRequester = new ReaiApiProxy(rc.getHost());
		
		headers = new HashMap<>();
		headers.put("Authorization", rc.getApiKey());
	}

	@Test
	void testSendEcho() {
		try {
			ApiResponse res = apiRequester.send(ApiEndpoint.ECHO, null, null, headers);
			assertEquals(200, res.getStatusCode());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	void testSendGetModels() {
		try {
			ApiResponse res = apiRequester.send(ApiEndpoint.GET_MODELS, null, null, headers);
			assertEquals(200, res.getStatusCode());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}