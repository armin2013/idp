package graph.operations;

import graph.model.MyEdge;
import graph.model.MyNode;

import java.awt.Dimension;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import model.ModelBuilder;
import de.tum.pssif.core.common.PSSIFConstants;
import de.tum.pssif.core.common.PSSIFOption;
import de.tum.pssif.core.common.PSSIFValue;
import de.tum.pssif.core.metamodel.Attribute;
import de.tum.pssif.core.metamodel.external.PSSIFCanonicMetamodelCreator;

public class AttributeAggregation {

	private Date node_earliestStartDate;
	private Date node_earliestEndDate;
	private Date node_latestStartDate;
	private Date node_latestEndDate; 
	
	private Date edge_earliestStartDate;
	private Date edge_earliestEndDate;
	private Date edge_latestStartDate;
	private Date edge_latestEndDate;
	
	private int numberOfNodes;
	private int numberOfEdges;
	
	private double global_Duration;
	private double global_Cost;
	private double global_Weight;
	
	private Date global_earliestStartDate;
	private Date global_latestEndDate;
	
	private void reset()
	{
		node_earliestStartDate= null;
		node_earliestEndDate= null;
		node_latestStartDate= null;
		node_latestEndDate= null; 
		
		edge_earliestStartDate= null;
		edge_earliestEndDate= null;
		edge_latestStartDate= null;
		edge_latestEndDate= null;
		
		global_Cost =0;
		global_Duration= 0;
		global_Weight = 0;
	}
	
	private void searchNodes()
	{
		LinkedList<MyNode> nodes = ModelBuilder.getAllNodes();
		
		for (MyNode currentNode: nodes)
		{
			HashMap<String,Attribute> attributes = currentNode.getAttributesHashMap();
			
			testNodeEndDate(currentNode, attributes);
			testNodeStartDate(currentNode, attributes);
			
			calcGlobalCost(currentNode, attributes);
			calcGlobalDuration(currentNode, attributes);
			calcGlobalWeight(currentNode, attributes);
		}
		
		numberOfNodes = nodes.size();
	}
	
	private void searchEdges()
	{
		LinkedList<MyEdge> edges = ModelBuilder.getAllEdges();
		
		for (MyEdge currentEdge : edges)
		{
			HashMap<String,Attribute> attributes = currentEdge.getAttributesHashMap();
			
			testEdgeEndDate(currentEdge,attributes);
			testEdgeStartDate(currentEdge, attributes);
		}
		
		numberOfEdges = edges.size();
	}
	
	private void testNodeStartDate(MyNode n, HashMap<String,Attribute> attributes)
	{
		
		PSSIFOption<PSSIFValue> tmp = attributes.get(PSSIFConstants.BUILTIN_ATTRIBUTE_VALIDITY_START).get(n.getNode());
		
		if (tmp.isOne())
		{
			Date startDate = tmp.getOne().asDate();
			
			
			if (node_earliestStartDate == null && node_latestStartDate ==null)
			{
				node_earliestStartDate = startDate;
				node_latestStartDate = startDate;
			}
			else
			{
					if (node_earliestStartDate.after(startDate))
						node_earliestStartDate = startDate;
					
					if (node_latestStartDate.before(startDate))
						node_latestStartDate = startDate;
			}
		}	
	}
	
	private void testNodeEndDate(MyNode n, HashMap<String,Attribute> attributes)
	{
		
		PSSIFOption<PSSIFValue> tmp = attributes.get(PSSIFConstants.BUILTIN_ATTRIBUTE_VALIDITY_END).get(n.getNode());
		
		if (tmp.isOne())
		{
			Date endDate = tmp.getOne().asDate();
			
			if (node_earliestEndDate == null && node_latestEndDate == null)
			{
				node_earliestEndDate = endDate;
				node_latestEndDate = endDate;
			}
			else
			{
					if (node_earliestEndDate.after(endDate))
						node_earliestEndDate = endDate;
					
					if (node_latestEndDate.before(endDate))
						node_latestEndDate = endDate;
			}
		}	
	}
	
	private void testEdgeStartDate(MyEdge e, HashMap<String,Attribute> attributes)
	{
		
		PSSIFOption<PSSIFValue> tmp = attributes.get(PSSIFConstants.BUILTIN_ATTRIBUTE_VALIDITY_START).get(e.getEdge());
		
		if (tmp.isOne())
		{
			Date startDate = tmp.getOne().asDate();
			
			if (edge_earliestStartDate == null && edge_latestStartDate==null)
			{
				edge_earliestStartDate = startDate;
				edge_latestStartDate = startDate;
			}
			else
			{
				if (edge_earliestStartDate.after(startDate))
					edge_earliestStartDate = startDate;
				
				if (edge_latestStartDate.before(startDate))
					edge_latestStartDate = startDate;
			}
		}	
	}
	
	private void testEdgeEndDate(MyEdge e, HashMap<String,Attribute> attributes)
	{
		PSSIFOption<PSSIFValue> tmp = attributes.get(PSSIFConstants.BUILTIN_ATTRIBUTE_VALIDITY_END).get(e.getEdge());
		
		if (tmp.isOne())
		{
			Date endDate = tmp.getOne().asDate();
			
			if (edge_earliestEndDate == null && edge_latestEndDate == null)
			{
				edge_earliestEndDate = endDate;
				edge_latestEndDate = endDate;
			}
			else
			{
				if (edge_earliestEndDate.after(endDate))
					edge_earliestEndDate = endDate;
				
				if (edge_latestEndDate.before(endDate))
					edge_latestEndDate = endDate;
			}
		}	
	}
	
	private void testEarliestAndLatestDate()
	{
		// earliest Startdate
		if (node_earliestStartDate == null && edge_earliestStartDate == null)
			global_earliestStartDate =null;
		else
		{
			if (node_earliestStartDate == null && edge_earliestStartDate!=null)
				global_earliestStartDate =edge_earliestStartDate;
			if (edge_earliestStartDate == null && node_earliestStartDate!=null)
				global_earliestStartDate =node_earliestStartDate;
			
			if (node_earliestStartDate != null && edge_earliestStartDate != null)
			{ 
				if (node_earliestStartDate.before(edge_earliestStartDate))
					global_earliestStartDate = node_earliestStartDate;
				else
					global_earliestStartDate = edge_earliestStartDate;
			}
		}
		
		// latest EndDate
		if (node_latestEndDate == null && edge_latestEndDate == null)
			global_latestEndDate =null;
		else
		{
			if (node_latestEndDate == null && edge_latestEndDate!=null)
				global_latestEndDate =edge_latestEndDate;
			if (edge_latestEndDate == null && node_latestEndDate!=null)
				global_latestEndDate =node_latestEndDate;
			
			if (node_latestEndDate != null && edge_latestEndDate != null)
			{ 
				if (node_latestEndDate.after(edge_latestEndDate))
					global_latestEndDate = node_latestEndDate;
				else
					global_latestEndDate = edge_latestEndDate;
			}
		}
	}
	
	private void calcGlobalCost (MyNode n, HashMap<String,Attribute> attributes)
	{
	//	HashMap<String,Attribute> attributes = n.getAttributesHashMap();
		
		if (attributes.get(PSSIFCanonicMetamodelCreator.TAGS.get("A_BLOCK_COST"))!=null)
		{
			PSSIFOption<PSSIFValue> tmp = attributes.get(PSSIFCanonicMetamodelCreator.TAGS.get("A_BLOCK_COST")).get(n.getNode());
		
			if (tmp.isOne())
			{
				BigDecimal cost = tmp.getOne().asDecimal();
				
				global_Cost = global_Cost + cost.doubleValue();
			}
		}
	}
	
	private void calcGlobalDuration (MyNode n, HashMap<String,Attribute> attributes)
	{
	//	HashMap<String,Attribute> attributes = n.getAttributesHashMap();
		
		if (attributes.get(PSSIFCanonicMetamodelCreator.TAGS.get("A_DURATION"))!=null)
		{
			PSSIFOption<PSSIFValue> tmp = attributes.get(PSSIFCanonicMetamodelCreator.TAGS.get("A_DURATION")).get(n.getNode());
			
			if (tmp.isOne())
			{
				BigDecimal duration = tmp.getOne().asDecimal();
				
				global_Duration = global_Duration + duration.doubleValue();
			}
		}
	}
	
	private void calcGlobalWeight (MyNode n, HashMap<String,Attribute> attributes)
	{
	//	HashMap<String,Attribute> attributes = n.getAttributesHashMap();
		
		if (attributes.get(PSSIFCanonicMetamodelCreator.TAGS.get("A_HARDWARE_WEIGHT"))!=null)
		{
			PSSIFOption<PSSIFValue> tmp = attributes.get(PSSIFCanonicMetamodelCreator.TAGS.get("A_HARDWARE_WEIGHT")).get(n.getNode());
			
			if (tmp.isOne())
			{
				BigDecimal weight = tmp.getOne().asDecimal();
				
				global_Weight = global_Weight + weight.doubleValue();
			}
		}
	} 
	
	public void showInfoPanel()
	{
		reset();
		searchEdges();
		searchNodes();
		testEarliestAndLatestDate();
		
		
		JPanel allPanel = new JPanel();
		allPanel.setLayout(new BoxLayout(allPanel, BoxLayout.Y_AXIS));
		
		allPanel.add(new JLabel("Statistics "));
		allPanel.add(new JLabel("Number of Nodes : "+numberOfNodes));
		allPanel.add(new JLabel("Number of Edges : "+numberOfEdges));
		
		allPanel.add(Box.createRigidArea(new Dimension(0,10)));
		allPanel.add(new JLabel("Nodes :"));
		if (node_earliestStartDate !=null)
			allPanel.add(new JLabel("Earliest Validity Start : "+datePrettyPrinter(node_earliestStartDate)));
		else
			allPanel.add(new JLabel("Earliest Validity Start : "));
		if (node_latestStartDate !=null)
			allPanel.add(new JLabel("Latest Validity Start : "+datePrettyPrinter(node_latestStartDate)));
		else
			allPanel.add(new JLabel("Latest Validity Start : "));
		if (node_earliestEndDate!=null)
			allPanel.add(new JLabel("Earliest Validity End : "+datePrettyPrinter(node_earliestEndDate)));
		else
			allPanel.add(new JLabel("Earliest Validity End : "));
		if (node_latestEndDate!=null)
			allPanel.add(new JLabel("Latest Validity End : "+datePrettyPrinter(node_latestEndDate)));
		else
			allPanel.add(new JLabel("Latest Validity End : "));
		
		allPanel.add(Box.createRigidArea(new Dimension(0,10)));
		allPanel.add(new JLabel("Edges :"));
		if (edge_earliestStartDate!=null)
			allPanel.add(new JLabel("Earliest Validity Start : "+datePrettyPrinter(edge_earliestStartDate)));
		else
			allPanel.add(new JLabel("Earliest Validity Start : "));
		if (edge_latestStartDate!=null)
			allPanel.add(new JLabel("Latest Validity Start : "+datePrettyPrinter(edge_latestStartDate)));
		else
			allPanel.add(new JLabel("Latest Validity Start : "));
		if (edge_earliestEndDate!=null)
			allPanel.add(new JLabel("Earliest Validity End : "+datePrettyPrinter(edge_earliestEndDate)));
		else
			allPanel.add(new JLabel("Earliest Validity End : "));
		if (edge_latestEndDate!=null)
			allPanel.add(new JLabel("Latest Validity End : "+datePrettyPrinter(edge_latestEndDate)));
		else
			allPanel.add(new JLabel("Latest Validity End : "));
		
		allPanel.add(Box.createRigidArea(new Dimension(0,10)));
		allPanel.add(new JLabel("Model :"));
		if (global_earliestStartDate!=null)
			allPanel.add(new JLabel("Earliest Validity Start : "+datePrettyPrinter(global_earliestStartDate)));
		else
			allPanel.add(new JLabel("Earliest Validity Start : "));
		if (global_latestEndDate!=null)
			allPanel.add(new JLabel("Latest Validity End : "+datePrettyPrinter(global_latestEndDate)));
		else
			allPanel.add(new JLabel("Latest Validity End : "));
		
		allPanel.add(new JLabel("Global Cost : "+global_Cost));
		allPanel.add(new JLabel("Global Weight : "+global_Weight));
		allPanel.add(new JLabel("Global Duration : "+secondsToTime(global_Duration)));
		
		allPanel.setPreferredSize(new Dimension(250,350));
		allPanel.setMaximumSize(new Dimension(250,350));
		allPanel.setMinimumSize(new Dimension(250,350));

		JOptionPane.showConfirmDialog(null, allPanel, "Model Statistics", JOptionPane.DEFAULT_OPTION);
	}
	
	private String secondsToTime(double time)
	{
		 int hr = (int)(time/3600);
		  int rem = (int)(time%3600);
		  int mn = rem/60;
		  int sec = rem%60;
		  
		  return hr+":"+mn+":"+sec;
	}
	
	private String datePrettyPrinter (Date date)
	{
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
		return  formatter.format(date);
		
	}
}
