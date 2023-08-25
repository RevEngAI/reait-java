package ai.reveng.toolkit.api;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

import ai.reveng.toolkit.Config;
import ai.reveng.toolkit.types.Binary;
import ai.reveng.toolkit.types.FunctionEmbedding;
import ai.reveng.toolkit.utils.ResourceUtils;

@TestMethodOrder(OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ReaiApiProxyTest {
	private ReaiApiProxy apiProxy;
	private Config rc;
	private String binHash;
	private Binary binEmbedding;

	@BeforeEach
	public void init() {

		Path configFile = ResourceUtils.getResourcePath("reai-config.toml");
		rc = new Config(configFile.toString());

		apiProxy = new ReaiApiProxy(rc.getHost(), rc.getApiKey(), rc.getModel().toString());
	}

	@Test
	void testSendEcho() {
		ApiResponse res = apiProxy.echo();
		assertEquals(200, res.getStatusCode());
	}

	@Test
	@Order(1)
	void testAnalyse() {
		Path binPath = ResourceUtils.getResourcePath("false");

		// Upload the file
		ApiResponse res = apiProxy.analyse(binPath, 00400000, new AnalysisOptions.Builder().build());
		assertEquals(200, res.getStatusCode());

		binHash = res.getJsonObject().getString("sha_256_hash");

		// wait until status returns complete
		String status = "";
		do {
			status = apiProxy.status(binHash).getJsonObject().getString("status");
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
	void testDelete() {
		// delete the file
		ApiResponse res = apiProxy.delete(binHash);
		assertEquals(200, res.getStatusCode());
	}
	
	@Test
	@Order(3)
	void testEmbeddings() {
		binHash = "d2ba6c0a536b309a76a3bd7d07ff19bbfdc1224cf0fa8a959379d13750ca1a5e";
		ApiResponse res = apiProxy.embeddings(binHash);
		assertEquals(200, res.getStatusCode());
		
		binEmbedding = new Binary(res.getJsonArray());
		FunctionEmbedding testEmbedding = binEmbedding.getFunctionEmbedding("FUN_001040e0");
		assertEquals(100, testEmbedding.getSize());
	}
	
	@Test
	@Order(4)
	void testNearestSymbols() {
		ApiResponse res = apiProxy.nearestSymbols(binEmbedding.getFunctionEmbedding("FUN_001040e0").getEmbedding(), 1, null);
		assertEquals(200, res.getStatusCode());
	}
	
	@Test
	void testNearestBinaries() {
		String hash = "d2ba6c0a536b309a76a3bd7d07ff19bbfdc1224cf0fa8a959379d13750ca1a5e";
		ApiResponse res = apiProxy.signature(hash);
		List<Double> binEmbed = new ArrayList<>();
		JSONArray jSig = res.getJsonArray();
		for (int i = 0; i < jSig.length(); i++) {
			binEmbed.add(jSig.getDouble(i));
		}
		res = apiProxy.nearestBinaries(binEmbed, 3, null);
		assertEquals(200, res.getStatusCode());
	}
	
	@Test
	void testSignature() {
		// fdupes on test account
		String hash = "3caa869ed79bf8c8c7a54b87f6e3e439ed3355b6076befa939c3189b5fb19d14";
		ApiResponse res = apiProxy.signature(hash);
		assertEquals(200, res.getStatusCode());
	}
	
	@Test
	void testLogs() {
		String hash = "3caa869ed79bf8c8c7a54b87f6e3e439ed3355b6076befa939c3189b5fb19d14";
		ApiResponse res = apiProxy.logs(hash);
		assertEquals(200, res.getStatusCode());
	}
	
	@Test
	void testCVEs() {
		// NGINX with known vulns
		String hash = "f1c950e282f03adfb32bef2ffeb423b760643271ff60957628a9f9e3ffb5de4e";
		ApiResponse res = apiProxy.cves(hash);
		assertEquals(200, res.getStatusCode());
	}
	
	@Test
	void testSBOM() {
		String hash = "3caa869ed79bf8c8c7a54b87f6e3e439ed3355b6076befa939c3189b5fb19d14";
		ApiResponse res = apiProxy.sbom(hash);
		assertEquals(200, res.getStatusCode());
	}
}