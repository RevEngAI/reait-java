package ai.reveng.toolkit.api;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implements behaviour for the IHttpRequester Interface
 */
public class ApiRequesterImpl implements IApiRequester {

	private HttpClient httpClient;
	private String baseUrl;

	public ApiRequesterImpl(String baseUrl) {
		this.httpClient = HttpClient.newHttpClient();
		this.baseUrl = baseUrl;
	}

	@Override
	public ApiResponse send(ApiEndpoint endpoint, Map<String, String> queryParams, Map<String, String> bodyData,
			Map<String, String> headers) throws IOException, InterruptedException {
		String url = constructUrlWithQueryParams(baseUrl + endpoint.getPath(), queryParams);
		
		URI uri = URI.create(url);
		HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();

		switch (endpoint.getHttpMethod()) {
		case "GET":
			System.out.println("Creating GET request: " + url);
			if (queryParams != null && !queryParams.isEmpty()) {
				String queryString = queryParams.entrySet().stream()
						.map(entry -> entry.getKey() + "=" + entry.getValue()).collect(Collectors.joining("&"));

				uri = URI.create(endpoint.getPath() + "?" + queryString);
				requestBuilder.uri(uri);
			}
			requestBuilder.GET();
			break;
		case "POST":
		case "PUT":
			// If there's bodyData, we convert it to a format suitable for HTTP
			// transmission.
			String requestBody = (bodyData == null) ? ""
					: bodyData.entrySet().stream().map(entry -> entry.getKey() + "=" + entry.getValue())
							.collect(Collectors.joining("&"));
			requestBuilder.POST(HttpRequest.BodyPublishers.ofString(requestBody));
			break;
		default:
			requestBuilder.GET();
			break;
		}

		// Add headers if provided
		if (headers != null && !headers.isEmpty()) {
			headers.forEach(requestBuilder::header);
		}

		HttpRequest request = null;
		try {
			request = requestBuilder.uri(new URI(url)).header("Content-Type", "application/x-www-form-urlencoded")
					.build();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
		return new ApiResponse(response.statusCode(), response.body());
	}

	private String constructUrlWithQueryParams(String baseUrl, Map<String, String> queryParams) {
		if (queryParams == null || queryParams.isEmpty()) {
			return baseUrl;
		}

		String queryString = queryParams.entrySet().stream().map(entry -> entry.getKey() + "=" + entry.getValue())
				.collect(Collectors.joining("&"));

		return baseUrl + "?" + queryString;
	}
}