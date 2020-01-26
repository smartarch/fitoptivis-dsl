package cz.cuni.mff.fitoptivis.modelExtractor;

import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.parser.IParseResult;

import com.google.inject.Injector;

import cz.cuni.mff.fitoptivis.FitLangStandaloneSetup;
import cz.cuni.mff.fitoptivis.fitLang.Model;
import cz.cuni.mff.fitoptivis.parser.antlr.FitLangParser;

// maintains cache of parsed Models
public class ModelRepository {
	Map<String, Model> models;
	CodeLoader loader;
	FitLangParser parser;
	
	public ModelRepository(CodeLoader loader) {
		models = new HashMap<String, Model>();
		this.loader = loader; 
		
		Injector injector = new FitLangStandaloneSetup().createInjectorAndDoEMFRegistration();		
		parser = injector.getInstance(FitLangParser.class);
	}
	
	
	public Model getModel(String modelName) {
		if (!models.containsKey(modelName))
			loadModel(modelName);
		
		return models.get(modelName);
	}
	
	private void loadModel(String modelName) {
		DslCode code = loader.loadCode(modelName);		
				
		Reader reader = new StringReader(code.content);
		IParseResult result = parser.parse(reader);
		
		Model model;
		
		if (result.hasSyntaxErrors()) {
			System.err.println("Got syntax errors in the result");
			for(INode error: result.getSyntaxErrors()) {
				String err = error.getText();
				System.err.println("Error (" + error.getStartLine() + ":" + error.getLength() + "): " + err);
			}
			
			model = null;
		} else {
			model = (Model)result.getRootASTElement();
		}		
		
		models.put(code.name, model);
		models.put(modelName, model);
	}
}
