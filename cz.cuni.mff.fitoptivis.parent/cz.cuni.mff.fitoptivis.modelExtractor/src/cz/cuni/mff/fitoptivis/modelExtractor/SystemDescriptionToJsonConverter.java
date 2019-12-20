package cz.cuni.mff.fitoptivis.modelExtractor;

import cz.cuni.mff.fitoptivis.modelExtractor.data.*;

import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

public class SystemDescriptionToJsonConverter {
	public static String convertToJson(SystemDescription description) {
		JSONObject result = new JSONObject();
		List<String> errors = description.getErrors();
		if (!errors.isEmpty()) {
			for(String err: errors) {
				result.append("Errors", err);
			}
			result.put("System", (Object)null);
		} else {
			result.put("Errors", new JSONArray());
			result.put("System", processValidSystemDescription(description));
		}
		return result.toString();
	}
	
	private static JSONObject processValidSystemDescription(SystemDescription system) {
		JSONObject result = new JSONObject();
		result.put("Name", system.getName());
		for(Component component: system.getComponents()) {
			result.append("Components", processComponent(component));			
		}
		for(Link link: system.getBudgetLinks()) {
			result.append("RunsOnLinks", processRunsOnLink(link));
		}
		for(Link link: system.getChannelLinks()) {
			result.append("OutputsToLinks", processChannelLink(link));
		}
		result.put("Qualities", processQualities(system.getQualities()));
		return result;
	}
	
	private static JSONObject processRunsOnLink(Link link) {
		JSONObject result = new JSONObject();
		result.put("Host", processLinkEndpoint(link.getFrom(), link.getFromPort()));
		result.put("Guest", processLinkEndpoint(link.getTo(), link.getToPort()));		
		result.put("Qualities", processQualities(link.getQualities()));	
		
		return result;
	}
	
	private static JSONObject processChannelLink(Link link) {
		JSONObject result = new JSONObject();
		
		result.put("From", processLinkEndpoint(link.getFrom(), link.getFromPort()));
		result.put("To", processLinkEndpoint(link.getTo(), link.getToPort()));		
		result.put("Qualities", processQualities(link.getQualities()));				
		return result;
	}
	
	private static JSONObject processLinkEndpoint(IndexedName componentName, IndexedName portName) {
		JSONObject result = new JSONObject();
		result.put("componentName", processIndexedName(componentName));
		result.put("portName", processIndexedName(portName));		
		
		return result;
	}
	
	private static JSONObject processIndexedName(IndexedName name) {
		JSONObject result = new JSONObject();
		result.put("name", name.getName());
		if (name.hasIndex())
			result.put("index", name.getIndex());		
		
		return result;
	}
	
	private static JSONObject processQualities(Map<String, String> qualities) {
		JSONObject result = new JSONObject();
		for(Map.Entry<String, String> quality: qualities.entrySet()) {
			result.put(quality.getKey(), quality.getValue());
		}
		return result;
	}
	
	private static JSONObject processComponent(Component component) {
		JSONObject result = new JSONObject();
		result.put("Type", component.getType());
		result.put("Configuration", component.getConfigurationName());
		result.put("Name", processIndexedName(component.getName()));
		result.put("Qualities", processQualities(component.getQualities()));
		result.put("Ports", processPorts(component.getPorts()));
		for(Link link: component.getBudgetLinks()) {
			result.append("RunsOnLinks", processRunsOnLink(link));
		}
		for(Link link: component.getChannelLinks()) {
			result.append("OutputsToLinks", processChannelLink(link));
		}
		for(Component subComponent: component.getSubComponents()) {
			result.append("Subcomponents", processComponent(subComponent));
		}
		
		return result;
	}
	
	private static JSONObject processPorts(Ports ports) {
		JSONObject result = new JSONObject();
		
		result.put("Inputs", processPortsList(ports.getInputs()));
		result.put("Outputs", processPortsList(ports.getOutputs()));
		result.put("Supports", processPortsList(ports.getSupports()));
		result.put("Requires", processPortsList(ports.getRequires()));
		
		return result;
	}
	
	private static JSONArray processPortsList(List<Port> ports) {
		JSONArray result = new JSONArray();
		
		for(Port port: ports) {
			JSONObject obj = new JSONObject();
			obj.put("Type", port.getType());
			obj.put("Name", processIndexedName(port.getName()));
			result.put(obj);
		}
		
		return result;
	}
}
