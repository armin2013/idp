package graph.operations;

import java.awt.BasicStroke;
import java.awt.Stroke;
import java.util.Collection;
import java.util.LinkedList;

import model.ModelBuilder;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.picking.PickedInfo;
import graph.model.IMyNode;
import graph.model.MyEdge;
import graph.model.MyEdgeType;
import graph.model.MyJunctionNode;
import graph.model.MyNode;

/**
 * Highlights certain Nodes which are connected with a defined Edge Type
 * @author Luc
 *
 * @param <V> The Node class ( e.g. MyNode)
 * @param <E> The Edge class ( e.g. MyEdge)
 */
public class VertexStrokeHighlight<V,E> implements Transformer<V,Stroke>
	    {
	        protected boolean highlight = false;
	        protected Stroke heavy = new BasicStroke(10);
	        protected Stroke medium = new BasicStroke(5);
	        protected Stroke light = new BasicStroke(1);
	        protected PickedInfo<V> pi;
	        protected Graph<V,E> graph;
	        private LinkedList<MyEdgeType> followEdges;
	        private int depth;
	        private boolean specialSearch;
	        
	        private static boolean debug = false;
	        
	        /**
	         * Init the Highlighter
	         * @param graph the graph on which the Highlighter should be applied
	         * @param pi which Node is currently selected
	         */
	        public VertexStrokeHighlight(Graph<V,E> graph, PickedInfo<V> pi)
	        {
	        	this.graph = graph;
	            this.pi = pi;
	            this.depth = 1;
	            this.followEdges = new LinkedList<MyEdgeType>();
	            this.specialSearch=false;
	        }
	        
	        /**
	         * Which Edge Types should be followed and how depth
	         * @param highlight should the Highlighter be turned on from the beginning
	         * @param searchDepth how depth should the search go. (e.g. how many Edges should be followed)
	         * @param followEdges which Edge Type should be followed at all
	         */
	        public void setHighlight(boolean highlight, int searchDepth, LinkedList<MyEdgeType> followEdges)
	        {
	            this.highlight = highlight;
	            this.depth = searchDepth;
	            this.followEdges = followEdges;
	            
	            if (this.depth!=1 || this.followEdges.size()!=0)
	            	this.specialSearch=true;
	            else
	            	this.specialSearch=false;
	        }
	        
	        /**
	         * Do the visualization on the graph
	         */
	        public Stroke transform(V currentNode)
	        {  
	        	if (highlight)
	            {
	        	//	System.out.println("-----------------------");
	        	//	System.out.println(((MyNode) currentNode).getRealName());
	               // System.out.println("specialSearch "+specialSearch);
	            	if (specialSearch)
	                {
	                	if (pi.isPicked(currentNode))
	                	{
		                    if (debug)
		                    	System.out.println(((IMyNode) currentNode).getName() +"  Heavy");
		                    if (currentNode instanceof MyJunctionNode)
		                    	return medium;
		                    else
		                    	return heavy;
	                	}
		                else
		                {	                
		                	if (depth>1)
            				{
		                		if (searchDept(currentNode, depth))
            					{
            						 if (debug)
            						 {
	            						System.out.println(((IMyNode) currentNode).getName());
	            						System.out.println("                              depth Predecessor  Medium ");
	            						System.out.println("----------------------------");
            						 }
                					return medium;
                				}
                				else
                				{
                					 if (debug)
                					 {
		                					System.out.println(((IMyNode) currentNode).getName());
		                					System.out.println("                             not depth Predecessor  Light ");
		                					System.out.println("----------------------------");
                					 }
                					return light;
                				}	
            				}
            				else
            				{
            					Collection<V>  predecessors = graph.getNeighbors(currentNode);
    		                	if (predecessors!=null)
    		                	{
    		                		for (V v: predecessors)
    		                		{
    		                			if (pi.isPicked(v))
    		                			{
    		                				if(v instanceof MyNode && searchOutEdges(v, currentNode,false))
    		                				{
    		                					//System.out.println("medium");
    		                					 if (debug)
    		                					 {
    			                					System.out.println(((IMyNode) currentNode).getName());
    			                					System.out.println("                             direct Predecessor  Medium ");
    		                					 }
    		                					return medium;
    		                				}
    		                			}
    		                			
    		                		}
    		                		return light;
    		                	}
    		                	else
    		                		return light;
            				}
		                		
	                	}        	
	                }
	                else
	                {
		            	if (pi.isPicked(currentNode))
		                    {
			                    if (currentNode instanceof MyJunctionNode)
			                    	return medium;
			                    else
			                    	return heavy;
		                    }
		                else
		                {	                
		                	if (searchDept(currentNode, depth))
        					{
        						 if (debug)
        						 {
            						System.out.println(((IMyNode) currentNode).getName());
            						System.out.println("                              depth Predecessor  Medium ");
            						//System.out.println("medium");
            						System.out.println("----------------------------");
        						 }
            					return medium;
            				}
            				else
            				{
            					 if (debug)
            					 {
	                					System.out.println(((IMyNode) currentNode).getName());
	                					System.out.println("                             not depth Predecessor  Light ");
	                					//System.out.println("light");
	                					System.out.println("----------------------------");
            					 }
            					return light;
            				}	
		                }
		            }
	            }
	            else
	                return light; 
	        }
	        
	        /**
	         * check if between the source and the destination exists an Edge which has one of the selected EdgeTypes  
	         * @param source The start Node
	         * @param dest The destination Node
	         * @param depthsearch if depth search has to be true, else always false
	         * @return true if an Edge exists, which fulfills the Type condition, otherwise false
	         */
	        private boolean searchOutEdges(V source, V dest, boolean depthsearch)
	        {
	        	
	        	LinkedList<MyEdge> eout = findOutgoingEdges(source);
				//Collection<MyEdge> eout = ( Collection<MyEdge>) graph.getOutEdges(source);
	        	 
	        	 for (MyEdge e : eout)
	        	 {
	        		/* System.out.println(e.getConnectionType()+" dest "+e.getDestinationNode().getRealName()+
 							" source "+e.getSourceNode().getRealName());*/
	        		if (depthsearch)
	        		{
		        		if (e.isDirected())
		        		{
		        			if (this.followEdges.isEmpty() && e.getDestinationNode().equals(((IMyNode) dest)))
		        					return true;
		        			if (!this.followEdges.isEmpty() && this.followEdges.contains(e.getEdgeType()) &&  e.getDestinationNode().equals(((IMyNode) dest)))
		        				 return true;
		        		}
		        		else
		        		{
		        			if (this.followEdges.isEmpty() && 
		        					(e.getDestinationNode().equals(((IMyNode) dest)) || e.getSourceNode().equals(((IMyNode) dest)) ) )
		        					return true;
		        			
			        		if (!this.followEdges.isEmpty() && this.followEdges.contains(e.getEdgeType()) &&
			        				(e.getDestinationNode().equals(((IMyNode) dest)) || e.getSourceNode().equals(((IMyNode) dest)) ) )
			        				 return true;
		        		}
	        		}
	        		else
	        		{
	        			if (e.isDirected())
	        			{
		        			if (this.followEdges.contains(e.getEdgeType()) && e.getDestinationNode().equals(((IMyNode) dest)))
			        				 return true;
	        			}
	        			else
	        			{
		        			if (this.followEdges.contains(e.getEdgeType()) && 
		        					(e.getDestinationNode().equals(((IMyNode) dest)) || e.getSourceNode().equals(((IMyNode) dest)) ))
		        				 return true;
	        			}
	        		}
	        	 }
	        	 return false;
	        }
	        
	        /**
	         * Test if one of the predecessor Node is already highlighted. 
	         * If the current Node is in the a reachable depth range, the current Node must also be highlighted
	         * @param current The Node which has to be test.
	         * @param level how depth should be searched
	         * @param res due to recursion. Should always be initialized with false 
	         * @return  true if the tested Node should be highlighted, otherwise false
	         */
	        private boolean searchDept(V current, int level )
	        {
	        	if (level>0)
	        	{
	        		Collection<V>  predecessors = graph.getNeighbors(current);
	        		LinkedList<V> next = new LinkedList<V>();
	        		
	        		for (V v: predecessors)
	        		{
	        			if (searchOutEdges(v, current, true))
	        			{
		        			if (pi.isPicked(v))
		        			{
		        				if (v instanceof MyNode)
		        					return true;
		        				else
		        					return false;
		        			}
		        			else
		        				next.add(v);

	        			}
	        		}
	        		
	        		boolean res=false;
	        		int levelbefore = level;
	        		level--;
	        		for (V v :next)
	        		{
	        			
	        			if (v instanceof MyNode)
	        				res = res || searchDept(v, level); 
	        			if (v instanceof MyJunctionNode)
	        				res = res || searchDept(v, levelbefore); 
	        		}
	        		
	        		return res;
	        	}

	        		return false;
	        }
	        

	        /**
	         * Get all the Edge Types which should be followed
	         * @return a list with Edge Types
	         */
	        public LinkedList<MyEdgeType> getFollowEdges()
	        {
	        	return this.followEdges;
	        }
	        
	        /**
	         * How depth is searched
	         * @return the depth
	         */
			public int getSearchDepth() {
				return depth;
			}
			
			/**
			 * Find for a given Node all the outgoing edges
			 * @param node the node which should be searched
			 * @return a list with all the outgoing Edges
			 */
			private LinkedList<MyEdge> findOutgoingEdges (V node)
			{
				LinkedList<MyEdge> res = new LinkedList<MyEdge>();
				
				for (MyEdge e :ModelBuilder.getAllEdges())
				{
					if (e.isDirected())
					{
						if (e.isVisible() && e.getSourceNode().equals((IMyNode)node))
							res.add(e);
					}
					else
					{
						if (e.isVisible() && ( e.getSourceNode().equals((IMyNode)node) || e.getDestinationNode().equals((IMyNode)node)))
							res.add(e);
					}
				}
				
				return res;
			}        
}
