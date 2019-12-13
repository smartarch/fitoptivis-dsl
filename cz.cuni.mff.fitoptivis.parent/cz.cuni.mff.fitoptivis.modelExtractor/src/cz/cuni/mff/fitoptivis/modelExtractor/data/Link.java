package cz.cuni.mff.fitoptivis.modelExtractor.data;

import java.util.HashMap;
import java.util.Map;

public class Link {
	private IndexedName from;
	private String fromPort;
	
	private IndexedName to;
	private String toPort;
	
	private Map<String, String> qualities;
	
	public Link() {
		qualities = new HashMap<String, String>();
		from = new IndexedName();
		to = new IndexedName();
	}
	
	public IndexedName getFrom() {
		return from;
	}
	
	public void setFrom(IndexedName from) {
		this.from = from;
	}
		
	public IndexedName getTo() {
		return to;
	}	
	
	public void setTo(IndexedName to) {
		this.to = to;
	}
	
	public Map<String, String> getQualities() {
		return qualities;
	}

	public String getToPort() {
		return toPort;
	}

	public void setToPort(String toPort) {
		this.toPort = toPort;
	}

	public String getFromPort() {
		return fromPort;
	}

	public void setFromPort(String fromPort) {
		this.fromPort = fromPort;
	}
}
