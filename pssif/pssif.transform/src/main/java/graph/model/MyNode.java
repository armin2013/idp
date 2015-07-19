package graph.model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import de.tum.pssif.core.common.PSSIFConstants;
import de.tum.pssif.core.common.PSSIFOption;
import de.tum.pssif.core.common.PSSIFValue;
import de.tum.pssif.core.metamodel.Attribute;
import de.tum.pssif.core.metamodel.DataType;
import de.tum.pssif.core.metamodel.NodeType;
import de.tum.pssif.core.metamodel.NodeTypeBase;
import de.tum.pssif.core.metamodel.PrimitiveDataType;
import de.tum.pssif.core.model.Node;

/**
 * A Data container for the Nodes the PSS-IF Model
 * Helps to manage the visualization/modification of the Node
 * @author Luc
 *
 */
public class MyNode implements IMyNode{
	private Node node;
	private double sizeheight;
	private double sizewidth;
	private MyNodeType type;
	private boolean detailedOutput;
	private boolean visible;
	private boolean collapseNode;
	
	//private static int limit = 5;
	private static int lineLimit = 18;
	
	public MyNode(Node node, MyNodeType type) {
		this.node = node;
		this.type = type;
		this.visible = true;
		this.collapseNode = false;
		
		this.update();
	}
	
	/**
	 * Get all the Attributes from this node
	 * @return List with the attributes. Format : Name = Value in (Unit) Datatype 
	 */
	private List<String> calcAttr()
	{
		List<String> attributes = new LinkedList<String>();
		
		PSSIFOption<Attribute> tmp = type.getType().getAttribute(PSSIFConstants.BUILTIN_ATTRIBUTE_NAME);
		Attribute nodeName;
		if (tmp.isOne())
			nodeName = tmp.getOne();
		else
			nodeName =null;
		
		Collection<Attribute> attr = type.getType().getAttributes();
		
		for (Attribute current : attr)
		{
			if (!current.equals(nodeName))
			{
				String attrName = current.getName();
				
				PSSIFValue value=null;
				
				if (current.get(node)!=null && current.get(node).isOne())
					value = current.get(node).getOne();
				
				String attrValue="";
				if (value !=null)
				{
					if (((PrimitiveDataType)current.getType()).getName().equals("Date"))
					{
						DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
						attrValue= df.format(value.getValue());
					}
					else
						attrValue = String.valueOf(value.getValue());
				}
				
				String attrUnit = current.getUnit().getName();
				
				String res;
				
				if (attrUnit.equals("none"))
					res = attrName+" = "+attrValue;//+" : "+((PrimitiveDataType)current.getType()).getName();
				else
					res = attrName+" = "+attrValue+" in "+attrUnit;//+ " : "+((PrimitiveDataType)current.getType()).getName();
				
				if (current.get(node)!=null && attrValue.length()>0)
					attributes.add(res);
			}
		}
		
		return attributes;
	}
	
	/**
	 * Get a list with all the attributes from this Node
	 * @return A list which contains a list with all the attribute information. Information Order in the list : Name, Value, Unit, Datatype
	 */
	public LinkedList<LinkedList<String>> getAttributes()
	{
		LinkedList<LinkedList<String>> attributes = new LinkedList<LinkedList<String>>();
		
		
		Collection<Attribute> attr = type.getType().getAttributes();
		
		for (Attribute current : attr)
		{
			LinkedList<String> currentAttr = new LinkedList<String>();
			
			String attrName = current.getName();
			
			currentAttr.add(attrName);
			
			PSSIFValue value=null;
			
			if (current.get(node)!=null && current.get(node).isOne())
				value = current.get(node).getOne();
			
			String attrValue="";
			if (value !=null)
			{
				if (((PrimitiveDataType)current.getType()).getName().equals("Date"))
				{
					DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
					attrValue= df.format(value.getValue());
				}
				else
					attrValue = String.valueOf(value.getValue());
			}
				
			currentAttr.add(attrValue);
			String attrUnit = current.getUnit().getName();
			currentAttr.add(attrUnit);
			
			currentAttr.add(((PrimitiveDataType)current.getType()).getName());
			
			attributes.add(currentAttr);
		}
		
		return sortAttributes(attributes);
	}
	
	/**
	 * Update the value of a given attribute
	 * @param attributeName
	 * @param value
	 * @return true if everything went fine, otherwise false
	 */
	public boolean updateAttribute(String attributeName, Object value)
	{		
		PSSIFOption<Attribute> tmp = type.getType().getAttribute(attributeName);
		if (tmp.isOne())
		{
			Attribute attribute = tmp.getOne();
			DataType attrType = attribute.getType();
		
			if (attrType.equals(PrimitiveDataType.BOOLEAN))
			{
				try 
				{
					PSSIFValue res = PrimitiveDataType.BOOLEAN.fromObject(value);
					
					attribute.set(node, PSSIFOption.one(res));
				}
				catch (IllegalArgumentException e)
				{
					e.printStackTrace();
					return false;
				}
			}
			
			if (attrType.equals(PrimitiveDataType.DATE))
			{
				try 
				{
					Date data = parseDate((String) value);
				
					PSSIFValue res = PrimitiveDataType.DATE.fromObject(data);

					attribute.set(node, PSSIFOption.one(res));
				}
				catch (IllegalArgumentException e)
				{
					e.printStackTrace();
					return false;
				}
			}
			
			if (attrType.equals(PrimitiveDataType.DECIMAL))
			{
				try 
				{
					PSSIFValue res = PrimitiveDataType.DECIMAL.fromObject(value);
					
					attribute.set(node, PSSIFOption.one(res));
				}
				catch (IllegalArgumentException e)
				{
					e.printStackTrace();
					return false;
				}
			}
			
			if (attrType.equals(PrimitiveDataType.INTEGER))
			{
				try 
				{
					PSSIFValue res = PrimitiveDataType.INTEGER.fromObject(value);
					
					attribute.set(node, PSSIFOption.one(res));
	
				}
				catch (IllegalArgumentException e)
				{
					e.printStackTrace();
					return false;
				}
			}
			
			if (attrType.equals(PrimitiveDataType.STRING))
			{
				try 
				{
					PSSIFValue res = PrimitiveDataType.STRING.fromObject(value);
					
					attribute.set(node, PSSIFOption.one(res));
	
				}
				catch (IllegalArgumentException e)
				{
					e.printStackTrace();
					return false;
				}
			}
			
			this.update();
			return true;
		}

		return false;
	}
	
	/**
	 * We only accepts certain date formats. Checks different date formats and returns a date object
	 * @param dateInString
	 * @return a date object, if the given String is not coded in one of the given date formats, null is returned
	 */
	private Date parseDate(String dateInString)
	{
		SimpleDateFormat formatter;
		
		try {
			formatter = new SimpleDateFormat("dd/MM/yyyy");
			return formatter.parse(dateInString);
		} catch (ParseException e) {
			//e.printStackTrace();
		}
		
		try {
			formatter = new SimpleDateFormat("dd-MM-yyyy");
			return formatter.parse(dateInString);
		} catch (ParseException e) {
			//e.printStackTrace();
		}
		
		try {
			formatter = new SimpleDateFormat("dd.MM.yyyy");
			return formatter.parse(dateInString);
		} catch (ParseException e) {
			//e.printStackTrace();
		}
		//----------------
		
		try {
			formatter = new SimpleDateFormat("d/M/yyyy");
			return formatter.parse(dateInString);
		} catch (ParseException e) {
		//	e.printStackTrace();
		}
		
		try {
			formatter = new SimpleDateFormat("d-M-yyyy");
			return formatter.parse(dateInString);
		} catch (ParseException e) {
			//e.printStackTrace();
		}
		
		try {
			formatter = new SimpleDateFormat("d.M.yyyy");
			return formatter.parse(dateInString);
		} catch (ParseException e) {
			//e.printStackTrace();
		}
		//----------------------------------------------
		try {
			formatter = new SimpleDateFormat("d/MM/yyyy");
			return formatter.parse(dateInString);
		} catch (ParseException e) {
			//e.printStackTrace();
		}
		
		try {
			formatter = new SimpleDateFormat("d-MM-yyyy");
			return formatter.parse(dateInString);
		} catch (ParseException e) {
			//e.printStackTrace();
		}
		
		try {
			formatter = new SimpleDateFormat("d.MM.yyyy");
			return formatter.parse(dateInString);
		} catch (ParseException e) {
			//e.printStackTrace();
		}
		//----------------------------------------------
		try {
			formatter = new SimpleDateFormat("dd/M/yyyy");
			return formatter.parse(dateInString);
		} catch (ParseException e) {
			//e.printStackTrace();
		}
		
		try {
			formatter = new SimpleDateFormat("dd-M-yyyy");
			return formatter.parse(dateInString);
		} catch (ParseException e) {
			//e.printStackTrace();
		}
		
		try {
			formatter = new SimpleDateFormat("dd.M.yyyy");
			return formatter.parse(dateInString);
		} catch (ParseException e) {
			//e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * Get a HashMap with all the Attributes from this node
	 * @return A Mapping from Attributename to Attrbiute
	 */
	public HashMap<String, Attribute> getAttributesHashMap()
	{
		 HashMap<String, Attribute> res = new  HashMap<String, Attribute>();
		
		Collection<Attribute> attr = type.getType().getAttributes();
		
		for (Attribute current : attr)
		{
			String attrName = current.getName();
			
			res.put(attrName, current);
		}
		
		return res;
	}

	/**
	 * define the width and height of the Node during the graph viz, according to the node information (name, type,...)
	 */
	private void setSize()
	{
		int lineheight =30;
		
		//height
		
		sizeheight =0;
		//type
		sizeheight += lineheight;
		
		//name
		int namelines = nameLines(findName());
		
		sizeheight += namelines*lineheight ;
		
		//Attributes
		List<String> attr = calcAttr();
		if (isDetailedOutput())
		{
			//Attributes label
			sizeheight += lineheight;
			
			//Attributes
			int nbAttr = attr.size();
			
			sizeheight += nbAttr*lineheight;
		}
		
		sizewidth = 180;
	}
	
	/**
	 * After a change of the Node information, the visualization might be changed. Do an update
	 */
	public void update()
	{
		setSize();
	}
	
	/**
	 * Get all the Informations about the Node. Should only be used in the GraphVisualization
	 * @param details true show all details, false only name
	 * @return a HTML String with all the node informations
	 */
	public String getNodeInformations(boolean details)
	{
		String output="";
		if (details)
		{
			output ="<table border=\"0\">";
			output+=" <tr> ";
			output+= "<th> <h3>&lt;&lt; "+type.getName()+" &gt;&gt; <br>";
			output+= evalName(findName());
			output+= "</h3> </th> </tr>";
			output+=" <tr> ";
			output+= "<td> <b>Attributes</b></td>";
			output+=  " </tr> ";
			for (String s : calcAttr())
			{
				output+=" <tr> ";
				output+= "<td> "+s+" </td>";
				output+=  " </tr> ";
			}
			
			output+=" </table>";
		}
		else
		{
			output+="<h3>&lt;&lt; "+type.getName()+" &gt;&gt; <br>"+ evalName(findName())+"</h3>";
		}
		
		return output;
	}
	
	private String evalName(String name)
	{
		//String name = findName();
		
		if (name.length()>lineLimit)
		{
			String res;
			List<Integer> spaceIndexes = getSpaceIndexes(name);
			
			int previous =-1;
			for (int current : spaceIndexes)
			{
				if (current > lineLimit)
				{
					if (previous!=-1)
					{
						res = name.substring(0, previous)+" <br>";
						
						return res+evalName(name.substring(previous+1));
					}
					else
					{
						res = name.substring(0, lineLimit-1)+"- <br>";
						
						return res+evalName(name.substring(lineLimit));
					}						
				}
				else
				{
					previous=current;
				}
			}
			
			if (previous==-1)
			{
				res = name.substring(0, lineLimit-1)+"- <br>";
				
				return res+evalName(name.substring(lineLimit));
			}
			
			if (previous<=lineLimit)
			{
				res = name.substring(0, previous)+" <br>";
				
				return res+name.substring(previous+1);
			}
		}

		return name+" ";
	}
	
	private int nameLines(String name)
	{
		if (name.length()>lineLimit)
		{
			List<Integer> spaceIndexes = getSpaceIndexes(name);
			
			int previous =-1;
			for (int current : spaceIndexes)
			{
				if (current > lineLimit)
				{
					if (previous!=-1)
					{
						return 1+nameLines(name.substring(previous+1));
					}
					else
					{
						return 1+nameLines(name.substring(lineLimit));
					}						
				}
				else
				{
					previous=current;
				}
			}
			
			if (previous==-1)
			{
				return 1+nameLines(name.substring(lineLimit));
			}
			
			if (previous<=lineLimit)
			{
				return 2;
			}
		}

		return 1;
	}
	
	public List<Integer> getSpaceIndexes(String value)
	{
		int position = 0;
		
		List<Integer> res = new LinkedList<Integer>();
		
		int space = value.indexOf(" ");
		while (space !=-1)
		{
			position = position+space;
			
			res.add(position);
			
			value = value.substring(space+1);
			position++;
			
			space = value.indexOf(" ");
			
		}
		
		return res;
	}
	
/**
 * Pretty printed Name
 * @return a html name
 */
	public String getName()
	{
		String res = findName().replaceAll("&lt;", "<");
		res = res.replaceAll("<br>", "");
		
		return res;
	}
	
	/**
	 * Actual name value
	 * @return the name
	 */
	public String getRealName()
	{
		return findName();
	}
	
	/**
	 * Get the name from the Node object
	 * @return the actual name or "Name not available" if the name was not defined
	 */
	private String findName()
	{
		String name="Name not available";
		// find the name of the Node
		PSSIFOption<Attribute> tmp =type.getType().getAttribute(PSSIFConstants.BUILTIN_ATTRIBUTE_NAME);
		if (tmp.isOne())
		{
			Attribute nodeName = tmp.getOne();
			
			if (nodeName.get(node)!=null)
			{
				PSSIFValue value =null;
				if (nodeName.get(node).isOne())
				{
					value = nodeName.get(node).getOne();
					name = value.asString();
				}
				if (nodeName.get(node).isNone())
				{
					name ="Name not available";
				}
			}
			else
				name ="Name not available";
		}
			
		return name;
	}

	public double getHeight() {
		return sizeheight;
	}
	
	public double getWidth() {
		return sizewidth;
	}

	public MyNodeType getNodeType() {
		return type;
	}

	public boolean isDetailedOutput() {
		return detailedOutput;
	}
	
	/**
	 * Defines if all the attributes and additional information is shown in the GUI
	 * @param detailedOutput
	 */
	public void setDetailedOutput(boolean detailedOutput) {
		this.detailedOutput = detailedOutput;
		setSize();
	}

	public Node getNode() {
		return node;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public boolean isCollapseNode() {
		return collapseNode;
	}

	public void setCollapseNode(boolean collapseNode) {
		this.collapseNode = collapseNode;
	}
	
	private LinkedList<LinkedList<String>> sortAttributes(LinkedList<LinkedList<String>> data)
	{
		Collections.sort(data, new MyAttributeListComparator());
		
		return data;
	}
	
	protected class MyAttributeListComparator implements Comparator<LinkedList<String>>
	{
	  @Override public int compare( LinkedList<String> attr1, LinkedList<String> attr2 )
	  {
	    return attr1.getFirst().compareTo(attr2.getFirst());
	  }
	}

	@Override
	public NodeTypeBase getBaseNodeType() {
		return type.getType();
	}
}
