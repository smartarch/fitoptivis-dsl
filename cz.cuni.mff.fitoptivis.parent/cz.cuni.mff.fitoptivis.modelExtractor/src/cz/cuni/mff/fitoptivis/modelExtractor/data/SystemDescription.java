package cz.cuni.mff.fitoptivis.modelExtractor.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Represents a graph of a system
public class SystemDescription {
	private String Name;
	private List<Component> components;
	private List<Link> budgetLinks;
	private List<Link> channelLinks;
	private Map<String, String> qualities;
	private List<String> errors;
	
	
	public SystemDescription() {
		components = new ArrayList<Component>();
		budgetLinks = new ArrayList<Link>();
		channelLinks = new ArrayList<Link>();
		qualities = new HashMap<String, String>();
		errors = new ArrayList<String>();
	}	

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}
	
	public List<Component> getComponents() {
		return components;
	}
	
	public List<Link> getBudgetLinks() {
		return budgetLinks;
	}
	
	public List<Link> getChannelLinks() {
		return channelLinks;
	}
	
	public void addComponent(Component c) {
		components.add(c);
	}
	
	public void addBudgetLink(Link l) {
		budgetLinks.add(l);
	}
	
	public void addChannelLink(Link l) {
		channelLinks.add(l);
	}
	
	public List<String> getErrors() {
		return errors;
	}
	
	public void addError(String error) {
		errors.add(error);
	}
	
	public Map<String, String> getQualities() {
		return qualities;
	}
	
	public void addQuality(String name, String value) {
		qualities.put(name, value);
	}
}
