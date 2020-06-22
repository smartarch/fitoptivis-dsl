package cz.cuni.mff.fitoptivis.modelExtractor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.cuni.mff.fitoptivis.fitLang.*;
import cz.cuni.mff.fitoptivis.modelExtractor.data.*;
import cz.cuni.mff.fitoptivis.modelExtractor.data.Component;
import cz.cuni.mff.fitoptivis.modelExtractor.metadata.ComponentDescription;
import cz.cuni.mff.fitoptivis.modelExtractor.metadata.ConfigurationDescription;
import cz.cuni.mff.fitoptivis.modelExtractor.metadata.InterfaceDescription;
import cz.cuni.mff.fitoptivis.modelExtractor.metadata.LinkDescription;
import cz.cuni.mff.fitoptivis.modelExtractor.metadata.PortDescription;

import static cz.cuni.mff.fitoptivis.modelExtractor.ListUtils.getLast;

public class SystemParser {
	ModelRepository models;
	
	public static final String DEFAULT_MODEL = "DEFAULT";
	public static final String DEFAULT_CONFIGURATION = "default";
	
	private class Metadata {
		public Model defaultModel;
		public Map<String, InterfaceDescription> channels;
		public Map<String, InterfaceDescription> budgets;
		public Map<String, ComponentDescription> components;
		
		public Metadata() {
			channels = new HashMap<String, InterfaceDescription>();
			budgets = new HashMap<String, InterfaceDescription>();
			components = new HashMap<String, ComponentDescription>();
		}
	}
	
	public SystemParser(ModelRepository models) {
		this.models = models;
	}
	
	public SystemDescription parse(String systemName) {
		SystemDescription result = new SystemDescription();
		
		result.setName(systemName);
		
		Metadata metadata = new Metadata();
		metadata.defaultModel = models.getModel(DEFAULT_MODEL);
		
		//TODO imports
		for(Budget budget: metadata.defaultModel.getBudgets()) {
			String name = budget.getName();
			InterfaceDescription description = new InterfaceDescription(name);
			for(SingleQualityDef def: budget.getQualityDefinitions()) {
				description.Qualities.add(def.getName());
			}
			metadata.budgets.put(name, description);
		}
		
		for(Channel channel: metadata.defaultModel.getChannels()) {
			String name = channel.getName();
			InterfaceDescription description = new InterfaceDescription(name);
			for(SingleQualityDef def: channel.getQualityDefinitions()) {
				description.Qualities.add(def.getName());
			}
			metadata.channels.put(name, description);
		}
		
		for(cz.cuni.mff.fitoptivis.fitLang.Component component: metadata.defaultModel.getComponents()) {
			processComponentDescription(result, component, metadata);
		}
		
		for(cz.cuni.mff.fitoptivis.fitLang.System sys: metadata.defaultModel.getSystems()) {
			if (systemName.equals(sys.getName())) {
				processSystem(result, sys, metadata);
			}
		}
		
		applyMetadata(result, metadata);
		
		return result;
	}
	
	private void applyMetadata(SystemDescription result, Metadata metadata) {
		for(Component component: result.getComponents()) {
			applyMetadataToComponent(component, metadata);
		}
	}
	
	private void applyMetadataToComponent(Component component, Metadata metadata) {
		for(Component subcomponent: component.getSubComponents()) {
			applyMetadataToComponent(subcomponent, metadata);
		}
		
		ConfigurationDescription description = metadata.components.get(component.getType()).configurations.get(component.getConfigurationName());
		
		Ports ports = component.getPorts();
		
		copyPortsFromMetadata(ports.getInputs(), description.inputPorts);
		copyPortsFromMetadata(ports.getOutputs(), description.outputPorts);
		copyPortsFromMetadata(ports.getRequires(), description.requiresPorts);
		copyPortsFromMetadata(ports.getSupports(), description.supportsPorts);
		copyQualitiesFromMetadata(component, description);
		addSubcomponentsFromMetadata(component, description, metadata);
	}
	
	private void addSubcomponentsFromMetadata(Component component, ConfigurationDescription description, Metadata metadata) {
		List<Component> subcomponents = component.getSubComponents();
		
		// add subcomponents - type, name, configuration, qualities, ports, subcomponents of subcomponents (recursively)
		for(String subcomponentName: description.subcomponents.keySet()) {
			Component subcomponent = new Component();
			
			subcomponent.setType(description.subcomponents.get(subcomponentName));
			
			IndexedName name = new IndexedName();
			name.setName(subcomponentName);
			subcomponent.setName(name);
			
			if (description.subcomponentConfigurations.containsKey(subcomponentName))
				subcomponent.setConfigurationName(description.subcomponentConfigurations.get(subcomponentName));
			else
				subcomponent.setConfigurationName(DEFAULT_CONFIGURATION);
			
			subcomponents.add(subcomponent);
			// add ports, qualities, and additional subcomponents
			applyMetadataToComponent(subcomponent, metadata);
		}
		
		// connect the components
		List<Link> budgetLinks = component.getBudgetLinks();
		for (LinkDescription runsOn: description.runsOnLinks) {
			Link link = new Link();
			IndexedName from = new IndexedName();
			IndexedName fromPort = new IndexedName();
			IndexedName to = new IndexedName();
			IndexedName toPort = new IndexedName();
			
			from.setName(runsOn.from);
			fromPort.setName(runsOn.fromPort);
			to.setName(runsOn.to);
			toPort.setName(runsOn.toPort);
			
			link.setFrom(from);
			link.setFromPort(fromPort);
			link.setTo(to);
			link.setToPort(toPort);
			
			budgetLinks.add(link);
		}
		
		List<Link> channelLinks = component.getChannelLinks();
		for (LinkDescription outputsTo: description.outputsToLinks) {
			Link link = new Link();
			IndexedName from = new IndexedName();
			IndexedName fromPort = new IndexedName();
			IndexedName to = new IndexedName();
			IndexedName toPort = new IndexedName();
			
			from.setName(outputsTo.from);
			fromPort.setName(outputsTo.fromPort);
			to.setName(outputsTo.to);
			toPort.setName(outputsTo.toPort);
			
			link.setFrom(from);
			link.setFromPort(fromPort);
			link.setTo(to);
			link.setToPort(toPort);
			
			channelLinks.add(link);
		}
	}
	
	private void copyPortsFromMetadata(List<Port> target, List<PortDescription> source) {
		for(PortDescription port: source) {
			Port p = new Port();
			IndexedName name = new IndexedName();
			name.setName(port.name);			
			//todo: indexes
			p.setName(name);
			p.setType(port.type);
			target.add(p);
		}
	}
	
	private void copyQualitiesFromMetadata(Component component, ConfigurationDescription metadata) {
		for(String quality: metadata.qualities) {
			component.addQuality(quality, "");
		}		
	}
	
	private void processComponentDescription(SystemDescription system, cz.cuni.mff.fitoptivis.fitLang.Component component, Metadata metadata) {
		ComponentDescription result = new ComponentDescription();
		
		result.name = component.getName();
		ComponentRules rules = component.getComponentRules();
		if (rules instanceof Configuration) {
			processComponentConfiguration(system, result, (Configuration)rules, metadata);
		} else if (rules instanceof Configurations) {
			Configurations configs = (Configurations)rules;
			for(Configuration conf: configs.getConfiguration()) {
				processComponentConfiguration(system, result, conf, metadata);
			}
		}
		
		metadata.components.put(result.name, result);
	}
	
	private void processComponentConfiguration(SystemDescription system, ComponentDescription result, Configuration configuration, Metadata metadata) {
		ConfigurationDescription description = new ConfigurationDescription();
		description.name = configuration.getName();
		if (description.name == null)
			description.name = DEFAULT_CONFIGURATION;
		
		ConfigurationBody body = configuration.getConfigBody();
		// ports
		processConfigurationPorts(body, description, metadata);		
		// qualities
		for(QualityPredicate quality: body.getQualities()) {
			QualityDeclaration q = (QualityDeclaration)quality;
			description.qualities.add(q.getName());
		}
		//subcomponents
		for(SubComponentPredicate subcomponent: body.getSubComponents()) {
			String type = subcomponent.getType().getName();
			String name = subcomponent.getName(); 
			description.subcomponents.put(name, type);
		}	
		// configurations, runs on, outputs to
		for(IndependentPredicate predicate: body.getIndependentPredicates()) {
			if (predicate instanceof ConfiguredAsPredicate) {
				ConfiguredAsPredicate configuredAs = (ConfiguredAsPredicate)predicate;
				
				QualityExpression componentPath = configuredAs.getComponent();
				String configName = configuredAs.getConfiguration();
				
				List<IndexedName> parsedPath = parseQualityExpression(componentPath);
				// supports only simplest quality expressions
				String componentName = parsedPath.get(0).getName();
				
				description.subcomponentConfigurations.put(componentName, configName);
			} else if (predicate instanceof RunsOnPredicate) {
				RunsOnPredicate runsOn = (RunsOnPredicate)predicate;
				
				QualityExpression host = runsOn.getHost();
				QualityExpression guest = runsOn.getGuest();
				
				List<IndexedName> hostPath = parseQualityExpression(host);
				List<IndexedName> guestPath = parseQualityExpression(guest);
				if (hostPath.size() > 2) {
					system.addError("HostPath " + serializePath(hostPath) + " is not at most 2 long. This tool can't support it.");
					continue;
				}
				if (guestPath.size() > 2) {
					system.addError("GuestPath" + serializePath(guestPath) + " is not at most 2 long. This tool can't support it.");
					continue;
				}
				
				LinkDescription linkDescription = new LinkDescription();
				if (hostPath.size() == 1) {
					linkDescription.to = null;
					linkDescription.toPort = hostPath.get(0).getName();
				} else {
					linkDescription.to = hostPath.get(0).getName();
					linkDescription.toPort = hostPath.get(1).getName();					
				}
				if (guestPath.size() == 1) {
					linkDescription.from = null;
					linkDescription.fromPort = guestPath.get(0).getName();
				} else {
					linkDescription.from = guestPath.get(0).getName();
					linkDescription.fromPort = guestPath.get(1).getName();					
				}
				
				description.runsOnLinks.add(linkDescription);
			} else if (predicate instanceof OutputsToPredicate) {
				OutputsToPredicate outputsTo = (OutputsToPredicate)predicate;
				
				QualityExpression producer = outputsTo.getProducer();
				QualityExpression consumer = outputsTo.getConsumer();
				
				List<IndexedName> producerPath = parseQualityExpression(producer);
				List<IndexedName> consumerPath = parseQualityExpression(consumer);
				
				if (producerPath.size() > 2) {
					system.addError("ProducerPath " + serializePath(producerPath) + " is not at most 2 long. This tool can't support it.");
					continue;
				}
				if (consumerPath.size() > 2) {
					system.addError("ConsumerPath" + serializePath(consumerPath) + " is not at most 2 long. This tool can't support it.");
					continue;
				}
				
				LinkDescription linkDescription = new LinkDescription();
				
				if (producerPath.size() == 1) {
					linkDescription.from = null;
					linkDescription.fromPort = producerPath.get(0).getName();
				} else {
					linkDescription.from = producerPath.get(0).getName();
					linkDescription.fromPort = producerPath.get(1).getName();					
				}
				if (consumerPath.size() == 1) {
					linkDescription.to = null;
					linkDescription.toPort = consumerPath.get(0).getName();
				} else {
					linkDescription.to = consumerPath.get(0).getName();
					linkDescription.toPort = consumerPath.get(1).getName();					
				}
								
				description.outputsToLinks.add(linkDescription);
			}
		}
				
		result.configurations.put(description.name, description);
	}
	
	private void processConfigurationPorts(ConfigurationBody body, ConfigurationDescription description, Metadata metadata) {
		for(InputsPredicate input: body.getInputs()) {
			PortDescription port = new PortDescription();
			port.type = input.getChannel().getName();
			port.name = input.getName();
			description.inputPorts.add(port);
		}
		for(OutputsPredicate output: body.getOutputs()) {
			PortDescription port = new PortDescription();
			port.type = output
					.getChannel()
					.getName();
			port.name = output.getName();
			description.outputPorts.add(port);
		}
		for(SupportsPredicate supports: body.getSupports()) {
			PortDescription port = new PortDescription();
			port.type = supports.getEnvironment().getName();
			port.name = supports.getName();
			description.supportsPorts.add(port);
		}
		for(RequiresPredicate requires: body.getRequires()) {
			PortDescription port = new PortDescription();
			port.type = requires.getEnvironment().getName();
			port.name = requires.getName();
			description.requiresPorts.add(port);
		}
	}
	
	private void processSubcomponent(SystemDescription result, SubComponentPredicate subcomponent, IndexedName name, Metadata metadata) {
		
		Component component = new Component();
		
		component.setName(name);
		String componentType = subcomponent.getType().getName();
		component.setType(componentType);
		component.setConfigurationName(DEFAULT_CONFIGURATION);
		
		result.addComponent(component);
	}
		
	private void processSystem(SystemDescription result, cz.cuni.mff.fitoptivis.fitLang.System sys, Metadata metadata) {
		for(SubComponentPredicate subcomponent: sys.getSubComponents()) {			
			ArrayIndex arrayDef = subcomponent.getArray();
			if (arrayDef != null) {
				IntLiteral literal = (IntLiteral)arrayDef.getArrayExpr();
				int arrayLength = literal.getValue();
				for(int i = 0; i < arrayLength; ++i) {
					IndexedName name = new IndexedName();
					name.setName(subcomponent.getName());
					name.setIndex(i);
					processSubcomponent(result, subcomponent, name, metadata);
				}				
			} else {
				IndexedName name = new IndexedName();
				name.setName(subcomponent.getName());
				processSubcomponent(result, subcomponent, name, metadata);
			}
		}	
		
		// TODO: process subcomponents
		// TODO: add ports according to spec
		
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
			Component component = findComponent(result, parsedPath, 1);
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
	
	private String serializePath(List<IndexedName> path) {
		// inefficient, not using stringbuilder, too lazy to look up documentation
		String result = "";
		
		for(IndexedName name: path) {
			if (!result.equals(""))
				result = result + ".";
			
			if (name.hasIndex())
				result = result + name.getName() + "[" + name.getIndex() + "]";
			else
				result = result + name.getName();
		}
		
		return result;
	}
}
