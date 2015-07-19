package reqtool.graph.operations;

import edu.uci.ics.jung.graph.Graph;
import graph.model.IMyNode;
import graph.model.MyEdge;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;
import java.util.LinkedList;

import model.ModelBuilder;

import org.apache.commons.collections15.Transformer;

import reqtool.controller.RequirementTracer;

/**
 * Highlights certain Nodes which are connected with a defined Edge Type
 * @author Luc
 *
 * @param <V> The Node class ( e.g. MyNode)
 * @param <E> The Edge class ( e.g. MyEdge)
 */
public class ReqTraceVertexStrokeHighlight<V,N> implements Transformer<V,Paint>  {
	protected Stroke heavy = new BasicStroke(10);
	protected Stroke medium = new BasicStroke(5);
	protected Stroke light = new BasicStroke(1);
	private boolean trace;
	Transformer<IMyNode, Paint> initTransformer;
	
	/**
	 * Init the Highlighter
	 * @param graph the graph on which the Highlighter should be applied
	 * @param pi which Node is currently selected
	 */
	public ReqTraceVertexStrokeHighlight(Graph<V,N> graph, Transformer<IMyNode, Paint> initTransformer)	{
	    this.trace=false;
	}
	
	/**
	 * Which Edge Types should be followed and how depth
	 * @param highlight should the Highlighter be turned on from the beginning
	 * @param searchDepth how depth should the search go. (e.g. how many Edges should be followed)
	 * @param followNodes which Edge Type should be followed at all
	 */
	public void setHighlight( boolean trace) {
	    	this.trace=trace;
	}
	
	/**
	 * Do the visualization on the graph
	 */
	public Paint transform(V currentNode) {
	    
	    	if (trace) {
	    		if (RequirementTracer.isTracedNode((IMyNode)currentNode)) {
					return Color.GREEN;
				} else if (RequirementTracer.isOnTraceList((IMyNode)currentNode)) {
					return Color.CYAN;
				} 
				return Color.LIGHT_GRAY;
	    	}
	      
	        return initTransformer.transform((IMyNode) currentNode); 
	}
	
	/**
	 * Find for a given Node all the outgoing edges
	 * @param node the node which should be searched
	 * @return a list with all the outgoing Edges
	 */
	private LinkedList<MyEdge> findOutgoingEdges (V node) {
		LinkedList<MyEdge> res = new LinkedList<MyEdge>();
		
		for (MyEdge e :ModelBuilder.getAllEdges())
		{
			if (e.isVisible() && e.getSourceNode().equals((IMyNode)node))
				res.add(e);
		}
		
		return res;
	}      
	
}
