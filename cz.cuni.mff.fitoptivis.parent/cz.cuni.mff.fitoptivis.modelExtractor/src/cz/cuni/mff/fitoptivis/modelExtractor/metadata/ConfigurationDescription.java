package cz.cuni.mff.fitoptivis.modelExtractor.metadata;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

public class ConfigurationDescription {
	public String name;
	public List<PortDescription> inputPorts;
	public List<PortDescription> outputPorts;
	public List<PortDescription> supportsPorts;
	public List<PortDescription> requiresPorts;
	public List<String> qualities;
	
	public Map<String, String> subcomponents; // name -> type
	public Map<String, String> subcomponentConfigurations;
	public List<LinkDescription> runsOnLinks;
	public List<LinkDescription> outputsToLinks;
	
	
	public ConfigurationDescription() {
		inputPorts = new ArrayList<PortDescription>();
		outputPorts = new ArrayList<PortDescription>();
		supportsPorts = new ArrayList<PortDescription>();
		requiresPorts = new ArrayList<PortDescription>();
		qualities = new ArrayList<String>();
		
		subcomponents = new HashMap<>();
		subcomponentConfigurations = new HashMap<>();
		runsOnLinks = new ArrayList<>();
		outputsToLinks = new ArrayList<>();
	}
}
