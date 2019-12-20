package cz.cuni.mff.fitoptivis.modelExtractor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cz.cuni.mff.fitoptivis.fitLang.*;
import cz.cuni.mff.fitoptivis.modelExtractor.data.*;
import cz.cuni.mff.fitoptivis.modelExtractor.data.Component;
import static cz.cuni.mff.fitoptivis.modelExtractor.ListUtils.getLast;

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
			ArrayIndex arrayDef = subcomponent.getArray();
			if (arrayDef != null) {
				IntLiteral literal = (IntLiteral)arrayDef.getArrayExpr();
				int arrayLength = literal.getValue();
				for(int i = 0; i < arrayLength; ++i) {
					Component component = new Component();
					IndexedName name = new IndexedName();
					name.setName(subcomponent.getName());
					name.setIndex(i);
					component.setName(name);
					result.addComponent(component);
				}				
			} else {
				Component component = new Component();
				IndexedName name = new IndexedName();
				name.setName(subcomponent.getName());
				component.setName(name);
				result.addComponent(component);
			}
		}		
		
		for(IndependentPredicate predicate: sys.getIndependentPredicates()) {
			if (predicate instanceof RunsOnPredicate) {
				processRunsOnPredicate(result, (RunsOnPredicate) predicate);
			}
			else if (predicate instanceof OutputsToPredicate) {
				processOutputsToPredicate(result, (OutputsToPredicate) predicate);
			} else if (predicate instanceof ConfiguredAsPredicate) {
				processConfiguredAsPredicate(result, (ConfiguredAsPredicate) predicate);
			} else if (predicate instanceof BoolExpression) {
				processBoolExpressionPredicate(result, (BoolExpression) predicate);
			}
		}
	}
		
	private void processRunsOnPredicate(SystemDescription result, RunsOnPredicate predicate) {		
		List<IndexedName> hostQualityPath = parseQualityExpression(predicate.getHost());
		List<IndexedName> guestQualityPath = parseQualityExpression(predicate.getGuest());
		Component host = findComponent(result, hostQualityPath, 1);
		Component guest = findComponent(result, guestQualityPath, 1);
		
		if (host == null || guest == null) {
			result.addError("Cannot find component linked in predicate: " + predicate.toString());
			return;
		}
		
		Link link = new Link();
		link.setFrom(host.getName());
		link.setTo(guest.getName());
		link.setFromPort(getLast(hostQualityPath));
		link.setToPort(getLast(guestQualityPath));
		
		result.addBudgetLink(link);
	}
	
	private void processOutputsToPredicate(SystemDescription result, OutputsToPredicate predicate) {		
		List<IndexedName> producerQualityPath = parseQualityExpression(predicate.getProducer());
		List<IndexedName> consumerQualityPath = parseQualityExpression(predicate.getConsumer());
		Component producer = findComponent(result, producerQualityPath, 1);
		Component consumer = findComponent(result, consumerQualityPath, 1);
		
		if (producer == null || consumer == null) {
			result.addError("Cannot find component linked in predicate: " + predicate.toString());
			return;
		}
		
		Link link = new Link();
		link.setFrom(producer.getName());
		link.setTo(consumer.getName());
		link.setFromPort(getLast(producerQualityPath));
		link.setToPort(getLast(consumerQualityPath));
		
		result.addChannelLink(link);
	}	
	
	private void processConfiguredAsPredicate(SystemDescription result, ConfiguredAsPredicate predicate) {
		List<IndexedName> componentPath = parseQualityExpression(predicate.getComponent());
		Component component = findComponent(result, componentPath, 0);
		
		if (component == null) {
			result.addError("Cannot find component linked in predicate: " + predicate.toString());
			return;
		}
		
		component.setConfigurationName(predicate.getConfiguration());
	}
	
	private void processBoolExpressionPredicate(SystemDescription result, BoolExpression predicate) {
		// only dealing with qualityPath = Literal
		if (!predicate.getOp().equals("==")) {
			result.addError("Can only process == predicates");
			return;
		}
		if (!(predicate.getLeft() instanceof QualityExpression) || !(predicate.getRight() instanceof Literal)) {
			result.addError("Can only process qualityPath == literal predicates");
			return;
		}
		
		String qualityValue;		
		Literal literal = (Literal) predicate.getRight();
		if (literal instanceof IntLiteral) {
			qualityValue = Integer.toString(((IntLiteral) literal).getValue());
		} else if (literal instanceof StringLiteral) {
			qualityValue = ((StringLiteral)literal).getText();
		} else {
			throw new RuntimeException("Unhandled predicate type");
		}
		
		QualityExpression path = (QualityExpression)predicate.getLeft();
		List<IndexedName> parsedPath = parseQualityExpression(path);
		if (parsedPath.size() == 1) { // it's system quality, not component quality
			result.addQuality(parsedPath.get(0).toString(), qualityValue);
		} else {
			Component component = findComponent(result, parsedPath, -1);
			if (component == null) {
				result.addError("Cannot find component linked in predicate: " + path.toString());
				return;
			}
			String qualityName = getLast(parsedPath).toString();
			
			
			component.addQuality(qualityName, qualityValue);
		}		
	}
	
	// helper methods
	
	private Component findComponent(SystemDescription result, List<IndexedName> qualityPath, int toSkipFromEnd) {
		List<Component> components = result.getComponents();
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
