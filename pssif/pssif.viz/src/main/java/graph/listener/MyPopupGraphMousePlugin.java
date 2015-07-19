package graph.listener;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

import javax.swing.AbstractAction;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;

import model.ModelBuilder;
import reqtool.bus.ReqToolReqistry;
import reqtool.event.menu.CreateReqMenuEvent;
import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.AbstractPopupGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse.Mode;
import graph.model.IMyNode;
import graph.model.MyEdge;
import graph.model.MyNode;
import graph.model.MyNodeType;
import gui.graph.GraphVisualization;

/**
 * Creates the right click popups
 * @author Luc
 *
 */
public class MyPopupGraphMousePlugin extends AbstractPopupGraphMousePlugin {
	
	private GraphVisualization gViz;
	private MyNode sourceNode = null;

    public MyPopupGraphMousePlugin(GraphVisualization gViz) {
        this(MouseEvent.BUTTON3_MASK);
        this.gViz = gViz;
    }
    public MyPopupGraphMousePlugin(int modifiers) {
        super(modifiers);
    }
    
    
    public void mousePressed(MouseEvent e) {
    	
    	if(e.isPopupTrigger()) {
            handlePopup(e);
            e.consume();
        }
    	
    	if(e.getButton() == MouseEvent.BUTTON1 && this.gViz.getAbstractModalGraphMode() == Mode.EDITING)
    		sourceNode = getClickedNode(e);
    }
    
    
    
    public void mouseReleased(MouseEvent e) {
    	if(e.isPopupTrigger()) {
            handlePopup(e);
            e.consume();
        }
    	
    	if(e.getButton() == MouseEvent.BUTTON1 && this.gViz.getAbstractModalGraphMode() == Mode.EDITING)
    	{
    		MyNode destn = getClickedNode(e);
    		if (destn != null && sourceNode != null)
    		{
    			CreateEdgePopup popup = new CreateEdgePopup(sourceNode, destn, gViz);
    			sourceNode = null;
    			popup.showPopup();
    			gViz.setAbstractModalGraphMode(Mode.PICKING);
    		}
    	}
    }
    
	/**
     * if the user clicked somewhere on the graph canvas. What should be done
     */
    protected void handlePopup(MouseEvent e) {
        @SuppressWarnings("unchecked")
		VisualizationViewer<MyNode,MyEdge> vv = (VisualizationViewer<MyNode,MyEdge>)e.getSource();
        Point2D p = e.getPoint();
        GraphElementAccessor<MyNode,MyEdge> pickSupport = vv.getPickSupport();
        if(pickSupport != null) {
            MyNode node = pickSupport.getVertex(vv.getGraphLayout(), p.getX(), p.getY());
            MyEdge edge = pickSupport.getEdge(vv.getGraphLayout(), p.getX(), p.getY());
            // check where did the user click
			if (node != null) {
				// if the user made a right click on a Node
				JPopupMenu popup = new JPopupMenu();
				
				JMenu submenu = createEdge(e, node);
				JMenuItem submenuRemove = removeNode(e, node);
				popup.add(submenu);
				popup.add(submenuRemove);

				// if the user made a right click on a Reqtool Node
				ReqToolReqistry.getInstance().post(new CreateReqMenuEvent(node, popup, gViz));

				popup.show(vv, e.getX(), e.getY());
			} else if (edge != null) {
				JPopupMenu popup = new JPopupMenu();
				
				JMenuItem submenu = removeEdge(e, edge);
				popup.add(submenu);
				
				popup.show(vv, e.getX(), e.getY());
			}
            else {
            	// not on a node, so show the new Node popup
            	createNodePopUp(e);
            }
        }
    }
    
    
	/**
    * Create the popup which provides the user the possibility to add a Node
    * @param e The MouseEvent which triggered the action
    */
    @SuppressWarnings("serial")
	private void createNodePopUp( final MouseEvent e )
    {
        @SuppressWarnings("unchecked")
		VisualizationViewer<MyNode,MyEdge> vv = (VisualizationViewer<MyNode,MyEdge>) e.getSource();
    	JPopupMenu popup = new JPopupMenu();
        popup.add(new AbstractAction("Create Node") {
            public void actionPerformed(ActionEvent e2) {
            	createNode(e.getPoint());
            }

        });
        popup.show(vv, e.getX(), e.getY());
    }
    
    private void createNode(Point2D p)
    {
    	JTextField NodeName = new JTextField();

    	MyNodeType[] possibilities = ModelBuilder.getNodeTypes().getAllNodeTypesArray();
    	JComboBox<MyNodeType> Nodetype = new JComboBox<MyNodeType>(possibilities);
    	
    	JComponent[] inputs = new JComponent[] {
    			new JLabel("Node Name"),
    			NodeName,
    			new JLabel("Nodetype"),
    			Nodetype
    	};						
    	
    	JOptionPane.showMessageDialog(null, inputs, "Create new Node Dialog", JOptionPane.PLAIN_MESSAGE);
    	
    	// check if the user filled all the input field
    	if (NodeName.getText()!=null && NodeName.getText().length()>0)
    	{
    		IMyNode mynode = ModelBuilder.addNewNodeFromGUI(NodeName.getText(), (MyNodeType) Nodetype.getSelectedItem());
    		ModelBuilder.printVisibleStuff();
    		gViz.getVisualisationViewer().getGraphLayout().setLocation(mynode, p);
    		gViz.updateGraph();
    	}       
    }
  
    /**
     * provide the SubMenu options to create a new Edge
     * @param e The MouseEvent which triggered the action
     * @param selectedNode The Node which was selected when the user pushed the right mouse button
     * @return a menu with all the options to create a new Edge
     */
    private JMenu createEdge ( MouseEvent e, MyNode selectedNode)
    {
    	JMenu submenu = new JMenu("Add Edge");

       	LinkedList<MyNode> col = new LinkedList<MyNode>();
       	
       	col.addAll(ModelBuilder.getAllNodes());
       	// self loops are allowed
       	
       	LinkedList<String> destinations = new LinkedList<String>();
       	HashMap<String, MyNode> help = new HashMap<String, MyNode>();
       	for (MyNode cur : col)
       	{
       		destinations.add("To : "+cur.getRealName());
       		help.put("To : "+cur.getRealName(), cur);
       		
       	}
       	
       	// sort the destination Nodes by Name
       	Collections.sort(destinations);
       	
       	for (String dest : destinations)
       	{
       		JMenuItem menuItem = new JMenuItem(dest);
       		// create the action listener with the specific Edge Types
       		MyAddEdgeListener el = new MyAddEdgeListener(selectedNode, help.get(dest), gViz);
       		
       		menuItem.addActionListener(el);
       		submenu.add(menuItem);
       	}
           
       	return submenu;
    }
    
    /**
     * provide the SubMenu options to remove a node
     * @param e The MouseEvent which triggered the action
     * @param selectedNode The Node which was selected when the user pushed the right mouse button
     * @return a menu with all the option to trace a requirement
     */
   
    private JMenuItem removeNode(MouseEvent e, final MyNode selectedNode) {
    	JMenuItem submenu = new JMenuItem("Remove node");
    	submenu.addActionListener(new ActionListener() {
    		@Override
        	public void actionPerformed(ActionEvent e) {
    			ModelBuilder.removeNodeFromGUI(selectedNode);
    			ModelBuilder.printVisibleStuff();
        		gViz.updateGraph();
        	}
		});
 		return submenu;
    }
    
    private JMenuItem removeEdge(MouseEvent e, final MyEdge selectedEdge) {
    	JMenuItem submenu = new JMenuItem("Remove edge");
    	submenu.addActionListener(new ActionListener() {
    		@Override
        	public void actionPerformed(ActionEvent e) {
    			ModelBuilder.removeEdgeFromGUI(selectedEdge);
    			ModelBuilder.printVisibleStuff();
        		gViz.updateGraph();
        	}
		});
 		return submenu;
    }


    
    
    /**
     * Action listener for the Edge creation
     * @author Luc
     *
     */
    private class MyAddEdgeListener implements ActionListener
    {
    	private MyNode source;
    	private MyNode dest;
    	private GraphVisualization gViz;
    	
    	public MyAddEdgeListener (MyNode source, MyNode dest, GraphVisualization gViz)
    	{
    		this.source = source;
    		this.dest = dest;
    		this.gViz = gViz;
    	}
    	
		@Override
		public void actionPerformed(ActionEvent e) {
			CreateEdgePopup popup = new CreateEdgePopup(source, dest, gViz);

			popup.showPopup();			
		}
    }
    
    private MyNode getClickedNode(MouseEvent e)
    {
    	@SuppressWarnings("unchecked")
		VisualizationViewer<MyNode,MyEdge> vv = (VisualizationViewer<MyNode,MyEdge>)e.getSource();
        Point2D p = e.getPoint();
        GraphElementAccessor<MyNode,MyEdge> pickSupport = vv.getPickSupport();
        if(pickSupport != null) {
            MyNode node = pickSupport.getVertex(vv.getGraphLayout(), p.getX(), p.getY());
			return node;
        }
        return null;
    }
}