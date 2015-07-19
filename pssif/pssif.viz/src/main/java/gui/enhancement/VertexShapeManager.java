package gui.enhancement;

import graph.model.IMyNode;
import graph.model.MyNode;


import graph.model.MyNodeType;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;

public class VertexShapeManager {
	
	HashMap<MyNodeType, Shape> nodeShapeMapping;
	
	public VertexShapeManager(HashMap<MyNodeType, Shape> shapemapping )
	{
		nodeShapeMapping = shapemapping;
	}
	
	public Shape selectShape(IMyNode node)
	{	
		if (node instanceof MyNode)
		{
			MyNode tmp = (MyNode) node;
			//System.out.println(tmp.getNodeType());
			return new Rectangle2D.Double(-(tmp.getWidth()/2), -(tmp.getHeight()/2), tmp.getWidth(), tmp.getHeight());
		}
		else
		{
			return new Ellipse2D.Double(-25, -25, 150, 50);
		}
	}
}
