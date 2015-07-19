package gui.graph;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import javax.swing.Icon;
import javax.swing.JMenu;

import org.apache.commons.collections15.Transformer;

import reqtool.controller.RequirementTracer;
import reqtool.graph.operations.ReqTraceVertexStrokeHighlight;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.AbstractModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse.Mode;
import edu.uci.ics.jung.visualization.layout.LayoutTransition;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;
import edu.uci.ics.jung.visualization.util.Animator;
import graph.listener.ConfigWriterReader;
import graph.listener.MyPopupGraphMousePlugin;
import graph.listener.NodeIconConfigWriterReader;
import graph.model.GraphBuilder;
import graph.model.IMyNode;
import graph.model.MyEdge;
import graph.model.MyEdgeType;
import graph.model.MyNode;
import graph.model.MyNodeType;
import graph.operations.GraphViewContainer;
import graph.operations.MyCollapser;
import graph.operations.VertexStrokeHighlight;
import gui.enhancement.EnhancedRenderer;
import gui.enhancement.EnhancedVisualizationViewer;
import gui.enhancement.VertexShapeManager;

/**
 * The GraphVisualization is the Connection between the Jung2 Framework and the PSS-IF Model
 * @author Luc
 *
 */
public class GraphVisualization
{
	public static final String KKLayout ="KKLayout";
	public static final String FRLayout  ="FRLayout";
	public static final String SpringLayout  ="SpringLayout";
	public static final String ISOMLayout  ="ISOMLayout";
	public static final String CircleLayout  ="CircleLayout";
	public static final String TestLayout  ="TestLayout";


	private Graph<IMyNode, MyEdge> g;
	private AbstractModalGraphMouse gm;
	private VisualizationViewer<IMyNode, MyEdge> vv;
	private Layout<IMyNode, MyEdge> layout;
	private VertexStrokeHighlight<IMyNode, MyEdge> vsh;
	private GraphBuilder gb;
	private boolean detailedNodes;
	private MyCollapser collapser;
	private boolean vertexLabelVisibility;

	public HashMap<MyNodeType, Shape> nodeShapeMapping;
	private HashMap<MyNodeType,Color> nodeColorMapping;
	private HashMap<MyNodeType, Icon> nodeIconMapping;
	
	private ConfigWriterReader configWriterReader;
	private NodeIconConfigWriterReader nodeIconConfigWriterReader;


	public GraphVisualization(Dimension d, boolean details)
	{ 

		int i = 1000;
		this.detailedNodes = details;
		this.gb = new GraphBuilder();
		this.collapser = new MyCollapser();
		configWriterReader = new ConfigWriterReader();
		nodeIconConfigWriterReader = new NodeIconConfigWriterReader();
		this.nodeColorMapping = configWriterReader.readColors();
		this.nodeIconMapping = nodeIconConfigWriterReader.readIcons();

		this.g =  this.gb.createGraph(this.detailedNodes);
		this.vertexLabelVisibility = true;

		this.layout = new FRLayout<IMyNode, MyEdge>(this.g);
		if (d != null)
		{
			int x = (int)d.getWidth() - 50;
			int y = (int)d.getHeight() - 50;
			this.layout.setSize(new Dimension(x, y));
		}
		else
		{
			this.layout.setSize(new Dimension(i, i));
		}

		//new settings here
		this.vv = new EnhancedVisualizationViewer(this.layout);
		EnhancedVisualizationViewer evv = (EnhancedVisualizationViewer) this.vv; 
		evv.setGraphVisualization(this);
		vv.getRenderer().setVertexRenderer(new EnhancedRenderer<IMyNode,MyEdge>());
		
		if (d != null) 
		{
			this.vv.setPreferredSize(d);
		}
		else 
		{
			this.vv.setPreferredSize(new Dimension(i, i));
		}

		//set the first node in the center of screen
		if (this.layout != null && this.g.getVertexCount() < 2)
			this.layout.setInitializer(new Transformer<IMyNode, Point2D>() {
				public Point2D transform(IMyNode node) {
					Point2D p = vv.getCenter();
					return p;
					}
				});
		

		this.vsh = new VertexStrokeHighlight<IMyNode,MyEdge>(this.g, this.vv.getPickedVertexState()); 
		this.vsh.setHighlight(true, 1, new LinkedList<MyEdgeType>());

		createNodeTransformers();
		createEdgeTransformers();

		this.gm = new EnhancedModalGraphMouse<MyNode, MyEdge>();
		
		this.vv.setGraphMouse(this.gm);   
		this.gm.add(new MyPopupGraphMousePlugin(this));
	}


	public boolean flipVertexLabelVisibility() {
		this.vertexLabelVisibility = !this.vertexLabelVisibility;
		return this.vertexLabelVisibility;
	}

	/**
	 * Create all Transformes which help to visualize the Nodes
	 */
	Transformer<IMyNode, Paint> vertexColor = new Transformer<IMyNode, Paint>() {
		public Paint transform(IMyNode i) {

			if (nodeColorMapping != null && i instanceof MyNode) {
				MyNode node = (MyNode) i;
				Color c = nodeColorMapping.get(node.getNodeType());
				if (c != null)
					return c;
			}

			return Color.LIGHT_GRAY;
		}
	};

	private void createNodeTransformers()
	{
		Transformer<IMyNode, Shape> vertexSize = new Transformer<IMyNode, Shape>()
				{
			public Shape transform(IMyNode node)
			{
				//remeber to put this into constructor
				VertexShapeManager vsm = new VertexShapeManager(nodeShapeMapping);
				return vsm.selectShape(node);

			}
				};

		Transformer<IMyNode, String> vertexLabelTransformer =  new Transformer<IMyNode, String>()
				{
			public String transform(IMyNode node)
			{
				if (vertexLabelVisibility)
					return "<html>" + node.getNodeInformations(node.isDetailedOutput());
				else 
					return "<html>"+node.getName();
			}
				};


		Transformer<IMyNode,String> vertexToolTippTransformer =new Transformer<IMyNode,String>()
				{
			public String transform(IMyNode n) 
			{
				if (!n.isDetailedOutput())
					return "<html>" + n.getNodeInformations(true);
				else
					return "";
			}
				};

				
		Transformer<IMyNode, Icon> vertexIconTransformer = new Transformer<IMyNode, Icon>()
				{
			public Icon transform(IMyNode n)
			{
				if (nodeIconMapping != null && n instanceof MyNode) 
				{
					MyNode node = (MyNode) n;
					Icon icon = nodeIconMapping.get(node.getNodeType());
					//System.out.println(icon);
					return icon;
				}
				return null;
			}
				};
		//Vertex
		this.vv.getRenderContext().setVertexIconTransformer(vertexIconTransformer);
		this.vv.getRenderContext().setVertexFillPaintTransformer(vertexColor);
		this.vv.getRenderContext().setVertexShapeTransformer(vertexSize);
		this.vv.getRenderContext().setVertexLabelTransformer(vertexLabelTransformer);
		this.vv.getRenderContext().setVertexStrokeTransformer(this.vsh);
		this.vv.getRenderer().getVertexLabelRenderer().setPosition(Position.CNTR);
		this.vv.setVertexToolTipTransformer(vertexToolTippTransformer);
	}

	/**
	 * Create all Transformes which help to visualize the Edges
	 */
	private void createEdgeTransformers()
	{
		// Set up a new stroke Transformer for the edges
		Transformer<MyEdge, Stroke> edgeTransformer = new Transformer<MyEdge, Stroke>()
				{
			public Stroke transform(MyEdge edge)
			{
				int res = edge.getEdgeType().getLineType() % 4;
				BasicStroke b = null;

				switch (res)
				{
				case 0: 
					return RenderContext.DASHED;
				case 1: 
					return RenderContext.DOTTED;
				case 2: 
					b = new BasicStroke(1.0F, 0, 2, 0.0F, new float[] { 20.0F, 12.0F }, 0.0F);
					return b;
				case 3: 
					b = new BasicStroke(5.0F, 0, 2, 0.0F, new float[] { 1.0F, 1.0F }, 0.0F);
					return b;
				}
				b = new BasicStroke(1.0F, 0, 2, 0.0F, new float[] { 1.0F, 1.0F }, 0.0F);
				return b;
			}
				};

				Transformer<MyEdge, String> edgeLabelTransformer =  new Transformer<MyEdge, String>()
						{
					public String transform(MyEdge edge)
					{
						return "<html>" + edge.getEdgeTypeName();
					}
						};

						Transformer<MyEdge, Paint> edgePaint = new Transformer<MyEdge, Paint>()
								{
							public Paint transform(MyEdge edge)
							{
								int res = edge.getEdgeType().getLineType() % 8;
								switch (res)
								{
								case 0: 
									return Color.BLACK;
								case 1: 
									return Color.RED;
								case 2: 
									return Color.BLUE;
								case 3: 
									return Color.CYAN;
								case 4: 
									return Color.GREEN;
								case 5: 
									return Color.MAGENTA;
								case 6: 
									return Color.ORANGE;
								case 7: 
									return Color.PINK;
								}
								return Color.BLACK;
							}
								};

								Transformer<MyEdge,String> edgeToolTippTransformer =new Transformer<MyEdge,String>(){
									public String transform(MyEdge e) {
										return "<html>" + e.getEdgeInformations();
									}
								};

								//Edge
								this.vv.getRenderContext().setEdgeStrokeTransformer(edgeTransformer);
								this.vv.getRenderContext().setEdgeLabelTransformer(edgeLabelTransformer);
								this.vv.getRenderContext().setEdgeDrawPaintTransformer(edgePaint);
								this.vv.setEdgeToolTipTransformer(edgeToolTippTransformer);
	}

	/**
	 * Get the Jung2 Graph Visualization
	 * @return the Jung2 VisualizationViewer
	 */
	public VisualizationViewer<IMyNode, MyEdge> getVisualisationViewer()
	{
		return this.vv;
	}



	/**
	 * Set the different Mouse Operation Modes of the Jung2 Framework (Picking and Transforming)
	 */
	public void setAbstractModalGraphMode(Mode m)
	{
		gm.setMode(m);
	}

	public Mode getAbstractModalGraphMode()
	{
		@SuppressWarnings("unchecked")
		EnhancedModalGraphMouse<MyNode, MyEdge> temp = (EnhancedModalGraphMouse<MyNode, MyEdge>) this.gm;
		return temp.getMode();
	}



	/**
	 * Get the different Mouse Operation Modes of the Jung2 Framework (Picking and Transforming)
	 * @return a JMenu which holds the Operation Modes
	 */
	public JMenu getMouseOperationModes()
	{
		return this.gm.getModeMenu();
	}

	/**
	 * Change the MyNode Appearance
	 * @param details true if all the Node details should be visible, false only minimal Node details should be displayed
	 */
	public void setNodeVisualisation(boolean details)
	{
		if (details != this.detailedNodes)
		{
			this.detailedNodes = details;

			if (collapser.CollapserActive())
				this.g = this.gb.changeNodeDetails(details, g);
			else
				this.g = this.gb.createGraph(details);

			this.updateGraph();
			this.vv.repaint();
		}
	}
	/**
	 * Which Edge Types should be followed during highlight
	 * @param types List with all the Edge Types which should be followed
	 */
	public void setFollowEdgeTypes(LinkedList<MyEdgeType> types)
	{
		setFollowEdgeTypes(types,vsh.getSearchDepth());
	}

	/**
	 * Which Edge Types should be followed during highlight
	 * @param types List with all the Edge Types which should be followed
	 * @param depth how deep should the Edges be followed
	 */
	public void setFollowEdgeTypes(LinkedList<MyEdgeType> types, int depth)
	{
		this.vsh = new VertexStrokeHighlight<IMyNode, MyEdge>(g, this.vv.getPickedVertexState());
		this.vsh.setHighlight(true, depth, types);
		this.vv.getRenderContext().setVertexStrokeTransformer(this.vsh);
		this.vv.repaint();
	}

	/**
	 * Which Edge Types should be followed during highlight
	 * @param depth how deep should the Edges be followed
	 */
	public void setFollowEdgeTypes(int depth)
	{
		setFollowEdgeTypes(vsh.getFollowEdges(), depth);
	}

	/**
	 * Get the Edge Types which are followed during the highlighting
	 * @return List with Edge Types
	 */
	public LinkedList<MyEdgeType> getFollowEdgeTypes() {
		return this.vsh.getFollowEdges();
	}

	/**
	 * Which Nodes should be followed during requirements traceability
	 * 
	 * @return List with Edge Types
	 */
	@SuppressWarnings("unchecked")
	public void traceNodes() {
		@SuppressWarnings("unused")
		Transformer<IMyNode, Paint> tracedVertexColor = new Transformer<IMyNode, Paint>() {
			public Paint transform(IMyNode i) {
				if (RequirementTracer.isTracedNode(i)) {
					return Color.GREEN;
				} else if (RequirementTracer.isOnTraceList(i)) {
					return Color.CYAN;
				} else {
					Color c = nodeColorMapping.get(((MyNode) i).getNodeType());
					if (c != null)
						return c;
				}
				return Color.LIGHT_GRAY;
			}
		};
		@SuppressWarnings("rawtypes")
		ReqTraceVertexStrokeHighlight<IMyNode, Paint> trans = new ReqTraceVertexStrokeHighlight(g, vertexColor);
		trans.setHighlight(true);
		this.vv.getRenderContext().setVertexFillPaintTransformer(trans);
		this.vv.repaint();
	}

	/**
	 * Stop requirements traceability
	 * 
	 * @return List with Edge Types
	 */
	public void stopTracingNodes() {
		this.vv.getRenderContext().setVertexFillPaintTransformer(vertexColor);
		this.vv.repaint();
	}

	/**
	 * Try to collapse the currently selected Node
	 */
	public void collapseNode ()
	{
		Collection<IMyNode> picked =new HashSet<IMyNode>(vv.getPickedVertexState().getPicked());
		if(picked.size() == 1) {
			IMyNode root = picked.iterator().next();

			if (root instanceof MyNode)
			{
				MyNode node = (MyNode) root;
				if (collapser.isCollapsable(node))
				{
					collapser.collapseGraph( node);

					this.updateGraph();
				}
			}
		}
	}

	/**
	 * Try to expand the currently selected Node
	 * @param nodeDetails define how many Node details should be visible
	 */
	public void expandNode(boolean nodeDetails)
	{
		Collection<IMyNode> picked =new HashSet<IMyNode>(vv.getPickedVertexState().getPicked());
		if(picked.size() == 1) {
			IMyNode root = picked.iterator().next();

			if (root instanceof MyNode)
			{
				MyNode node = (MyNode) root;
				if (collapser.isExpandable(node))
				{
					collapser.expandNode( node, nodeDetails);

					this.updateGraph();
				}
			}
		}
	}

	/**
	 * Is the currently selected Node expandable
	 * @return true if the Node is expandable, false otherwise
	 */
	public boolean isExpandable ()
	{
		Collection<IMyNode> picked =new HashSet<IMyNode>(vv.getPickedVertexState().getPicked());
		if(picked.size() == 1) {
			IMyNode root = picked.iterator().next();

			if (root instanceof MyNode)
			{
				MyNode node = (MyNode) root;
				return collapser.isExpandable(node);
			}
		}
		return false;
	}

	/**
	 * Can the currently selected Node be collapsed
	 * @return true if the Node can be collapsed, false otherwise
	 */
	public boolean isCollapsable()
	{
		Collection<IMyNode> picked =new HashSet<IMyNode>(vv.getPickedVertexState().getPicked());
		if(picked.size() == 1) {
			IMyNode root = picked.iterator().next();

			if (root instanceof MyNode)
			{
				MyNode node = (MyNode) root;
				return collapser.isCollapsable(node);
			}
		}
		return false;
	}

	/**
	 * Update the Graph
	 * (Should be used after every operation which alters the appearence of the graph in any way) 
	 */
	public void updateGraph()
	{
		g = gb.updateGraph(detailedNodes);

		vv.getPickedVertexState().clear();
		vv.repaint();
	}

	/**
	 * Update the configuration file with a new Colormapping
	 * @param nodeColorMapping a mapping from MyNodeType to the assigned Color
	 */
	public void setNodeColorMapping(HashMap<MyNodeType, Color> nodeColorMapping) {
		this.nodeColorMapping.putAll(nodeColorMapping);
		this.configWriterReader.setColors(nodeColorMapping);

		updateGraph();
	}

	/**
	 * Get for every MyNodeType the assigned color
	 * @return a mapping from MyNodeType to the assigned Color
	 */
	public HashMap<MyNodeType, Color> getNodeColorMapping ()
	{
		return this.configWriterReader.readColors();
	}
	
	public HashMap<MyNodeType, Icon> getNodeIconMapping()
	{
		//return this.nodeIconConfigWriterReader.readIcons();
		return this.nodeIconMapping;
	}

	/**
	 * Create a new GraphView in the configuration file
	 * @param newView GraphViewContainer which contains all the information about the new Graphview
	 */
	public void createNewGraphView (GraphViewContainer newView)
	{
		this.configWriterReader.setGraphView(newView);
		this.nodeIconConfigWriterReader.setGraphView(newView);
	}

	/**
	 * Get all GraphViews from the configuration file
	 * @return a mapping viewname to the GraphViewContainer with all the view information. Might be empty
	 */
	public HashMap<String, GraphViewContainer> getAllGraphViews()
	{
		return this.configWriterReader.readViews();
	}
	

	/**
	 * Delete a GraphView in the configuration file
	 * @param deleteView the GraphViewContainer which describes the GraphView which should be deleted
	 */
	public void deleteGraphView (GraphViewContainer deleteView)
	{
		this.configWriterReader.deleteView(deleteView.getViewName());
	}

	/**
	 * Change the Graphs Layout ( currently only predefined Jung2 layouts)
	 * @param newLayout the name of the Layout which should be applied
	 */
	public void changeLayout (String newLayout)
	{
		if (newLayout.equals(KKLayout))
		{
			this.layout = new KKLayout<IMyNode, MyEdge>(g);
		}
		if (newLayout.equals(FRLayout))
		{
			this.layout = new FRLayout<IMyNode, MyEdge>(g);
		}
		if (newLayout.equals(SpringLayout))
		{
			this.layout = new SpringLayout<IMyNode, MyEdge>(g);
		}
		if (newLayout.equals(ISOMLayout))
		{
			this.layout = new ISOMLayout<IMyNode, MyEdge>(g);
		}
		if (newLayout.equals(CircleLayout))
		{
			this.layout = new CircleLayout<IMyNode, MyEdge>(g);
		}

		if (newLayout.equals(TestLayout))
		{
			this.layout = new gui.TestLayout<IMyNode, MyEdge>(g);
		}

		layout.setInitializer(vv.getGraphLayout());
		layout.setSize(vv.getSize());

		LayoutTransition<IMyNode,MyEdge> lt =new LayoutTransition<IMyNode,MyEdge>(vv, vv.getGraphLayout(), layout);
		Animator animator = new Animator(lt);
		animator.start();
		vv.getRenderContext().getMultiLayerTransformer().setToIdentity();
		vv.repaint();
	}

	public void clearPickSupport()
	{
		vv.getPickedVertexState().clear();
		vv.getPickedEdgeState().clear();
	}

	public void setNodeIconMapping(HashMap<MyNodeType, Icon> iconMapper) {
		this.nodeIconMapping = iconMapper;
		this.nodeIconConfigWriterReader.setIcons(nodeIconMapping);

		updateGraph();
		
	}
}