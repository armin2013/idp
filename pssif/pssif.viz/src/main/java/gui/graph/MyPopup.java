package gui.graph;

import graph.model.MyEdgeType;
import graph.model.MyNodeType;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeMap;

/**
 * Provides basic functionalities for a Popup
 * Should not be instantiated
 * @author Luc
 *
 */
public abstract class MyPopup {
	
	/**
	 * Order the Edge Types first by their parent name, than after their own name.
	 * @param edgeTypes the Edge Types which should be filtered
	 * @return a TreeMap(EdgeType ParentName, List of EdgeType Object) which contains the ordered EdgeTypes
	 */
	protected TreeMap <String,LinkedList<MyEdgeType>> sortByEdgeTypeByParentType (LinkedList<MyEdgeType> edgeTypes)
	{
		TreeMap <String,LinkedList<MyEdgeType>> res = new TreeMap <String,LinkedList<MyEdgeType>>();
		
		TreeMap <String,LinkedList<MyEdgeType>> map = new TreeMap<String,LinkedList<MyEdgeType>>();
		
		// Add the the Edge Types to the map. Sorted by their respective parent
		for (MyEdgeType t : edgeTypes)
		{
			String parentName;
			if (t.getParentType()!=null)
			{
				parentName = t.getParentType().getName();
			}
			else
			{
				parentName="Other";
			}
			LinkedList<MyEdgeType> tmp = map.get(parentName);
			if (tmp == null)
				tmp = new LinkedList<MyEdgeType>();
			
			tmp.add(t);
			map.put(parentName, tmp);
		}
		
		
		Iterator<String> it = map.keySet().iterator();
		
		MyEdgeTypeComparator comp = new MyEdgeTypeComparator();
		
		// Order the EdgeTypes in the List by name
		while (it.hasNext())
		{
			String current = it.next();
			LinkedList<MyEdgeType> values = map.get(current);
			
			Collections.sort(values, comp);
			
			res.put(current, values);
		}
		
		System.out.println(res.size());
		
		return res;
	}
	
	/**
	 * provides a possibility to compare the EdgeTypes
	 * @author Luc
	 *
	 */
	public class MyEdgeTypeComparator implements Comparator<MyEdgeType>
	{
	  @Override public int compare( MyEdgeType type1, MyEdgeType type2 )
	  {
	    return type1.getName().compareTo(type2.getName());
	  }
	}
	
	/**
	 * Sort a given list of NodeTypes by their name
	 * @param types the list of NodeTypes
	 * @return the sorted List
	 */
	protected LinkedList<MyNodeType> sortNodeTypes (LinkedList<MyNodeType> types)
	{
		Collections.sort(types, new MyNodeTypeComparator());
		
		return types;
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
