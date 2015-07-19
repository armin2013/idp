package graph.model;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import de.tum.pssif.core.metamodel.JunctionNodeType;

public class MyJunctionNodeTypes {
private LinkedList<MyJunctionNodeType> types;
	
	public MyJunctionNodeTypes (Collection<JunctionNodeType> newtypes)
	{
		this.types = new LinkedList<MyJunctionNodeType>();
		
		for (JunctionNodeType ntype : newtypes)
		{
			MyJunctionNodeType tmp = new MyJunctionNodeType(ntype);
			
			if (!types.contains(tmp))
				types.add(tmp);
		}
		
		Collections.sort(this.types, new MyJunctionNodeTypeComparator());
	}
	
	public MyJunctionNodeTypes (HashSet<MyJunctionNodeType> newTypes)
	{
		this.types = new LinkedList<MyJunctionNodeType>();
		
		Iterator<MyJunctionNodeType> it = newTypes.iterator();
		
		while (it.hasNext())
		{
			MyJunctionNodeType tmp = it.next();
			
			if (!types.contains(tmp))
				types.add(tmp);
		}
		
		Collections.sort(types, new MyJunctionNodeTypeComparator());
	}
	
	/**
	 * Search a MyJunctionNodeType by name
	 * @param name
	 * @return MyJunctionNodeType or null if there is no MyJunctionNodeType with the correct value
	 */
	public MyJunctionNodeType getValue(String name)
	{
		for (MyJunctionNodeType type : types)
		{
			if (type.getName().equals(name))
				return type;
			
		}
		
		//No value found
		return null;
	}
	
	public LinkedList<MyJunctionNodeType> getAllJunctionNodeTypes()
	{
		return types;
	}
	
	public MyJunctionNodeType[] getAllJunctionNodeTypesArray()
	{
		return types.toArray(new MyJunctionNodeType[types.size()]);
	}
	
	/**
	 * provides a possibility to compare the MyJunctionNodeTypes
	 * @author Luc
	 *
	 */
	protected class MyJunctionNodeTypeComparator implements Comparator<MyJunctionNodeType>
	{
	  @Override public int compare( MyJunctionNodeType type1, MyJunctionNodeType type2 )
	  {
	    return type1.getName().compareTo(type2.getName());
	  }
	}
}
