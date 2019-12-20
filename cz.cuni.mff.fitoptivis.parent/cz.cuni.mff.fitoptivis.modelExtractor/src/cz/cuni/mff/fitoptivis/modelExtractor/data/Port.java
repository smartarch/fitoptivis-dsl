package cz.cuni.mff.fitoptivis.modelExtractor.data;

public class Port {
	String type;
	IndexedName name;
	
	public Port() {
		name = new IndexedName();
	}
	
	public String getType() {
		return type;
	}
	
	public IndexedName getName() {
		return name;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public void setName(IndexedName name) {
		this.name = name;
	}
}
