package ai.reveng.toolkit.api;

import java.util.Map;

/**
 * Defines the valid API Endpoints for RevEng.AI
 */
public enum ApiEndpoint {
	GET_MODELS("/models", "GET"), ECHO("/echo", "GET"), ANALYSE("/analyse", "POST"),
	STATUS("/analyse/status/{sha_256_hash}", "GET");

	private final String pathPattern;
	private final String httpMethod;

	ApiEndpoint(String pathPattern, String httpMethod) {
		this.pathPattern = pathPattern;
		this.httpMethod = httpMethod;
	}

	public String getPath(Map<String, String> pathParams) {
		String resolvedPath = pathPattern;
		for (Map.Entry<String, String> entry : pathParams.entrySet()) {
			resolvedPath = resolvedPath.replace("{" + entry.getKey() + "}", entry.getValue());
		}
		return resolvedPath;
	}

	public String getHttpMethod() {
		return httpMethod;
	}
}
