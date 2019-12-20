package cz.cuni.mff.fitoptivis.modelExtractor;

import org.json.JSONTokener;
import org.json.JSONObject;
import org.json.JSONException;

import java.io.Reader;
import java.io.StringReader;

import com.google.inject.Injector;

import cz.cuni.mff.fitoptivis.fitLang.Model;
import cz.cuni.mff.fitoptivis.modelExtractor.data.SystemDescription;
import cz.cuni.mff.fitoptivis.parser.antlr.FitLangParser;
import cz.cuni.mff.fitoptivis.FitLangStandaloneSetup;

import org.eclipse.xtext.parser.IParseResult;

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
	
	private static SystemParser setupParser() {
		CodeLoader loader = new CodeLoader(System.in);
		ModelRepository repository = new ModelRepository(loader);
		SystemParser parser = new SystemParser(repository);
		repository.getModel(SystemParser.DEFAULT_MODEL);
		
		return parser;
	}

	public static void main(String[] args) {		
		final Arguments parsedArgs = parseArgs(args);
		
		if (parsedArgs.error) {
			System.exit(1);
		}
		
		SystemParser parser = setupParser();
		SystemDescription description = parser.parse(parsedArgs.systemName);	
		String json = SystemDescriptionToJsonConverter.convertToJson(description);
		System.out.print(json);
	}

}

class Arguments {
	public String systemName;
	public boolean error;
}