package cz.cuni.mff.fitoptivis.modelExtractor;

import java.io.InputStream;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class CodeLoader {
	JSONTokener tokener;
	
	public CodeLoader(InputStream input) {
		tokener = new JSONTokener(input);		
	}
	
	public DslCode loadCode(String name) {	
		DslCode result = new DslCode();
		
		if (!name.equals("DEFAULT")) {
			// TODO: request additional file
			return null;
		}
		
		try {	
			JSONObject obj = new JSONObject(tokener);			
			result.name = obj.getString("name");
			result.content = obj.getString("content");			
			
		} catch (JSONException e) {
			System.err.println("Error reading JSON: " + e.getMessage());
			return null;
		}
		
		return result;
	}
}
