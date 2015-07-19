package graph.model;

import java.util.Collection;
import java.util.LinkedList;

import model.ModelBuilder;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;


public class GraphBuilder {
	
	private Graph<IMyNode, MyEdge> g;
	//private boolean detailedNodes;
	private static boolean commentsVisible = false;
	
	/**
	 * Creates a new Graph with a given mode of Display for the Nodes
	 * @param detailedNodes true == more details, false = less details
	 * @return the graph with the according Node details grade
	 */
	public Graph<IMyNode, MyEdge> createGraph(boolean detailedNodes)
	{
		if (g==null)
			g = new SparseMultigraph<IMyNode,MyEdge>();
			//g = new SetHypergraph<MyNode,MyEdge>();
		
		removeAllNodesAndEdges();
		
		buildGraphFromModel(detailedNodes);
		
		return g;
	}
	
	/**
	 * Change the Display mode of the Nodes on the given graph
	 * @param detailedNodes true == more details, false = less details
	 * @par graph the graph on which the operation should be applied
	 * @return the graph with the according Node details grade
	 */
	public Graph<IMyNode, MyEdge> changeNodeDetails(boolean detailedNodes, Graph<IMyNode, MyEdge> graph)
	{
		Collection<IMyNode> nodes = graph.getVertices();
				
		for (IMyNode n : nodes)
		{
			n.setDetailedOutput(detailedNodes);
		}

		return graph;
	}
	
	/**
	 * Remove all Nodes and Edges from the graph
	 * @return the empty graph
	 */
	public Graph<IMyNode, MyEdge> removeAllNodesAndEdges ()
	{
		return removeAllNodesAndEdges(g);
	}
	
	/**
	 * Remove all Nodes and Edges from the graph
	 * @param graph the graph on which the operation should be applied
	 * @return the empty graph
	 */
	private Graph<IMyNode, MyEdge> removeAllNodesAndEdges (Graph<IMyNode, MyEdge> graph)
	{
		LinkedList<MyEdge> edges = new LinkedList<MyEdge>(graph.getEdges());
		
		for (MyEdge e : edges)
		{
			graph.removeEdge(e);
		}
		
		LinkedList<IMyNode> nodes = new LinkedList<IMyNode>(graph.getVertices());
		
		for (IMyNode n : nodes)
		{
			graph.removeVertex(n);
		}
		
		return graph;
	}
	
	/**
	 * Updates the graph. Builds the graph again from the Model with the given mode of details display
	 * @param detailedNodes true == all details, false == less details
	 * @return the graph with the given preferences
	 */
	public Graph<IMyNode, MyEdge> updateGraph (boolean detailedNodes)
	{
		this.removeAllNodesAndEdges();
		
		return buildGraphFromModel(detailedNodes);
	}
	
	/**
	 * Build the graph from the Model with the given mode of details for the nodes
	 * @param detailedNodes true == all details, false == less details
	 * @return the graph with the given preferences
	 */
	private Graph<IMyNode, MyEdge> buildGraphFromModel (boolean detailedNodes)
	{
		if (commentsVisible)
			System.out.println("buildGraphFromModel ");
		LinkedList<MyEdge> edges = ModelBuilder.getAllEdges();
		LinkedList<MyNode> nodes = ModelBuilder.getAllNodes();
		LinkedList<MyJunctionNode> junctionNodes = ModelBuilder.getAllJunctionNodes();
		
		
		for (MyNode n : nodes)
		{
			if (n.isVisible())
			{
				n.setDetailedOutput(detailedNodes);
				g.addVertex(n);
				if (commentsVisible)
					System.out.println("Node is visible "+n.getRealName());
			}
			else
			{
				if (commentsVisible)
					System.out.println("Node not visible "+n.getRealName());
			}
		}
		
		for (MyJunctionNode n : junctionNodes)
		{
			if (n.isVisible())
			{
				n.setDetailedOutput(detailedNodes);
				g.addVertex(n);
				if (commentsVisible)
					System.out.println("JunctionNode is visible "+n.getRealName());
			}
			else
			{
				if (commentsVisible)
					System.out.println("JunctionNode not visible "+n.getRealName());
			}
		}
		
		for (MyEdge e : edges)
		{
			if (e.isVisible() && e.getDestinationNode().isVisible() && e.getSourceNode().isVisible())
			{
				if (commentsVisible)
				{
					System.out.println("Edge :"+ e.getEdgeTypeName());
					System.out.println("Source "+e.getSourceNode().getRealName());
					System.out.println("Dest "+e.getDestinationNode().getRealName());
				}
				if (e.isDirected())
				{
					//LinkedList<MyNode> tmp = new LinkedList<MyNode>();
					//tmp.add(e.getSourceNode());
					//tmp.add(e.getDestinationNode());
					g.addEdge(e, e.getSourceNode(), e.getDestinationNode(), EdgeType.DIRECTED);
				}
				else
				{
					/*LinkedList<MyNode> tmp = new LinkedList<MyNode>();
					tmp.add(e.getSourceNode());
					tmp.add(e.getDestinationNode());*/
					g.addEdge(e, e.getSourceNode(), e.getDestinationNode(), EdgeType.UNDIRECTED);
				}
				
				
			}
			
			if (commentsVisible)
			{
				System.out.println("Visibilty "+ e.isVisible());
				System.out.println("Edge-----------------");
			}
				
		}
		
		if (commentsVisible)
			System.out.println("----------------");
		
		return g;
	}
}
