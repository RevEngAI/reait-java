package ai.reveng.toolkit.api;

import java.io.IOException;
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

	@Override
	public ApiResponse send(ApiEndpoint endpoint, Map<String, String> queryParams, Map<String, String> bodyData,
			Map<String, String> headers) throws IOException, InterruptedException {
		String fullUrl = baseUrl + endpoint.getPath();
		System.out.println("Sending " + endpoint.getHttpMethod() + " request via proxy to: " + fullUrl);

		ApiResponse response = apiRequester.send(endpoint, queryParams, bodyData, headers);
		System.out.println("Request completed.\n"+response.getResponseBody());

		return response;
	}
}
