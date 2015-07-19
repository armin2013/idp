package gui.graph;

import graph.model.MyNodeType;
import gui.GraphView;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.Arrays;
import java.util.HashMap;

import javax.swing.JColorChooser;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import model.ModelBuilder;

public class NodeColorPopup extends MyPopup{
	
	private JPanel panel;
	private MyListColorRenderer colorlistener;
	private JList<MyNodeType> nodeTypeList;
	private GraphView graphView;
	
	public NodeColorPopup(GraphView graphView)
	{
		this.graphView = graphView;
	}
	
	/**
	 * Evaluate the Popup after the users input
	 * @param dialogResult the result of the users interaction with the popup gui
	 */
	private void evalDialog (int dialogResult)
	{
		if (dialogResult==0)
	 	{
	 		HashMap<MyNodeType, Color > colors = colorlistener.getColorMapping();
	 		graphView.getGraph().setNodeColorMapping(colors);
	 		
	 	}
	}
	
	/**
	 * Create the Panel(GUI) of the Popup 
	 * @return a panel with all the components
	 */
	private JPanel createPanel()
	{
		JPanel bannerPanel = new JPanel(new GridLayout());
		
		MyNodeType[] nodetypes = ModelBuilder.getNodeTypes().getAllNodeTypesArray();
		
		Arrays.sort(nodetypes, new MyNodeTypeComparator());
		
		nodeTypeList = new JList<MyNodeType>( nodetypes );  
	     
	    colorlistener = new MyListColorRenderer();
	    colorlistener.setColors(graphView.getGraph().getNodeColorMapping());
	    
	    nodeTypeList.setCellRenderer(colorlistener);
	    nodeTypeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	     
	    JScrollPane sp = new JScrollPane( nodeTypeList ); 
	    bannerPanel.add(sp);
			
	     //Set up color chooser for setting text color
	     final JColorChooser tcc = new JColorChooser();
	     tcc.getSelectionModel().addChangeListener(new ChangeListener() {
				
				@Override
				public void stateChanged(ChangeEvent e) {
					MyNodeType type = nodeTypeList.getSelectedValue();
					Color newColor = tcc.getColor();
					 
					if (type!=null)
					{
						colorlistener.setColor(type, newColor);
						nodeTypeList.repaint();
					}
				}
			});
	     
	     bannerPanel.add(tcc);
	     
	     return bannerPanel;
	}
	
	/**
	 * Display the Popup to the user
	 */
	public void showPopup()
	{
		panel = createPanel();
		
		int dialogResult = JOptionPane.showConfirmDialog(null, panel, "Choose Nodes Colors", JOptionPane.DEFAULT_OPTION);

		evalDialog(dialogResult);
	}
}
