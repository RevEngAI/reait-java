package ai.reveng.toolkit.api;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

import ai.reveng.toolkit.Config;
import ai.reveng.toolkit.types.BinaryEmbedding;
import ai.reveng.toolkit.types.FunctionEmbedding;
import ai.reveng.toolkit.utils.ResourceUtils;

@TestMethodOrder(OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ReaiApiProxyTest {
	private ReaiApiProxy apiProxy;
	private Map<String, String> headers;
	private Map<String, String> params;
	private Config rc;
	private String binHash;
	private BinaryEmbedding binEmbedding;

	@BeforeEach
	public void init() {

		Path configFile = ResourceUtils.getResourcePath("reai-config.toml");
		rc = new Config(configFile.toString());

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
	@Order(1)
	void testUpload() {
		Path binPath = ResourceUtils.getResourcePath("false");
		File bin = binPath.toFile();
		assertTrue(bin.exists());

		params.put("model", rc.getModel().toString());
		params.put("file_options", "ELF");
		params.put("file_name", bin.getName());
		params.put("base_vaddr", "00400000");

		// Upload the file
		ApiResponse res = apiProxy.upload(params, binPath, headers);
		assertEquals(200, res.getStatusCode());

		binHash = res.getJsonObject().getString("sha_256_hash");

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
	}
	
	@Test
	@Order(2)
	void testEmbeddings() {
		// Fetch embeddings for the result
		ApiResponse res = apiProxy.embeddings(binHash, headers);
		assertEquals(200, res.getStatusCode());
		
		binEmbedding = new BinaryEmbedding(res.getJsonArray());
		FunctionEmbedding testEmbedding = binEmbedding.getFunctionEmbedding("quotearg_alloc");
		assertEquals(9, testEmbedding.getSize());
	}
	
	@Test
	@Order(3)
	void testDelete() {
		// delete the file
		ApiResponse res = apiProxy.delete(binHash, headers);
		assertEquals(200, res.getStatusCode());
	}
}