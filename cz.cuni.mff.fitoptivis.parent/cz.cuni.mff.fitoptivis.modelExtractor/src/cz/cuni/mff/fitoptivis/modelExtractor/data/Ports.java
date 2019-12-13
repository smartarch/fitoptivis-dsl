package cz.cuni.mff.fitoptivis.modelExtractor.data;

import java.util.ArrayList;
import java.util.List;

public class Ports {
	List<Port> inputs;
	List<Port> outputs;
	List<Port> requires;
	List<Port> supports;
	
	public Ports() {
		inputs = new ArrayList<Port>();
		outputs = new ArrayList<Port>();
		requires = new ArrayList<Port>();
		supports = new ArrayList<Port>();
	}
	
	public List<Port> getInputs() {
		return inputs;
	}
	
	public List<Port> getOutputs() {
		return outputs;
	}

	public List<Port> getRequires() {
		return requires;
	}

	public List<Port> getSupports() {
		return supports;
	}
}
