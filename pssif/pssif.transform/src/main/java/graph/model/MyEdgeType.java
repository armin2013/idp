package graph.model;

import de.tum.pssif.core.common.PSSIFOption;
import de.tum.pssif.core.metamodel.EdgeType;
import de.tum.pssif.core.metamodel.NodeType;

/**
 * A container for the EdgeType from the PSSIF Model
 * Adds an easier API 
 * @author Luc
 *
 */
public class MyEdgeType {
	private EdgeType type; 
    private int lineType;
    
    /**
     * Creates a new MyEdgeType Object
     * @param type : EdgeType from the PSSIF Model
     * @param lineType : a number to identify the MyEdgeType
     */
    public MyEdgeType (EdgeType type, int lineType)
    {
    	this.type = type;
    	this.lineType = lineType;
    	
    }

	public EdgeType getType() {
		return type;
	}
	
	public EdgeType getParentType() {
		
		PSSIFOption<EdgeType>tmp = type.getGeneral();
		if (tmp!=null && tmp.isOne())
			return tmp.getOne();
		else
			return null;
	}

	public int getLineType() {
		return lineType;
	}
    
	 public String getName() {
			return type.getName();
	}
	 
	 @Override
	 public boolean equals(Object obj) {
	     if (this == obj)
	         return true;
	     if (obj == null)
	         return false;
	     if (getClass() != obj.getClass())
	         return false;
	     
	     MyEdgeType tmp = (MyEdgeType) obj;
	     
	     if (tmp.getName().equals(getName()))
	    	 return true;
	     else
	    	 return false;
	 }
	 
	 public String toString() {
		return type.getName();
	}

    
}
