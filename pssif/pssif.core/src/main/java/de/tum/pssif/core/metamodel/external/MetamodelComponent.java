package de.tum.pssif.core.metamodel.external;

public abstract class MetamodelComponent {
	
	private String tag;
	private String name;
	private String type;
	
	public MetamodelComponent(String tag, String name, String type) {
		this.tag = tag;
		this.name = name;
		this.type = type;
	}
	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
