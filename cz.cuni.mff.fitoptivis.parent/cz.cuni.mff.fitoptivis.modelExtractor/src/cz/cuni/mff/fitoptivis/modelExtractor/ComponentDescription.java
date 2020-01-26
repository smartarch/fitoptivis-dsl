package cz.cuni.mff.fitoptivis.modelExtractor;

import java.util.Map;
import java.util.HashMap;

public class ComponentDescription {
	public String name;
	public Map<String, ConfigurationDescription> configurations;
	
	public ComponentDescription() {
		configurations = new HashMap<String, ConfigurationDescription>();
	}
}
