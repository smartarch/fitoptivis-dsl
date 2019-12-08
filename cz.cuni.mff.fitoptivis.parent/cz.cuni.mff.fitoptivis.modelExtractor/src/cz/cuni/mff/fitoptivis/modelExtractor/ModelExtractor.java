package cz.cuni.mff.fitoptivis.modelExtractor;

import java.io.BufferedReader;
import java.io.InputStreamReader;

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

	public static void main(String[] args) {		
		final Arguments parsedArgs = parseArgs(args);
		
		if (parsedArgs.error) {
			System.exit(1);
		}

		
	}

}

class Arguments {
	public String systemName;
	public boolean error;
}