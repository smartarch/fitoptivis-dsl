package cz.cuni.mff.fitoptivis.modelExtractor;

import java.util.List;
import java.util.ArrayList;
import java.util.Dictionary;

public class ConfigurationDescription {
	public String name;
	public List<PortDescription> inputPorts;
	public List<PortDescription> outputPorts;
	public List<PortDescription> supportsPorts;
	public List<PortDescription> requiresPorts;
	public List<String> qualities;
	
	
	public ConfigurationDescription() {
		inputPorts = new ArrayList<PortDescription>();
		outputPorts = new ArrayList<PortDescription>();
		supportsPorts = new ArrayList<PortDescription>();
		requiresPorts = new ArrayList<PortDescription>();
		qualities = new ArrayList<String>();
	}
}
