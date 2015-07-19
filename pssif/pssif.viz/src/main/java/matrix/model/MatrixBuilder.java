package matrix.model;

import graph.model.IMyNode;
import graph.model.MyEdge;
import graph.model.MyEdgeType;
import graph.model.MyJunctionNode;
import graph.model.MyNode;
import graph.model.MyNodeType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import model.ModelBuilder;

/**
 * Build a Matrix out of the available Model data
 * @author Luc
 *
 */
public class MatrixBuilder {
	
	private LinkedList<MyNodeType> nodeTypes;
	private LinkedList<MyEdgeType>  edgesTypes;
	
	/**
	 * initialize a MatrixBuilder instance
	 * @param nodeTypes which Nodetypes should be contained in the matrix
	 * @param edgesTypes which Edgetypes should be contained in the matrix
	 */
	public MatrixBuilder(LinkedList<MyNodeType> nodeTypes, LinkedList<MyEdgeType>  edgesTypes)
	{
		this.nodeTypes= nodeTypes;
		this.edgesTypes = edgesTypes;
	}
	
	public MatrixBuilder()
	{
		this.nodeTypes= new LinkedList<MyNodeType>();
		this.edgesTypes = new LinkedList<MyEdgeType>();
	}
	
	/**
	 * Find all the Nodes which have as type one of the previously defined NodeTypes
	 * @return a List with all Nodes instances
	 */
	public LinkedList<MyNode> findRelevantNodes()
	{
		LinkedList<MyNode> res = new LinkedList<MyNode>();
		
		List<MyNode> search = ModelBuilder.getAllNodes();
		
		if (search !=null)
		{
			for (MyNode node : search)
			{
				//System.out.println("Node loop "+node.getName()+" Type "+node.getNodeType().getName());
				if (nodeTypes.contains(node.getNodeType()))
				{
					res.add(node);
				}
			}
		}
		
		//System.out.println("Found relevant nodes "+res.size());
		return res;
	}
	
	/**
	 * Find all the Edges which have as type one of the previously defined EdgeTypes
	 * @return a List with all Edge instances
	 */
	public LinkedList<MyEdge> findRelevantEdges()
	{
		LinkedList<MyEdge> res = new LinkedList<MyEdge>();
		
		List<MyEdge> search = ModelBuilder.getAllEdges();
		
		if (search !=null)
		{
			
			for (MyEdge edge : search)
			{
				//System.out.println("Edge loop Type "+edge.getEdgeType().getName());
				if (edgesTypes.contains(edge.getEdgeType()))
					res.add(edge);
			}
		}
		
		//System.out.println("Found relevant edges "+res.size());
		return res;
	}
	
	/**
	 * Create the content of the matrix. Only the Edge connections between 2 Nodes 
	 * @param nodes which have to be considered
	 * @param edges which have to be considered
	 * @return an 2 dimensional array with the connection information
	 */
	public String[][] getEdgeConnections(LinkedList<MyNode> nodes, LinkedList<MyEdge> edges)
	{
		String[][] res = new String[nodes.size()][nodes.size()];
		
		// create HashMap to find connections faster
		
		HashMap<MyNode,LinkedList<MyEdge>> mapping = new HashMap<MyNode,LinkedList<MyEdge>>();
		
		for (MyEdge e :edges)
		{
			IMyNode node = e.getSourceNode();
			if (node instanceof MyNode)
			{
				MyNode source = (MyNode) node;
				IMyNode dest = e.getDestinationNode();
				
				LinkedList<MyEdge> tmp = mapping.get(source);
				
				// check if there is already a Connection
				if (tmp==null)
				{ 
					// no entry yet
					tmp = new LinkedList<MyEdge>();
				}
				
				if (dest instanceof MyJunctionNode)
				{	
					HashSet<MyEdge> secondEdge = findJunctionSourceNode(ModelBuilder.getAllEdges(), (MyJunctionNode) dest);
					
					LinkedList<MyEdge> tmpEdges = new LinkedList<MyEdge>();
					for (MyEdge se :secondEdge)
					{
						tmpEdges.add( new MyEdge(null, se.getEdgeType(), source, se.getDestinationNode())) ;
					}
					
					// add the temporary edges 
					tmp.addAll(tmpEdges);
					mapping.put(source, tmp);
					
				}
				else
				{
					tmp.add(e);
					mapping.put(source, tmp);
					
				}
			}
		}
		
		
		// content
		for (int i =0; i<res.length; i++)
		{
			for (int j=0; j<res[i].length;j++)
			{
				MyNode nodeI = nodes.get(i);
				MyNode nodeJ = nodes.get(j);
				
				//check Mapping
				LinkedList<MyEdge> connections = mapping.get(nodeI);
				if (connections==null)
				{
					// no connections
					res[i][j] ="";
				}
				else
				{
					// there are connections
					//check if there is a connection from NodeI to NodeJ
					LinkedList<MyEdge> edge = findDestinationNode(connections, nodeJ);
					if (edge.size() != 0)
					{
						String s = "";
						
						boolean first = true;
						for (MyEdge e : edge)
						{

							if (first)
							{
								s = s + e.getEdgeType().toString();
								first = false;
							}
							else
								s = s + " || "+ e.getEdgeType().toString();

							/*if (e.getAttributes().size() != 0) 
							{
								for (String a : e.getAttributes()) 
								{
									s = s + " " + a + " ";
								}
							}*/
						}
						res[i][j] = s;
					}
					else
					{
						// there are connections, but no connection between NodeI to NodeJ
						res[i][j] ="";
					}
				}
			}
		}
		
		return res;
	}


	public LinkedList<MyNodeType> getRelevantNodeTypes() {
		return nodeTypes;
	}

	public void setRelevantNodeTypes(LinkedList<MyNodeType> nodeTypes) {
		this.nodeTypes = nodeTypes;
	}

	public LinkedList<MyEdgeType> getRelevantEdgesTypes() {
		return edgesTypes;
	}

	public void setRelevantEdgesTypes(LinkedList<MyEdgeType> edgesTypes) {
		this.edgesTypes = edgesTypes;
	}
	
	/**
	 * find the Edges where a specific Node is the destination 
	 * @param connections a set of Edges which will be tested
	 * @param node the destination Node
	 * @return a list with all the Edges which have as destination the parameter node
	 */
	private LinkedList<MyEdge> findDestinationNode(LinkedList<MyEdge> connections, MyNode node)
	{
		LinkedList<MyEdge> res = new LinkedList<MyEdge>();
		for (MyEdge e : connections) 
		{
			if (e.getDestinationNode().equals(node)) 
			{
				res.add(e);
			}
		}
		return res;
	}
	
	/**
	 * find the Edges where a specific MyJunctionNode is the source 
	 * @param connections a set of Edges which will be tested
	 * @param node the source MyJunctionNode
	 * @return a Set with all the Edges which have as source the parameter MyJunctionNode
	 */
	private HashSet<MyEdge> findJunctionSourceNode(LinkedList<MyEdge> connections, MyJunctionNode node)
	{
		HashSet<MyEdge> res = new HashSet<MyEdge>();
		for (MyEdge e : connections) 
		{
			if (e.getSourceNode().equals(node)) 
			{
				if (e.getDestinationNode() instanceof MyJunctionNode)
				{
					res.addAll(findJunctionSourceNode(ModelBuilder.getAllEdges(),(MyJunctionNode) e.getDestinationNode()));
				}
				else
				{
					res.add(e);
				}
			}
		}
		return res;
	}
	
	
}
