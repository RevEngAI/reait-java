package ai.reveng.toolkit.api;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Proxy that manages API actions.
 * 
 * Useful for intercepting requests for logging and caching. The goal with this
 * class is to provide user-friendly methods that wrap the ApiRequests interface
 */
public class ReaiApiProxy {
	private ApiRequesterImpl apiRequester;
	private String baseUrl;
	private String modelName;
	private Map<String, String> headers;

	/**
	 * Creates a new proxy
	 * 
	 * @param baseUrl url of API host, e.g. https://reveng.ai
	 */
	public ReaiApiProxy(String baseUrl, String apiKey, String modelName) {
		apiRequester = new ApiRequesterImpl(baseUrl);
		this.baseUrl = baseUrl;
		this.modelName = modelName;

		headers = new HashMap<>();
		headers.put("Authorization", apiKey);
		headers.put("User-Agent", "REAIT Java Proxy");
	}

	/**
	 * Main send method for the proxy
	 * 
	 * @see IApiRequester#send
	 */
	private ApiResponse send(ApiEndpoint endpoint, Map<String, String> pathParams, Map<String, String> queryParams,
			Object body, ApiBodyType bodyType, Map<String, String> headers) throws IOException, InterruptedException {
		String dynamicPath = (pathParams != null) ? endpoint.getPath(pathParams) : endpoint.getPath(new HashMap<>());
		String fullUrl = baseUrl + dynamicPath;
		System.out.println("Sending " + endpoint.getHttpMethod() + " request via proxy to: " + fullUrl);

		System.out.println(queryParams);

		ApiResponse response = apiRequester.send(endpoint, pathParams, queryParams, body, bodyType, headers);
		System.out.println("Request completed.\n" + response.getResponseBody());

		return response;
	}

	/**
	 * Send an echo request to the API to test for a connection
	 * 
	 * @param headers request headers
	 * @return ApiResponse
	 */
	public ApiResponse echo() {
		try {
			return send(ApiEndpoint.ECHO, null, null, // no params
					null, // no body for GET
					null, // no body type
					headers);
		} catch (IOException | InterruptedException e) {
			return new ApiResponse(-1, e.getMessage());
		}
	}

	/**
	 * Call the analysis endpoint
	 * 
	 * @param binPath
	 * @param modelName
	 * @param baseAddr
	 * @param opts
	 * @return
	 */
	public ApiResponse analyse(Path binPath, String modelName, String baseAddr, AnalysisOptions opts) {
		File bin = binPath.toFile();

		if (!bin.exists())
			throw new RuntimeException("Binary to upload does not exist");

		Map<String, String> params = new HashMap<>();
		params.put("file_name", bin.getName());
		params.put("base_vaddr", baseAddr);
		params.put("model", modelName);
		params.putAll(opts.toMap());

		try {
			return send(ApiEndpoint.ANALYSE, null, params, binPath, ApiBodyType.FILE, headers);
		} catch (IOException | InterruptedException e) {
			return new ApiResponse(-1, e.getMessage());
		}
	}

	/**
	 * Analyse call when we want to use the provided model name
	 * 
	 * @param binPath
	 * @param baseAddr
	 * @param opts
	 * @return
	 */
	public ApiResponse analyse(Path binPath, String baseAddr, AnalysisOptions opts) {
		return analyse(binPath, modelName, baseAddr, opts);
	}

	/**
	 * Check the status of an analysis
	 * 
	 * @param binHash SHA 256 hash of the binary you uploaded
	 * @return ApiResponse
	 */
	public ApiResponse status(String binHash) {
		Map<String, String> pathParams = new HashMap<>();
		pathParams.put("sha_256_hash", binHash);
		try {
			return send(ApiEndpoint.STATUS, pathParams, null, null, null, headers);
		} catch (IOException | InterruptedException e) {
			return new ApiResponse(-1, e.getMessage());
		}
	}

	/**
	 * Delete a given binary from the dataset
	 * 
	 * @param binHash   sha-256 hash of binary file
	 * @param modelName name of model used to perform the analysis
	 * @return
	 */
	public ApiResponse delete(String binHash, String modelName) {
		Map<String, String> pathParams = new HashMap<>();
		pathParams.put("sha_256_hash", binHash);

		Map<String, String> params = new HashMap<>();
		params.put("model_name", modelName);

		try {
			return send(ApiEndpoint.DELETE, pathParams, params, null, null, headers);
		} catch (IOException | InterruptedException e) {
			return new ApiResponse(-1, e.getMessage());
		}
	}

	/**
	 * Delete an analysis from the server
	 * 
	 * @param binHash sha256 hash of the binary you wish to delete
	 * @return ApiResponse
	 */
	public ApiResponse delete(String binHash) {
		return delete(binHash, modelName);
	}

	/**
	 * Return the function embeddings for the given binary
	 * 
	 * @param binHash   hash of binary to get embeddings for
	 * @param modelName model used to compute the embeddings
	 * @return
	 */
	public ApiResponse embeddings(String binHash, String modelName) {
		Map<String, String> pathParams = new HashMap<>();
		pathParams.put("sha_256_hash", binHash);
		
		Map<String, String> params = new HashMap<>();
		params.put("model_name", modelName);
		
		try {
			return send(ApiEndpoint.EMBEDDINGS, pathParams, null, null, null, headers);
		} catch (IOException | InterruptedException e) {
			return new ApiResponse(-1, e.getMessage());
		}
	}

	/**
	 * Return the embeddings for the given binary
	 * 
	 * @param binHash sha256 hash of binary
	 * @return
	 */
	public ApiResponse embeddings(String binHash) {
		return embeddings(binHash, modelName);
	}
	
	public ApiResponse signature(String binHash, String modelName) {
		Map<String, String> pathParams = new HashMap<>();
		pathParams.put("sha_256_hash", binHash);
		
		Map<String, String> params = new HashMap<>();
		params.put("model_name", modelName);
		
		try {
			return send(ApiEndpoint.SIGNATURE, pathParams, params, null, null, headers);
		} catch (IOException | InterruptedException e) {
			return new ApiResponse(-1, e.getMessage());
		}
	}
	
	public ApiResponse signature(String binHash) {
		return signature(binHash, modelName);
	}

	public ApiResponse nearestSymbols(double[] embeddings, String modelName, int nns, String[] collections) {
		return null;
	}

}
