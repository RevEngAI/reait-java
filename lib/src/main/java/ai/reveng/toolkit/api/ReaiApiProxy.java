package ai.reveng.toolkit.api;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Proxy that manages API actions.
 * 
 * Useful for intercepting requests for logging and caching
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
	 * A simplified send method for requests ithout body types and headers
	 * 
	 * @param endpoint
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private ApiResponse send(ApiEndpoint endpoint) throws IOException, InterruptedException {
		return send(endpoint, null, null, null, null, null);
	}

	private ApiResponse send(ApiEndpoint endpoint, Map<String, String> pathParams, Map<String, String> queryParams, Object body, ApiBodyType bodyType,
			Map<String, String> headers) throws IOException, InterruptedException {
		String dynamicPath = (pathParams != null) ? endpoint.getPath(pathParams) : endpoint.getPath(new HashMap<>());
		String fullUrl = baseUrl + dynamicPath;
		System.out.println("Sending " + endpoint.getHttpMethod() + " request via proxy to: " + fullUrl);

		ApiResponse response = apiRequester.send(endpoint, pathParams, queryParams, body, bodyType, headers);
		System.out.println("Request completed.\n" + response.getResponseBody());

		return response;
	}
	
	public ApiResponse echo(Map<String, String> headers) {
		try {
			return send(ApiEndpoint.ECHO, null,
					null, // no params
					null, // no body for GET
					null, // no body type
					headers);
		} catch (IOException | InterruptedException e) {
			return new ApiResponse(-1, e.getMessage());
		}
	}
	
	public ApiResponse upload(Map<String, String> params, Object binPath, Map<String, String> headers) {
		try {
			return send(ApiEndpoint.ANALYSE, null, params, binPath, ApiBodyType.FILE, headers);
		} catch (IOException | InterruptedException e) {
			return new ApiResponse(-1, e.getMessage());
		}
	}
	
	public ApiResponse status(String binHash, Map<String, String> headers) {
		Map<String, String> pathParams = new HashMap<>();
		pathParams.put("sha_256_hash", binHash);
		try {
			return send(ApiEndpoint.STATUS, pathParams, null, null, null, headers);
		} catch (IOException | InterruptedException e) {
			return new ApiResponse(-1, e.getMessage());
		}
	}
	
	public ApiResponse delete(String binHash, Map<String, String> headers) {
		Map<String, String> pathParams = new HashMap<>();
		pathParams.put("sha_256_hash", binHash);
		try {
			return send(ApiEndpoint.DELETE, pathParams, null, null, null, headers);
		} catch (IOException | InterruptedException e) {
			return new ApiResponse(-1, e.getMessage());
		}
	}
}
