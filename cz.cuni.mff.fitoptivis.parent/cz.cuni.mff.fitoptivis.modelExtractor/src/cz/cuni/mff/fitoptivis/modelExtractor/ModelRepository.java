package cz.cuni.mff.fitoptivis.modelExtractor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.Resource.Diagnostic;
import org.eclipse.xtext.linking.ILinker;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.parser.IParseResult;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.XtextResourceFactory;
import org.eclipse.xtext.resource.XtextResourceSet;

import com.google.inject.Injector;

import cz.cuni.mff.fitoptivis.FitLangStandaloneSetup;
import cz.cuni.mff.fitoptivis.fitLang.Model;
import cz.cuni.mff.fitoptivis.parser.antlr.FitLangParser;
import cz.cuni.mff.fitoptivis.scoping.FitLangScopeProvider;

// maintains cache of parsed Models
public class ModelRepository {
	Map<String, Model> models;
	CodeLoader loader;
	XtextResourceSet resourceSet;
	
	public ModelRepository(CodeLoader loader) {
		models = new HashMap<String, Model>();
		this.loader = loader; 
		
		Injector injector = new FitLangStandaloneSetup().createInjectorAndDoEMFRegistration();				
		resourceSet = injector.getInstance(XtextResourceSet.class);
		resourceSet.addLoadOption(XtextResource.OPTION_RESOLVE_ALL, Boolean.TRUE);		
	}
	
	
	public Model getModel(String modelName) {
		if (!models.containsKey(modelName))
			loadModel(modelName);
		
		return models.get(modelName);
	}
	
	private void loadModel(String modelName) {
		DslCode code = loader.loadCode(modelName);		
				
		InputStream in = new ByteArrayInputStream(code.content.getBytes());
		Resource resource = resourceSet.createResource(URI.createURI("stdin:/" + modelName + ".fit"));
		try {
			resource.load(in, resourceSet.getLoadOptions());
			
			Model model = (Model) resource.getContents().get(0);
			
				
			if (!resource.getErrors().isEmpty()) {
				System.err.println("Got syntax errors in the result");
				for(Diagnostic error: resource.getErrors()) {
					String err = error.getMessage();
					System.err.println("Error (" + error.getLine() + ":" + error.getColumn() + "): " + err);
				}
			}			
			
			models.put(code.name, model);
			models.put(modelName, model);
		}
		catch(IOException e)
		{
			// do nothing?
		}
		
	}
}
