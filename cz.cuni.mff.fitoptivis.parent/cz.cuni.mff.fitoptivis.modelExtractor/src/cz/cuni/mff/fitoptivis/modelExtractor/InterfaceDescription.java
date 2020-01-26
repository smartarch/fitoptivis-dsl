package cz.cuni.mff.fitoptivis.modelExtractor;

import java.util.ArrayList;
import java.util.List;

public class InterfaceDescription {
	public String Name;
	public List<String> Qualities;
	
	public InterfaceDescription(String name) {
		this.Name = name;
		Qualities = new ArrayList<String>();
	}
}
