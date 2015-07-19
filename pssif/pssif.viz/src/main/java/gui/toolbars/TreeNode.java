package gui.toolbars;

import java.util.LinkedList;

import de.tum.pssif.core.metamodel.external.MetamodelNode;

public class TreeNode {
	private LinkedList<TreeNode> leaves;
	private MetamodelNode content;
	
	public TreeNode(MetamodelNode node)
	{
		this.content = node;
		this.leaves = new LinkedList<TreeNode>();
	}
	
	public int getSize()
	{
		int c = 1;
		for (TreeNode tn : this.leaves)
			c += tn.getSize();
		return c;
	}
	
	public LinkedList<TreeNode> getLeaves() {
		return leaves;
	}
	public void setLeaves(LinkedList<TreeNode> leaves) {
		this.leaves = leaves;
	}
	public MetamodelNode getContent() {
		return content;
	}
	public void setContent(MetamodelNode content) {
		this.content = content;
	}
	
	
}
