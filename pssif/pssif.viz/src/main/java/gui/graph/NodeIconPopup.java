package gui.graph;

import graph.model.MyNodeType;
import gui.GraphView;

import java.awt.BorderLayout;
import java.util.Arrays;
import java.util.HashMap;

import javax.swing.Icon;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import model.ModelBuilder;

public class NodeIconPopup extends MyPopup{
	
	private JPanel panel;
	private JList<MyNodeType> nodeTypeList;
	private HashMap<MyNodeType, Icon> iconMapper;
	private GraphView graphView;
	public ImageImporter ii;
	
	public NodeIconPopup(GraphView graphView)
	{
		this.graphView = graphView;
		ii = new ImageImporter(this);
		iconMapper = graphView.getGraph().getNodeIconMapping();
	}
	

	
	private void evalDialog (int dialogResult)
	{
		if (dialogResult==0)
	 	{
	 		graphView.getGraph().setNodeIconMapping(iconMapper);
	 		
	 	}
	}
	
	
	
	/**
	 * Create the Panel(GUI) of the Popup 
	 * @return a panel with all the components
	 */
	private JPanel createPanel()
	{
		JPanel bannerPanel = new JPanel(new BorderLayout());
		
		MyNodeType[] nodetypes = ModelBuilder.getNodeTypes().getAllNodeTypesArray();
		
		Arrays.sort(nodetypes, new MyNodeTypeComparator());
		
		nodeTypeList = new JList<MyNodeType>( nodetypes );  
	    nodeTypeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	    
	    ListSelectionHandler lsh = new ListSelectionHandler(this);
	    nodeTypeList.addListSelectionListener(lsh);
	     
	    JScrollPane sp = new JScrollPane( nodeTypeList ); 
	    bannerPanel.add(sp, BorderLayout.WEST);
	    
	    bannerPanel.add(ii, BorderLayout.EAST);
	    bannerPanel.setSize(100, 400);
	    return bannerPanel;
	}
	
	/**
	 * Evaluate the Popup after the users input
	 * @param dialogResult the result of the users interaction with the popup gui
	 */
	

	/**
	 * Display the Popup to the user
	 */
	public void showPopup()
	{
		panel = createPanel();
		
		int dialogResult = JOptionPane.showConfirmDialog(null, panel, "Choose Nodes Shapes", JOptionPane.DEFAULT_OPTION);
		
		
		evalDialog(dialogResult);
	}


	public HashMap<MyNodeType, Icon> getIconMapper() {
		return iconMapper;
	}


	public void setIconMapper(HashMap<MyNodeType, Icon> iconMapper) {
		this.iconMapper = iconMapper;
	}
}
