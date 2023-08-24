package ai.reveng.toolkit.types;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;

public class BinaryEmbedding {
	private final Map<String, FunctionEmbedding> functionEmbeddings;
	
	public BinaryEmbedding(JSONArray jBinaryEmbeddings) {
		functionEmbeddings = new HashMap<String, FunctionEmbedding>();
		
		for (int i = 0; i < jBinaryEmbeddings.length(); i++) {
			FunctionEmbedding tmp = new FunctionEmbedding(jBinaryEmbeddings.getJSONObject(i));
			functionEmbeddings.put(tmp.getName(), tmp);
		}
	}
	
	public FunctionEmbedding getFunctionEmbedding(String fName) {
		return functionEmbeddings.containsKey(fName) ? functionEmbeddings.get(fName) : null;
	}
}
