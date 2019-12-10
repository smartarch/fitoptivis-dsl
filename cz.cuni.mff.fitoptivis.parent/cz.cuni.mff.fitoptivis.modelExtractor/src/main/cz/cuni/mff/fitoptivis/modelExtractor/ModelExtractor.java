package cz.cuni.mff.fitoptivis.modelExtractor;

import org.json.JSONTokener;
import org.json.JSONObject;
import org.json.JSONException;
//import cz.cuni.mff.fitoptivis.fitLang.Model;
import cz.cuni.mff.fitoptivis.FitLangStandaloneSetup;
//import org.eclipse.emf.ecore.resource.ResourceSet;
//import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

public class ModelExtractor {
	
	private static Arguments parseArgs(String[] args) {
		final Arguments parsedArgs = new Arguments();
		
		if (args.length == 0) {
			System.out.println("Usage: ModelExtractor --system $(system_name) < $(dsl_code)");
			parsedArgs.error = true;
		}	
				
		for(int i = 0; i < args.length; ++i) {
			switch(args[i]) {
				case "--system": 
					if (args.length <= i + 1) {
						System.out.println("System name not provided.");
						parsedArgs.error = true;
						continue;
					}
					parsedArgs.systemName = args[i];
					i++;
					break;
				default:
					System.out.println("Unknown argument: " + args[i]);
					parsedArgs.error = true;
				break;
			}
		}
		
		return parsedArgs;
	}	
	
	private static DslCode readDslCode() {
		JSONTokener tokener = new JSONTokener(System.in);		

		try {	
			JSONObject obj = new JSONObject(tokener);
			DslCode result = new DslCode();
			result.name = obj.getString("name");
			result.content = obj.getString("content");
			return result;
			
		} catch (JSONException e) {
			return null;
		}
	}
	
	private static void loadCode() {
//		new FitLangStandaloneSetup().createInjectorAndDoEMFRegistration();
//		ResourceSet
	}

	public static void main(String[] args) {		
		final Arguments parsedArgs = parseArgs(args);
		
		if (parsedArgs.error) {
			System.exit(1);
		}
		
		final DslCode code = readDslCode();
		if (code == null) {
			System.out.println("Could not read the JSON file object. Quitting");
			System.exit(1);
		}
		
		System.out.println("code: " + code.content);
	}

}

class Arguments {
	public String systemName;
	public boolean error;
}

class DslCode {
	public String name;
	public String content;
}