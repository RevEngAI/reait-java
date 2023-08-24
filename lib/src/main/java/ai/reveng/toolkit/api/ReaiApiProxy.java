package ai.reveng.toolkit.api;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Proxy that manages API actions.
 * 
 * Useful for intercepting requests for logging and caching
 */
public class ReaiApiProxy implements IApiRequester {
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
	public ApiResponse send(ApiEndpoint endpoint) throws IOException, InterruptedException {
		return send(endpoint, null, null, null, null, null);
	}

	@Override
	public ApiResponse send(ApiEndpoint endpoint, Map<String, String> pathParams, Map<String, String> queryParams, Object body, ApiBodyType bodyType,
			Map<String, String> headers) throws IOException, InterruptedException {
		String dynamicPath = (pathParams != null) ? endpoint.getPath(pathParams) : endpoint.getPath(new HashMap<>());
		String fullUrl = baseUrl + dynamicPath;
		System.out.println("Sending " + endpoint.getHttpMethod() + " request via proxy to: " + fullUrl);

		ApiResponse response = apiRequester.send(endpoint, pathParams, queryParams, body, bodyType, headers);
		System.out.println("Request completed.\n" + response.getResponseBody());

		return response;
	}
}
