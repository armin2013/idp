package graph.model;


import de.tum.pssif.core.metamodel.JunctionNodeType;


public class MyJunctionNodeType {
	private JunctionNodeType type;

	public MyJunctionNodeType(JunctionNodeType type) {
		this.type = type;
	}

	public String getName() {
		return type.getName();
	}

	public JunctionNodeType getType()
	{
		return type;
	}
	
	public String toString() {
		return type.getName();
	}
	
	public boolean equals(Object obj) {
	    if (this == obj)
	        return true;
	    if (obj == null)
	        return false;
	    if (getClass() != obj.getClass())
	        return false;
	    
	    MyJunctionNodeType other = (MyJunctionNodeType) obj;
	    
	    if (this.getName()==other.getName())
	    	return true;
	    else
	    	return false;
	}
}
