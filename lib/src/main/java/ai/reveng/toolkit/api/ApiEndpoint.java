package ai.reveng.toolkit.api;

/**
 * Defines the valid API Endpoints for RevEng.AI
 */
public enum ApiEndpoint {
	GET_MODELS("/models", "GET"),
	ECHO("/echo", "GET");
	
	private final String path;
	private final String httpMethod;
	
	ApiEndpoint(String path, String httpMethod) {
		this.path = path;
		this.httpMethod = httpMethod;
	}

	public String getPath() {
		return path;
	}

	public String getHttpMethod() {
		return httpMethod;
	}
}
