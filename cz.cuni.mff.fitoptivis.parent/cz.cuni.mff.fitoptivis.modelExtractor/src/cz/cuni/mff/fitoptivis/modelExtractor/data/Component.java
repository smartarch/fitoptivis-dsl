package cz.cuni.mff.fitoptivis.modelExtractor.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Represents a node in the system graph
public class Component {
	private String type;
	private IndexedName name;
	private String configurationName;
	private Map<String, String> qualities;
	private Ports ports;
	
	private List<Component> subComponents;
	private List<Link> budgetLinks;
	private List<Link> channelLinks;
	
	public Component() {
		qualities = new HashMap<String, String>();
		subComponents = new ArrayList<Component>();
		budgetLinks = new ArrayList<Link>();
		channelLinks = new ArrayList<Link>();
		ports = new Ports();
		name = new IndexedName();
	}
	
	public Map<String, String> getQualities() {
		return qualities;
	}

	public IndexedName getName() {
		return name;
	}
	
	public String getConfigurationName() {
		return configurationName;
	}
	
	public void setConfigurationName(String configurationName) {
		this.configurationName = configurationName;
	}
	
	public List<Component> getSubComponents() {
		return subComponents;
	}
	
	public List<Link> getBudgetLinks() {
		return budgetLinks;
	}
	
	public List<Link> getChannelLinks() {
		return channelLinks;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public Ports getPorts() {
		return ports;
	}
}
