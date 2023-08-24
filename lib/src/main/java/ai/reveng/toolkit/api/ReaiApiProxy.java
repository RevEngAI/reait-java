package ai.reveng.toolkit.api;

import java.io.IOException;
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

	/**
	 * Creates a new proxy
	 * 
	 * @param baseUrl url of API host, e.g. https://reveng.ai
	 */
	public ReaiApiProxy(String baseUrl) {
		this.apiRequester = new ApiRequesterImpl(baseUrl);
		this.baseUrl = baseUrl;
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
	public ApiResponse echo(Map<String, String> headers) {
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
	 * Upload a binary to the server
	 * 
	 * @param params  parameters with details about the binary
	 * @param binPath path to binary you wish to upload
	 * @param headers request headers
	 * @return ApiResponse
	 */
	public ApiResponse upload(Map<String, String> params, Object binPath, Map<String, String> headers) {
		try {
			return send(ApiEndpoint.ANALYSE, null, params, binPath, ApiBodyType.FILE, headers);
		} catch (IOException | InterruptedException e) {
			return new ApiResponse(-1, e.getMessage());
		}
	}

	/**
	 * Check the status of an analysis
	 * 
	 * @param binHash SHA 256 hash of the binary you uploaded
	 * @param headers request headers
	 * @return ApiResponse
	 */
	public ApiResponse status(String binHash, Map<String, String> headers) {
		Map<String, String> pathParams = new HashMap<>();
		pathParams.put("sha_256_hash", binHash);
		try {
			return send(ApiEndpoint.STATUS, pathParams, null, null, null, headers);
		} catch (IOException | InterruptedException e) {
			return new ApiResponse(-1, e.getMessage());
		}
	}

	/**
	 * Delete an analysis from the server
	 * 
	 * @param binHash sha256 hash of the binary you wish to delete
	 * @param headers request headers
	 * @return ApiResponse
	 */
	public ApiResponse delete(String binHash, Map<String, String> headers) {
		Map<String, String> pathParams = new HashMap<>();
		pathParams.put("sha_256_hash", binHash);
		try {
			return send(ApiEndpoint.DELETE, pathParams, null, null, null, headers);
		} catch (IOException | InterruptedException e) {
			return new ApiResponse(-1, e.getMessage());
		}
	}
	
	public ApiResponse embeddings(String binHash, Map<String, String> headers) {
		Map<String, String> pathParams = new HashMap<>();
		pathParams.put("sha_256_hash", binHash);
		try {
			return send(ApiEndpoint.EMBEDDINGS, pathParams, null, null, null, headers);
		} catch (IOException | InterruptedException e) {
			return new ApiResponse(-1, e.getMessage());
		}
	}
}
