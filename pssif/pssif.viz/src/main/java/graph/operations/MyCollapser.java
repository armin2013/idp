package graph.operations;

import graph.model.IMyNode;
import graph.model.MyEdge;
import graph.model.MyNode;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import model.ModelBuilder;
import de.tum.pssif.core.metamodel.external.PSSIFCanonicMetamodelCreator;

/**
 * Allows to collapse or expand Nodes
 * @author Luc
 *
 */
public class MyCollapser {
	

	private MyNode recStartNode;
	private LinkedList<MyEdge> newInEdges;
	private LinkedList<MyEdge> newOutEdges;
	private LinkedList<MyNode> touchedNodes;
	
	private HashMap<MyNode,CollapseContainer> history;
	private CollapseContainer container;
	
	public MyCollapser()
	{
		this.history = new HashMap<MyNode, CollapseContainer>();
	}
	
	/**
	 * collapses the Graph at the given Node
	 * @param startNode
	 */
	public void collapseGraph( MyNode startNode) 
	{
		container = new CollapseContainer();
		
		recStartNode=startNode;
		newInEdges = new LinkedList<MyEdge>();
		newOutEdges = new LinkedList<MyEdge>();
		touchedNodes = new LinkedList<MyNode>();
		
		// remove all the collapsed nodes from the graph
		recCollapseGraph(startNode,true,new LinkedList<MyNode>());
		
		// add new Edges which where previously connected to on of the Nodes which are are now collapsed
		// the collapsed Node was the source of the Edge
		for (MyEdge ic : newInEdges)
		{
			if (!touchedNodes.contains(ic.getSourceNode()))
			{
				//g.addEdge(ic.getEdge(), ic.getEdgeSource(), ic.getEdgeDestintation(),EdgeType.DIRECTED);
				ModelBuilder.addCollapserEdge(ic);
				container.addNewEdges(ic);
			}
		}
		
		// add new Edges which where previously connected to on of the Nodes which are are now collapsed
		// the collapsed Node was the destination of the Edge
		for (MyEdge ic : newOutEdges)
		{
			if (!touchedNodes.contains(ic.getDestinationNode()))
			{
				//g.addEdge(ic.getEdge(), ic.getEdgeSource(), ic.getEdgeDestintation(),EdgeType.DIRECTED);
				ModelBuilder.addCollapserEdge(ic);
				container.addNewEdges(ic);
			}
			
		}
		
		// store all the information about the collapse operation
		history.put(startNode, container);
		
	}
	
	/**
	 * Remove all the Nodes and Edges from the graph which should not be displayed anymore after the collapse operation
	 * @param startNode where to start the collapse operation
	 * @param start should be set true, if called from outside
	 * @param work Nodes which have to be processed
	 */
	private void recCollapseGraph ( MyNode startNode, boolean start, LinkedList<MyNode> work)
	{
		
		if (start)
		{
			LinkedList<MyEdge> out = findOutgoingEdges(startNode);
			
			LinkedList<MyNode> tmp = new LinkedList<MyNode>();
			LinkedList<MyEdge> del = new LinkedList<MyEdge>();
			
			for (MyEdge e : out)
			{
				/*EdgeType parent = e.getEdgeType().getParentType();
				
				boolean test = false;
				// test if one of the outgoing edges is an containment
				if (parent!=null && parent.getName()!="Edge")
					test = parent.getName().equals(MyEdgeTypes.CONTAINMENT);
				else
					test = e.getEdgeType().getName().equals(MyEdgeTypes.CONTAINMENT);
				
				// if it was a containment edge, the connected nodes have to get further treatment
				if (test)
					test = parent.getName().equals(PSSIFCanonicMetamodelCreator.E_RELATIONSHIP_INCLUSION);
				else
					test = e.getEdgeType().getName().equals(PSSIFCanonicMetamodelCreator.E_RELATIONSHIP_INCLUSION);
				*/
				// if it was a containment edge, the connected nodes have to get further treatment
				if (testInclusionEdge(e))
				{
					MyNode next = (MyNode)e.getDestinationNode();
					// add them to the list of Edges which have to be deleted
					del.add(e);
					tmp.add(next);
				}
			}
			
			// add all the containment Nodes to the future work 
			work.addAll(tmp);
			touchedNodes.addAll(tmp);
			
			// remove the Edges from the graph
			for (int i = 0;i<del.size();i++)
			{
				MyEdge e = del.get(i);
				
				container.addOldEdge(e);
				
				e.setVisible(false);
			}
		}
		
		// if there are still some Nodes which must be checked
		if (work.size()>0)
		{
			MyNode workNode = work.getFirst();
			
			List<MyEdge> out = findOutgoingEdges(workNode);
			List<MyEdge> in = findIncomingEdges(workNode);
			
			if (in!=null)
			{
				// remove all the incoming edges and connect them to Nodes which will be available after the collapse operation
				for (int i = 0; i<in.size();i++)
				{
					//System.out.println("loop in "+i);
					MyEdge e= in.get(i);
					
					MyNode source = (MyNode)e.getSourceNode();
					
					container.addOldEdge(e);
					
					e.setVisible(false);
					newInEdges.add(new MyEdge(e.getEdge(), e.getEdgeType(), source, recStartNode));
					
				}
			}
			if (out!=null)
			{	
				// remove all the outgoing edges and connect them to Nodes which will be available after the collapse operation
				for (int i = 0; i<out.size();i++)
				{
				//	System.out.println("loop out "+out.size());
					MyEdge e= out.get(i);
					MyNode dest = (MyNode)e.getDestinationNode();//g.getDest(e);
			//		System.out.println("Remove Edge  "+e.getEdgeType().getName());
					container.addOldEdge(e);
					
					e.setVisible(false);
			//		System.out.println("Add to work  "+dest.getName());
					newOutEdges.add(new MyEdge(e.getEdge(),e.getEdgeType(),recStartNode, e.getDestinationNode()));
					if (testInclusionEdge(e) && !work.contains(dest) )
					{
						work.add(dest);
						touchedNodes.add(dest);
						
					}
				}
			}
			//System.out.println("workNode " + (workNode==null)+" name "+ workNode.getName());
			container.addOldNode(workNode);
			
			workNode.setVisible(false);
			work.remove(workNode);
			
			recCollapseGraph( null, false, work);
		}
	}
	
	/**
	 * Expands the graph again from a given startNode
	 * @param startNode  the node which should be expanded
	 * @param nodeDetails the mode of details display for the Node which should be applied
	 */
	public void expandNode( MyNode startNode, boolean nodeDetails)
	{
		// was there any collapse operation executed previously 
		CollapseContainer container = this.history.get(startNode);
		
		//if yes undo all the operation
		if (container!=null)
		{
			LinkedList<MyEdge> workEdges = container.getNewEdges();
			
			// remove added Edges
			for (MyEdge ic :workEdges)
			{
				ModelBuilder.removeCollapserEdge(ic);
			}
			
			// add old Nodes
			LinkedList<MyNode> nodes = container.getOldNodes();
			
			for (MyNode n : nodes)
			{
				if (n!=null)
				{
					//FIXME maybe has to be used
					//MyNode tmp = ModelBuilder.findNode(n);
					//tmp.setDetailedOutput(nodeDetails);
					//tmp.setVisible(true);
					
					n.setDetailedOutput(nodeDetails);
					n.setVisible(true);
					
				}
			}
			
			// add old Edges
			workEdges = container.getOldEdges();
			for (MyEdge ic :workEdges)
			{
				//FIXME maybe has to be used
				//MyEdge tmp = ModelBuilder.findEdge(ic);
				//tmp.setVisible(true);
				ic.setVisible(true);
			}
			
			this.history.remove(startNode);
		}
	}
	
	/**
	 * Test if a certain Node is expandable
	 * @param startNode the Node which should be tested
	 * @return true if the node is expandable, fals if not
	 */
	public boolean isExpandable (IMyNode startNode)
	{
		if (startNode instanceof MyNode)
		{
			MyNode node = (MyNode) startNode;
			CollapseContainer container = this.history.get(node);
			
			if (container==null)
				return false;
			else
				return true;
		}
		else
			return false;
	}
	
	/**
	 * Test if a Node can collapsed
	 * @param startNode the node which should be tested
	 * @return true if the node can be collapsed, false otherwise
	 */
	public boolean isCollapsable (IMyNode startNode)
	{
		if (startNode instanceof MyNode)
		{
			MyNode node = (MyNode) startNode;
			LinkedList<MyEdge> out = findOutgoingEdges(node);
			for (MyEdge e : out)
			{
				if (testInclusionEdge(e))
				{
					return true;
				}
			}
			
			return false;
		}
		else
			return false;
	}
	
	/**
	 * Was any collapse operation executed which is not yet undone(expanded)
	 * @return true if there are operations which have to be undone(expanded), false otherwise
	 */
	public boolean CollapserActive()
	{
		if (this.history.size()>0)
			return true;
		else
			return false;
	}
	/**
	 * remove all the expand operations which are not yet executed
	 */
	public void reset()
	{
		this.history.clear();
	}
	
	/**
	 * Find for a given Node all the outgoing edges
	 * @param node the node which should be searched
	 * @return a list with all the outgoing Edges
	 */
	private LinkedList<MyEdge> findOutgoingEdges (MyNode node)
	{
		LinkedList<MyEdge> res = new LinkedList<MyEdge>();
		
		for (MyEdge e :ModelBuilder.getAllEdges())
		{
			if (e.isVisible() && e.getSourceNode().equals(node))
				res.add(e);
		}
		
		return res;
	}
	
	/**
	 * Find for a given Node all the incoming edges
	 * @param node the node which should be searched
	 * @return a list with all the incoming Edges
	 */
	private LinkedList<MyEdge> findIncomingEdges (MyNode node)
	{
		LinkedList<MyEdge> res = new LinkedList<MyEdge>();
		
		for (MyEdge e :ModelBuilder.getAllEdges())
		{
			if (e.isVisible() && e.getDestinationNode().equals(node))
				res.add(e);
		}
		return res;
	}
	
	private boolean testInclusionEdge(MyEdge e)
	{
		/*EdgeType parent = e.getEdgeType().getParentType();
		
		if (parent !=null)
		{
			boolean res = false;
			res = res || e.getEdgeType().getName().equals(PSSIFCanonicMetamodelCreator.E_RELATIONSHIP_INCLUSION);
			res = res || e.getEdgeType().getName().equals(PSSIFCanonicMetamodelCreator.E_RELATIONSHIP_INCLUSION_CONTAINS);
			res = res || e.getEdgeType().getName().equals(PSSIFCanonicMetamodelCreator.E_RELATIONSHIP_INCLUSION_GENERALIZES);
			res = res || e.getEdgeType().getName().equals(PSSIFCanonicMetamodelCreator.E_RELATIONSHIP_INCLUSION_INCLUDES);
			
			//check parent 
			res = res || e.getEdgeType().getParentType().getName().equals(PSSIFCanonicMetamodelCreator.E_RELATIONSHIP_INCLUSION);
			
			return res;
		}
		else
		{
			boolean res = false;
			res = res || e.getEdgeType().getName().equals(PSSIFCanonicMetamodelCreator.E_RELATIONSHIP_INCLUSION);
			res = res || e.getEdgeType().getName().equals(PSSIFCanonicMetamodelCreator.E_RELATIONSHIP_INCLUSION_CONTAINS);
			res = res || e.getEdgeType().getName().equals(PSSIFCanonicMetamodelCreator.E_RELATIONSHIP_INCLUSION_GENERALIZES);
			res = res || e.getEdgeType().getName().equals(PSSIFCanonicMetamodelCreator.E_RELATIONSHIP_INCLUSION_INCLUDES);
			
			return res;
		}*/
		
		boolean res = false;
		res = res || e.getEdgeType().getName().equals(PSSIFCanonicMetamodelCreator.TAGS.get("E_RELATIONSHIP_INCLUSION_CONTAINS"));
		
		return res;
	}
}
