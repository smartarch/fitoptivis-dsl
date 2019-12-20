package cz.cuni.mff.fitoptivis.modelExtractor.data;

import java.util.HashMap;
import java.util.Map;

public class Link {
	private IndexedName from;
	private IndexedName fromPort;
	
	private IndexedName to;
	private IndexedName toPort;
	
	private Map<String, String> qualities;
	
	public Link() {
		qualities = new HashMap<String, String>();
		from = new IndexedName();
		fromPort = new IndexedName();
		to = new IndexedName();
		toPort = new IndexedName();
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

	public IndexedName getToPort() {
		return toPort;
	}

	public void setToPort(IndexedName toPort) {
		this.toPort = toPort;
	}

	public IndexedName getFromPort() {
		return fromPort;
	}

	public void setFromPort(IndexedName fromPort) {
		this.fromPort = fromPort;
	}
}
