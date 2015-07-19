package viewplugin.gui;

import graph.model.MyEdgeType;
import graph.model.MyNodeType;
import graph.operations.AttributeFilter;
import graph.operations.AttributeOperations;
import gui.graph.MyPopup;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.TreeMap;

import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import model.ModelBuilder;
import de.tum.pssif.core.metamodel.Attribute;
import de.tum.pssif.core.metamodel.DataType;
import de.tum.pssif.core.metamodel.PrimitiveDataType;
/**
 * Allows the user to specify an attribute filter for Nodes or Edges
 * @author Luc
 *
 */
public class AddAttributeFiltersPopup extends MyPopup {
	
	private MyNodeType[] nodePossibilities;
	private MyEdgeType[] edgePossibilities;
	private JPanel allPanel;
	private JComboBox<String> attributeList;
	private JComboBox<String> operationList;
	private JTextField valueTextField;
	private ButtonGroup group;
	private JRadioButton nodeFilter;
	private JRadioButton edgeFilter;
	
	private TreeMap<String, DataType> attributeNames;
	public static String newEdge ="Edge";
	public static String newNode ="Node";
	
	/**
	 * Show the Popup to the user
	 * @return "Edge" if an Edge Condition was added, "Node" of a Node condition was added, null otherwise
	 */
	public TreeMap<String, String[]> showPopup()
	{
		JPanel panel = createPanel();
		
		int dialogResult = JOptionPane.showConfirmDialog(null, panel, "Filter by Attribute", JOptionPane.DEFAULT_OPTION);
		
		return evalDialog(dialogResult);
	}
	
	/**
	 * Defines which compare operations are possible for the given Datatype
	 * @param type the datatype for which we have to define the compare possibilities
	 * @return a String array with all the possible compare operation names
	 */
	private String[] getPossibleOperations(DataType type)
	{
		String[] res;
		
		if (type.equals(PrimitiveDataType.BOOLEAN) || type.equals(PrimitiveDataType.STRING))
		{
			res = new String[]{AttributeOperations.EQUAL.toString(),AttributeOperations.NOT_EQUAL.toString()};
		}
		else
		{
			AttributeOperations[] tmp = AttributeOperations.values();
			
			res = new String[tmp.length];
			
			int i=0;
			for (AttributeOperations ao :tmp)
			{
				res[i] = ao.getName();
				i++;
						
			}
		}
		
		Arrays.sort(res);
		
		return res;
	}
	/**
	 * Create the Panel(GUI) of the Popup 
	 * @return a panel with all the components
	 */
	private JPanel createPanel()
	{	
		allPanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		int ypos =0;
		
		group = new ButtonGroup();
		nodeFilter = new JRadioButton("Node attributes");
		edgeFilter = new JRadioButton("Edge attributes");
		
		nodeFilter.setSelected(true);
		evalNodeSelection();
		
		nodeFilter.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				evalNodeSelection();
				updateAllFields();
			}
		});
		
		edgeFilter.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				evalEdgeSelection();
				updateAllFields();
			}
		});
		
		group.add(nodeFilter);
		group.add(edgeFilter);
		
		c.gridx= 0;
		c.gridy = ypos;
		allPanel.add(new JLabel("Filter by :"),c);
		
		ypos++;
		
		c.gridx= 1;
		c.gridy = ypos;
		allPanel.add(nodeFilter,c);
		c.gridx= 2;
		c.gridy = ypos;
		allPanel.add(edgeFilter,c);
		
		ypos++;
		c.gridx= 1;
		c.gridy = ypos;
		allPanel.add(new JLabel(""),c);
		
		ypos++;
		
	//	c.weighty = 1;
		
		// holds all the Attributes
		attributeList = new JComboBox<String>(attributeNames.keySet().toArray(new String[]{}));
		
		operationList = new JComboBox<String>(new String[]{});
		
		attributeList.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				 JComboBox<String> cb = (JComboBox<String>)e.getSource();
				 if (cb.getSelectedItem()!=null)
				 {
				     String attrName = (String)cb.getSelectedItem();
				     
				     DataType dataType = attributeNames.get(attrName);
				     
				     operationList.removeAllItems();
				     			     
				     for (String s : getPossibleOperations(dataType))
				     {
				    	 operationList.addItem(s);
				     }
				 }
			}
		});
		
		if (attributeList.getItemCount()>0)
			attributeList.setSelectedIndex(0);
		if (operationList.getItemCount()>0)
			operationList.setSelectedIndex(0);
		
	    valueTextField = new JTextField(10);
	    
		c.gridx = 0;
		c.gridy = ypos;
		allPanel.add(new JLabel("Choose an Attribute"),c);
		c.gridx = 1;
		c.gridy = ypos;
		allPanel.add(new JLabel("Choose an Operation"),c);
		c.gridx = 2;
		c.gridy = ypos++;
		allPanel.add(new JLabel("Enter a compare Value"),c);
		
		//c.weighty = 1;
		c.weightx = 1;
		
	    c.gridx = 0;
	    c.gridy = ypos;
	    allPanel.add(attributeList, c);
	    c.gridx = 1;
	    c.gridy = ypos;
	    allPanel.add(operationList, c);
	    c.gridx = 2;
	    c.gridy = ypos++;
	    allPanel.add(valueTextField, c);
	    
		
		allPanel.setPreferredSize(new Dimension(400,200));
		allPanel.setMaximumSize(new Dimension(400,200));
		allPanel.setMinimumSize(new Dimension(400,200));

		return allPanel;
	}
	
	private void evalNodeSelection()
	{
		nodePossibilities = ModelBuilder.getNodeTypes().getAllNodeTypesArray();
		//edgePossibilities =null;
		
		attributeNames = new TreeMap<String, DataType>();
		
		// get all the attributes and their type
		for (MyNodeType type : nodePossibilities)
		{
			LinkedList<Attribute> temp = new LinkedList<Attribute>(type.getType().getAttributes());
			
			for (Attribute attr :temp)
			{
				attributeNames.put(attr.getName(), attr.getType());
			}
			
		}
	}
	private void evalEdgeSelection()
	{
		edgePossibilities = ModelBuilder.getEdgeTypes().getAllEdgeTypesArray();
		//nodePossibilities=null;
		
		attributeNames = new TreeMap<String, DataType>();
		
		// get all the attributes and their type
		for (MyEdgeType type : edgePossibilities)
		{
			LinkedList<Attribute> temp = new LinkedList<Attribute>(type.getType().getAttributes());
			
			for (Attribute attr :temp)
			{
				attributeNames.put(attr.getName(), attr.getType());
			}
			
		}
	}
	
	private void updateAllFields()
	{
		valueTextField.setText("");
		
		attributeList.removeAllItems();
		for (String s: attributeNames.keySet().toArray(new String[]{}))
		{
			attributeList.addItem(s);
		}
		
		DataType dataType = attributeNames.get(attributeList.getItemAt(0));
	     
	    operationList.removeAllItems();
	     
	     
	     for (String s : getPossibleOperations(dataType))
	     {
	    	 operationList.addItem(s);
	     }
	}
	
	/**
	 * Evaluate the Popup after the users input
	 * @param dialogResult the result of the users interaction with the popup gui
	 * @return "Edge|"+condition if an Edge Condition was added, "Node|"+condition of a Node condition was added, null otherwise
	 */
	private TreeMap<String, String[]> evalDialog (int dialogResult)
	{
		if (dialogResult==0)
    	{
    		
			String selectedAttribute = (String)attributeList.getSelectedItem();
    		String selectedOperation = (String)operationList.getSelectedItem();
    		String refValue = valueTextField.getText();
    		
			if (refValue.length()==0)
			{
        		// not enough information
        		JPanel errorPanel = new JPanel();
        		
        		errorPanel.add(new JLabel("No name entered or no edge and node types selected"));
        		
        		JOptionPane.showMessageDialog(null, errorPanel, "Ups something went wrong", JOptionPane.ERROR_MESSAGE);
        		
        		return null;
			}
			else
			{
				// all information is available
				AttributeOperations op = AttributeOperations.getValueOf(selectedOperation);
				
				try
				{
					if (nodeFilter.isSelected())
					{
						AttributeFilter.filterNode( selectedAttribute, op, refValue);
						String condition = AttributeFilter.addNodeCondition(selectedAttribute, op, refValue);
						TreeMap<String, String[]> nodeAttributeFilters = new TreeMap<String, String[]>();
						String key = newNode+"|"+condition;
						String[] params = {selectedAttribute, op.getName(), refValue};
						nodeAttributeFilters.put(key, params);
						return nodeAttributeFilters;
					}

					if (edgeFilter.isSelected())
					{
						AttributeFilter.filterEdge( selectedAttribute, op, refValue);
						String condition = AttributeFilter.addEdgeCondition(selectedAttribute, op, refValue);
						TreeMap<String, String[]> edgeAttributeFilters = new TreeMap<String, String[]>();
						String key = newEdge+"|"+condition;
						String[] params = {selectedAttribute, op.getName(), refValue};
						edgeAttributeFilters.put(key, params);
						return edgeAttributeFilters;
					}
				}
				catch (Exception e)
				{
					//System.out.println(e.getMessage());
//					System.out.println("Here");
					JPanel errorPanel = new JPanel();
	        		
	        		errorPanel.add(new JLabel("There was a problem converting the entered value into the attribute data type"));
	        		
	        		JOptionPane.showMessageDialog(null, errorPanel, "Ups something went wrong", JOptionPane.ERROR_MESSAGE);
	        		return null;
				}
			}
    	}
		return null;
	}
}
