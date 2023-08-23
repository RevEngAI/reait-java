package ai.reveng.toolkit.api;

import java.io.IOException;
import java.util.Map;

/**
 * Interface for sending HTTP Requests to the RevEng.AI enpoints
 */
public interface IApiRequester {
	/**
	 * 
	 * @param endpoint path of the endpoint
	 * @param queryParams http parameters for the endpoint
	 * @param bodyData data for the request body
	 * @param headers http headers
	 * @return ApiResponse with status code and body
	 * @see ApiResponse
	 * @throws IOException
	 * @throws InterruptedException
	 */
    ApiResponse send(ApiEndpoint endpoint, Map<String, String> queryParams, Map<String, String> bodyData, Map<String, String> headers) throws IOException, InterruptedException;
}


