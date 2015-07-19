package gui.matrix;

import graph.model.MyEdgeType;
import graph.model.MyNodeType;
import gui.checkboxtree.CheckBoxTree;
import gui.graph.MyPopup;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.TreeMap;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;

import matrix.model.MatrixBuilder;
import model.ModelBuilder;

public class MatrixChooseNodeAndEgeTypePopup extends MyPopup{
	
	private JPanel NodePanel;
	private JPanel EdgePanel;
	private MatrixBuilder matrixBuilder;
	private CheckBoxTree tree;
	
	
	public MatrixChooseNodeAndEgeTypePopup(MatrixBuilder mbuilder)
	{
		this.matrixBuilder =mbuilder;
		this.tree = new CheckBoxTree();
	}
	
	/**
	 * Evaluate the Popup after the users input
	 * @param dialogResult the result of the users interaction with the popup gui
	 */
	private boolean evalDialog (int dialogResult)
	{
		if (dialogResult==0)
    	{
    		//System.out.println("new select of Nodes");
    		if (matrixBuilder.getRelevantNodeTypes() ==null)
    			matrixBuilder.setRelevantNodeTypes(new LinkedList<MyNodeType>());
    		if (matrixBuilder.getRelevantEdgesTypes() == null)
    			matrixBuilder.setRelevantEdgesTypes(new LinkedList<MyEdgeType>());
    		
    		// get all the values of the Nodes
        	Component[] attr = NodePanel.getComponents();       	
        	for (Component tmp :attr)
        	{
        		if ((tmp instanceof JCheckBox))
        		{
        			JCheckBox a = (JCheckBox) tmp;
        			
        			// compare which ones where selected
        			 if (a.isSelected())
        			 {
        				 MyNodeType b = ModelBuilder.getNodeTypes().getValue(a.getText());
        				 if (!matrixBuilder.getRelevantNodeTypes().contains(b))
        					 matrixBuilder.getRelevantNodeTypes().add(b);
        			 }
        			 else
        			 {
        				 MyNodeType b = ModelBuilder.getNodeTypes().getValue(a.getText());
        				 if (matrixBuilder.getRelevantNodeTypes().contains(b))
        					 matrixBuilder.getRelevantNodeTypes().remove(b);
        			 }
        				
        		}	
        	}
        	
    		// get all the values of the edges
        	LinkedList<MyEdgeType> selectedEdges = tree.evalTree();
        	matrixBuilder.setRelevantEdgesTypes(selectedEdges);
    		
        	// can we display something
    		if (matrixBuilder.getRelevantEdgesTypes().size()>0 && matrixBuilder.getRelevantNodeTypes().size()>0)
    			return true;
    	}

		return false;
	}
	
	/**
	 * Display the Popup to the user
	 */
	public boolean showPopup()
	{
		JPanel allPanel = createPanel();
		
		int dialogResult = JOptionPane.showConfirmDialog(null, allPanel, "Choose the Edge and Node types", JOptionPane.DEFAULT_OPTION);
		
		return evalDialog(dialogResult);
	}
	
	/**
	 * Create the Panel(GUI) of the Popup 
	 * @return a panel with all the components
	 */
	private JPanel createPanel()
	{
		LinkedList<MyNodeType> nodePossibilities = ModelBuilder.getNodeTypes().getAllNodeTypes();
		
		JPanel allPanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		NodePanel = new JPanel(new GridLayout(0, 1));
		
		nodePossibilities = sortNodeTypes(nodePossibilities);
		for (MyNodeType attr : nodePossibilities)
		{
			JCheckBox choice = new JCheckBox(attr.getName());
			if (matrixBuilder.getRelevantNodeTypes()!=null && matrixBuilder.getRelevantNodeTypes().contains(attr))
				choice.setSelected(true);
			else
				choice.setSelected(false);
			NodePanel.add(choice);
		}
		
		EdgePanel = new JPanel(new GridLayout(0, 1));
		
		TreeMap <String,LinkedList<MyEdgeType>> edges = this.sortByEdgeTypeByParentType(ModelBuilder.getEdgeTypes().getAllEdgeTypes());
		JTree edgeTree;
		if (matrixBuilder.getRelevantEdgesTypes()!=null)
		{
			edgeTree = tree.createTree(edges, matrixBuilder.getRelevantEdgesTypes());
		}
		else
		{
			edgeTree = tree.createTree(edges);
		}
		
		EdgePanel.add(edgeTree);
		
		JScrollPane scrollNodes = new JScrollPane(NodePanel,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollNodes.setPreferredSize(new Dimension(200, 400));
			    
	    JScrollPane scrollEdges = new JScrollPane(EdgePanel,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollEdges.setPreferredSize(new Dimension(200, 400));
		
		final JCheckBox selectAllNodes = new JCheckBox("Select all Node Types");
	    
	    selectAllNodes.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) 
		
	      {
	        if (selectAllNodes.isSelected())
	        {
	          Component[] attr = NodePanel.getComponents();
	          for (Component tmp : attr) {
	            if ((tmp instanceof JCheckBox))
	            {
	              JCheckBox a = (JCheckBox)tmp;
	              
	              a.setSelected(true);
	            }
	          }
	        }
	        else
	        {
	          Component[] attr = NodePanel.getComponents();
	          for (Component tmp : attr) {
	            if ((tmp instanceof JCheckBox))
	            {
	              JCheckBox a = (JCheckBox)tmp;
	              
	              a.setSelected(false);
	            }
	          }
	        }
	      }
	    });
		
		
	    if ( matrixBuilder.getRelevantNodeTypes()!=null &&
	    		matrixBuilder.getRelevantNodeTypes().containsAll(nodePossibilities))
	    	selectAllNodes.setSelected(true);
	    else
	    	selectAllNodes.setSelected(false);
	    
	    c.gridx = 0;
		c.gridy = 0;
		allPanel.add(new JLabel("Choose Node Types"),c);
		c.gridx = 1;
		c.gridy = 0;
		allPanel.add(new JLabel("Choose Edge Types"),c);
	    c.gridx = 0;
	    c.gridy = 1;
	    allPanel.add(scrollNodes, c);
	    c.gridx = 1;
	    c.gridy = 1;
	    allPanel.add(scrollEdges, c);
	    c.gridx = 0;
	    c.gridy = 2;
	    allPanel.add(selectAllNodes, c);
		
		allPanel.setPreferredSize(new Dimension(400,500));
		allPanel.setMaximumSize(new Dimension(400,500));
		allPanel.setMinimumSize(new Dimension(400,500));
		
		return allPanel;
	}
}
