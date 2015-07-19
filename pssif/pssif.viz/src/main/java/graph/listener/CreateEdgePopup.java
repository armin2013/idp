package graph.listener;

import graph.model.IMyNode;
import graph.model.MyEdgeType;
import graph.model.MyNode;
import gui.graph.GraphVisualization;
import gui.graph.MyPopup;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Arrays;

import javax.swing.Box;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import model.ModelBuilder;

public class CreateEdgePopup extends MyPopup{
	
	private MyNode source;
	private MyNode dest;
	private GraphVisualization gViz;
	
	private JComboBox<MyEdgeType> edgeTypeDropBox;
	private JComboBox<Boolean> directedEdgeDropBox;
	
	public CreateEdgePopup(MyNode source, MyNode dest, GraphVisualization gViz)
	{
		this.source = source;
		this.dest = dest;
		this.gViz = gViz;
	}
	/**
	 * Evaluate the Popup after the users input
	 * @param dialogResult the result of the users interaction with the popup gui
	 */
	private void evalDialog (int dialogResult)
	{
		if (dialogResult==0)
    	{
			// Build the Edge
			MyEdgeType selectedEdgeType = (MyEdgeType) edgeTypeDropBox.getModel().getSelectedItem();
			Boolean directed = (Boolean) directedEdgeDropBox.getModel().getSelectedItem();
			
			boolean res = ModelBuilder.addNewEdgeGUI(source, dest, selectedEdgeType, directed);
			if (res)
				gViz.updateGraph();
			else
			{
				JPanel errorPanel = new JPanel();
        		
        		errorPanel.add(new JLabel("This edge type is not allowed between these nodes in this model"));
        		
        		JOptionPane.showMessageDialog(null, errorPanel, "Ups something went wrong", JOptionPane.ERROR_MESSAGE);
			}
			
        }
	}
	
	/**
	 * Show the Popup to the user
	 */
	public void showPopup()
	{
		JPanel allPanel = createPanel();
		
		int dialogResult = JOptionPane.showConfirmDialog(null, allPanel, "Choose an Edge Type", JOptionPane.DEFAULT_OPTION);
		
		evalDialog(dialogResult);
	}
	
	/**
	 * Create the Panel(GUI) of the Popup 
	 * @return a panel with all the components
	 */
	private JPanel createPanel()
	{		
		MyEdgeType[] possibilities = ModelBuilder.getPossibleEdges(source.getNodeType().getType(), dest.getNodeType().getType()).toArray(new MyEdgeType[0]);
		
		Arrays.sort(possibilities, new MyEdgeTypeComparator());
		
		JPanel allPanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		
		JLabel EdgeTypeLabel = new JLabel("Select an EdgeType");
		
		edgeTypeDropBox = new JComboBox<MyEdgeType>(possibilities);
		
		JLabel DirectedEdgeLabel = new JLabel("Should the Edge be directed?");
		
		Boolean[] boolpos = new Boolean[]{true, false};
		directedEdgeDropBox = new JComboBox<Boolean>(boolpos);
		
		int ypos =0;
		
		c.gridx = 0;
		c.gridy = ypos;
		ypos++;
		allPanel.add(EdgeTypeLabel,c);
		c.gridx = 0;
		c.gridy = ypos;
		ypos++;
		allPanel.add(edgeTypeDropBox,c);
		
		c.gridy = ypos;
		ypos++;
		allPanel.add(Box.createVerticalStrut(7), c);
		
		c.gridx = 0;
		c.gridy = ypos;
		ypos++;
		allPanel.add(DirectedEdgeLabel,c);
		c.gridx = 0;
		c.gridy = ypos;
		ypos++;
		allPanel.add(directedEdgeDropBox,c);
		
		allPanel.setPreferredSize(new Dimension(200,250));
		allPanel.setMaximumSize(new Dimension(200,250));
		allPanel.setMinimumSize(new Dimension(200,250));
		
		return allPanel;
	}
}
