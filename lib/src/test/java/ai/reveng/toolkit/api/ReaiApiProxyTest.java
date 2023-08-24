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
	private IApiRequester apiRequester;
	private Map<String, String> headers;
	private Map<String, String> params;
	private Map<String, String> body;
	private Map<String, String> pathParams;
	private Config rc;

	@BeforeEach
	public void init() {
		workingDir = Path.of("", "src/test/resources");
		Path configFile = this.workingDir.resolve("reai-dev-config.toml");
		rc = new Config(configFile.toString());
		assertEquals("f77d0f9e-c7f2-4652-bd53-256c1aa3bc78", rc.getApiKey());
		assertEquals("https://api.reveng.ai", rc.getHost());
		assertEquals("binnet-0.1", rc.getModel().toString());

		apiRequester = new ReaiApiProxy(rc.getHost());

		headers = new HashMap<>();
		headers.put("Authorization", rc.getApiKey());
		headers.put("User-Agent", "REAIT Java Tests");

		params = new HashMap<>();
		
		pathParams = new HashMap<>();
	}

	@Test
	void testSendEcho() {
		try {
			ApiResponse res = apiRequester.send(ApiEndpoint.ECHO, null, null, // no params
					null, // no body for GET
					null, // no body type
					headers);
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
	void testUpload() {
		Path binPath = workingDir.resolve("false");
		File bin = binPath.toFile();
		assertTrue(bin.exists());

		params.put("model", rc.getModel().toString());
		params.put("file_options", "ELF");
		params.put("file_name", bin.getName());
		params.put("base_vaddr", "00400000");

		try {
			// Upload the file
			ApiResponse res = apiRequester.send(ApiEndpoint.ANALYSE, null, params, binPath, ApiBodyType.FILE, headers);
			assertEquals(200, res.getStatusCode());
			
			String binHash = res.getJsonObject().getString("sha_256_hash");
			pathParams.put("sha_256_hash", binHash);
			
			// wait until status returns complete
			String status = "";
			do {
				status = apiRequester.send(ApiEndpoint.STATUS, pathParams, null, null, null, headers).getJsonObject().getString("status");
				try {
	                Thread.sleep(15000); // Sleep for 15000 milliseconds (15 seconds)
	            } catch (InterruptedException e) {
	                e.printStackTrace();
	            }
			} while (!status.equalsIgnoreCase("complete"));
			
			// delete the file
			res = apiRequester.send(ApiEndpoint.DELETE, pathParams, null, null, null, headers);
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