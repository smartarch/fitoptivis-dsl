package cz.cuni.mff.fitoptivis.modelExtractor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cz.cuni.mff.fitoptivis.fitLang.*;
import cz.cuni.mff.fitoptivis.modelExtractor.data.*;
import cz.cuni.mff.fitoptivis.modelExtractor.data.Component;

public class SystemParser {
	ModelRepository models;
	
	public static final String DEFAULT_MODEL = "DEFAULT";
	
	public SystemParser(ModelRepository models) {
		this.models = models;
	}
	
	public SystemDescription parse(String systemName) {
		SystemDescription result = new SystemDescription();
		
		result.setName(systemName);
		
		Model defaultModel = models.getModel(DEFAULT_MODEL);
		
		for(cz.cuni.mff.fitoptivis.fitLang.System sys: defaultModel.getSystems()) {
			if (systemName.equals(sys.getName())) {
				processSystem(result, sys);
			}
		}
		
		return result;
	}
	
	private void processSystem(SystemDescription result, cz.cuni.mff.fitoptivis.fitLang.System sys) {
		for(SubComponentPredicate subcomponent: sys.getSubComponents()) {
			Component component = new Component();
			ArrayIndex arrayDef = subcomponent.getArray();
			if (arrayDef != null) {
				// an array is defined...
				//TODO: handle
			}
			component.setName(subcomponent.getName());
			result.addComponent(component);
		}		
		
		for(IndependentPredicate predicate: sys.getIndependentPredicates()) {
			if (predicate instanceof RunsOnPredicate) {
				processRunsOnPredicate(result, (RunsOnPredicate) predicate);				
						
			}
		}
	}
	
	private void processRunsOnPredicate(SystemDescription result, RunsOnPredicate predicate) {
		Component host = findComponent(result, predicate.getHost(), 1);
		Component guest = findComponent(result, predicate.getGuest(), 1);
		
		if (host == null || guest == null) {
			result.addError("Cannot find component linked in predicate: " + predicate.toString());
			return;
		}
		
		Link link = new Link();
		link.setFrom(host.getName());
		link.setTo(guest.getName());
		link.setFromPort();
		link.setToPort();
		
		result.addBudgetLink(link);
	}
	
	private Component findComponent(SystemDescription result, QualityExpression expression, int toSkipFromEnd) {
		List<Component> components = result.getComponents();
		List<IndexedName> qualityPath = parseQualityExpression(expression);
		Component candidate = null;
		
		for(int i = 0; i < qualityPath.size() - toSkipFromEnd; ++i) {
			IndexedName name = qualityPath.get(i);
			candidate = findComponent(components, name);
			if (candidate == null) 
				return null;
			
			components = candidate.getSubComponents();
		}
		
		return candidate;
	}
	
	private Component findComponent(List<Component> components, IndexedName name) {
		for(Component c: components) {
			if (name.equals(c.getName()))
				return c;
		}
		return null;
	}
	
	private List<IndexedName> parseQualityExpression(QualityExpression expression) {
		List<IndexedName> reversedResult = new ArrayList<IndexedName>();
		
		IndexedName another = new IndexedName();
		while (expression != null) {			
			
			if (expression instanceof PropertyAccessExpression) {
				PropertyAccessExpression access = (PropertyAccessExpression) expression;
				another.setName(access.getPropertyName());
				reversedResult.add(another);
				another = new IndexedName();
				expression = access.getObject();
				
			} else if (expression instanceof ArrayAccessExpression) {
				ArrayAccessExpression access = (ArrayAccessExpression) expression;
				IntLiteral indexLiteral = (IntLiteral)access.getIndex().getArrayExpr();
				another.setIndex(indexLiteral.getValue());
				expression = access.getArray();
				
			} else {
				another.setName(expression.getQualityName());
				reversedResult.add(another);
				break;
			}
		}
		
		Collections.reverse(reversedResult);		
		return reversedResult;
	}
}
