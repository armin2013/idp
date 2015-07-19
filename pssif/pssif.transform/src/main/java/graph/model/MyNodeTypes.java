package graph.model;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import de.tum.pssif.core.metamodel.NodeType;

/**
 * All the possible Node Types of the PSSIF Model
 * @author Luc
 *
 */
public class MyNodeTypes {
	private LinkedList<MyNodeType> types;
	
	public MyNodeTypes (Collection<NodeType> newtypes)
	{
		this.types = new LinkedList<MyNodeType>();
		
		for (NodeType ntype : newtypes)
		{
			MyNodeType tmp = new MyNodeType(ntype);
			
			if (!types.contains(tmp))
				types.add(tmp);
		}
		
		Collections.sort(this.types, new MyNodeTypeComparator());
	}
	
	public MyNodeTypes (HashSet<MyNodeType> newTypes)
	{
		this.types = new LinkedList<MyNodeType>();
		
		Iterator<MyNodeType> it = newTypes.iterator();
		
		while (it.hasNext())
		{
			MyNodeType tmp = it.next();
			
			if (!types.contains(tmp))
				types.add(tmp);
		}
		
		Collections.sort(types, new MyNodeTypeComparator());
	}
	
	/**
	 * Search a NodeType by name
	 * @param name
	 * @return NodeType or null if there is no NodeType with the correct value
	 */
	public MyNodeType getValue(String name)
	{
		for (MyNodeType type : types)
		{
			if (type.getName().equals(name))
				return type;
			
		}
		
		//No value found
		return null;
	}
	
	public LinkedList<MyNodeType> getAllNodeTypes()
	{
		return types;
	}
	
	public MyNodeType[] getAllNodeTypesArray()
	{
		return types.toArray(new MyNodeType[types.size()]);
	}
	
	/**
	 * provides a possibility to compare the NodeTypes
	 * @author Luc
	 *
	 */
	protected class MyNodeTypeComparator implements Comparator<MyNodeType>
	{
	  @Override public int compare( MyNodeType type1, MyNodeType type2 )
	  {
	    return type1.getName().compareTo(type2.getName());
	  }
	}
}
