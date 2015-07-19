package gui.graph;

import graph.model.MyEdgeType;
import graph.model.MyNodeType;
import graph.operations.GraphViewContainer;
import graph.operations.MasterFilter;
import gui.GraphView;
import gui.checkboxtree.CheckBoxTree;

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
import javax.swing.JTextField;
import javax.swing.JTree;

import model.ModelBuilder;

/**
 * Provides the functionality to create a new View on the Graph
 * @author Luc
 *
 */
public class CreateNewGraphViewPopup extends MyPopup{
	
	private JPanel NodePanel;
	private JPanel EdgePanel;
	private JTextField viewNameTextField;
	private GraphView graphView;
	private CheckBoxTree tree;
	private MasterFilter masterFilter;
	
	public CreateNewGraphViewPopup(GraphView graphView, MasterFilter masterFilter)
	{
		this.graphView = graphView;
		tree = new CheckBoxTree();
		this.masterFilter = masterFilter;
	}
	
	/**
	 * Evaluate the Popup after the users input
	 * @param dialogResult the result of the users interaction with the popup gui
	 * @return true if the View was successfully created, false otherwise
	 */
	private boolean evalDialog (int dialogResult)
	{
		if (dialogResult==0)
    	{
    		LinkedList<MyNodeType> selectedNodes = new LinkedList<MyNodeType>();
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
        				 selectedNodes.add(b);
        			 }
        				
        		}	
        	}
        	
        	LinkedList<MyEdgeType> selectedEdges = tree.evalTree();        	
        	
        	String viewName = viewNameTextField.getText();
        	
        	if (viewName.length()==0 || (selectedNodes.size()==0 && selectedEdges.size()==0))
        	{
        		// not enough information
        		JPanel errorPanel = new JPanel();
        		
        		errorPanel.add(new JLabel("No name entered or no edge and node types selected"));
        		
        		JOptionPane.showMessageDialog(null, errorPanel, "Ups something went wrong", JOptionPane.ERROR_MESSAGE);
        		
        		
        	}
        	else
        	{
        		// write to config
        		GraphViewContainer container = new GraphViewContainer(selectedNodes,selectedEdges,viewName);
	        	graphView.getGraph().createNewGraphView(container);
	        	
	        	masterFilter.addNodeAndEdgeTypeFilter(selectedNodes, selectedEdges, viewName, false);
	        	// apply the view
	        	//graphView.getGraph().applyNodeAndEdgeFilter(container.getSelectedNodeTypes(), container.getSelectedEdgeTypes(), viewName);
	        	
	        	return true;
        	}
			
        }
			
		return false;
	}
	
	/**
	 * Show the Popup to the user
	 * @return
	 */
	public boolean showPopup()
	{
		JPanel allPanel = createPanel();
		
		int dialogResult = JOptionPane.showConfirmDialog(null, allPanel, "Create a new Graph View", JOptionPane.DEFAULT_OPTION);
		
		return evalDialog(dialogResult);
	}
	
	/**
	 * Create the Panel(GUI) of the Popup 
	 * @return a panel with all the components
	 */
	private JPanel createPanel()
	{		
		LinkedList<MyNodeType> nodePossibilities = ModelBuilder.getNodeTypes().getAllNodeTypes();
		
		nodePossibilities = sortNodeTypes(nodePossibilities);
		
		JPanel allPanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		NodePanel = new JPanel(new GridLayout(0, 1));
		
		for (MyNodeType attr : nodePossibilities)
		{
			JCheckBox choice = new JCheckBox(attr.getName());
			
			choice.setSelected(false);
			NodePanel.add(choice);
		}
		
		EdgePanel = new JPanel(new GridLayout(0, 1));
		
		TreeMap<String, LinkedList<MyEdgeType>> sortedEdges = sortByEdgeTypeByParentType(ModelBuilder.getEdgeTypes().getAllEdgeTypes());
		
		JTree tmpTree = tree.createTree(sortedEdges);

		EdgePanel.add(tmpTree);
		
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
		
	    selectAllNodes.setSelected(false);
	    
	    viewNameTextField = new JTextField(10);
	    
	    int ypos =0;
	    
		c.gridx = 0;
		c.gridy = ypos;
		allPanel.add(new JLabel("Choose Node Types"),c);
		c.gridx = 1;
		c.gridy = ypos++;
		allPanel.add(new JLabel("Choose Edge Types"),c);
	    c.gridx = 0;
	    c.gridy = ypos;
	    allPanel.add(scrollNodes, c);
	    c.gridx = 1;
	    c.gridy = ypos++;
	    allPanel.add(scrollEdges, c);
	    c.gridx = 0;
	    c.gridy = ypos;
	    allPanel.add(selectAllNodes, c);
	    c.gridx = 1;
	    c.gridy = ypos++;
	    
	    c.weighty = 1;
	    
		c.gridx = 0;
		c.gridy = ypos;
		allPanel.add(new JLabel("Graph View name "),c);
		c.gridx = 1;
		c.gridy = ypos++;
		allPanel.add(viewNameTextField,c);

		
		allPanel.setPreferredSize(new Dimension(400,500));
		allPanel.setMaximumSize(new Dimension(400,500));
		allPanel.setMinimumSize(new Dimension(400,500));
		
		return allPanel;
	}
	
	
}
