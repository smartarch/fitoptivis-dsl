package cz.cuni.mff.fitoptivis.modelExtractor.data;

public class IndexedName {
	String name;
	int index;
	boolean hasIndex;
	
	public IndexedName() {
		name = null;
		index = 0;
		hasIndex = false;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getIndex() {
		return index;
	}
	
	public void setIndex(int index) {
		this.index = index;
		hasIndex = true;
	}
	
	public void unsetIndex() {
		hasIndex = false;
	}
	
	public boolean hasIndex() {
		return hasIndex;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof IndexedName))
			return false;
		
		IndexedName other = (IndexedName) obj;
		return hasIndex == other.hasIndex 
				&& index == other.index
				&& java.util.Objects.equals(name, other.name);
	}
	
	@Override
	public String toString() {
		if (hasIndex) {
			return name + "[" + index + "]";
		} else {
			return name;
		}			
	}
}
