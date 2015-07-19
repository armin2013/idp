package gui.toolbars;

import graph.operations.MasterFilter;

import java.util.Collection;

import javax.swing.JScrollPane;
import javax.swing.JTree;

import de.tum.pssif.core.metamodel.external.MetamodelNode;
import de.tum.pssif.core.metamodel.external.PSSIFCanonicMetamodelCreator;

public class NodeHierarchyContainer {
	NodeHierarchyTree nodeTree;
	public NodeHierarchyContainer()
	{
		this.nodeTree = new NodeHierarchyTree();
	}
	
	public void printSelecteds()
	{
		nodeTree.printSelecteds();
	}
	
	public JScrollPane createNodeHierarchyTree(MasterFilter mfilter)
	{
		PSSIFCanonicMetamodelCreator.loadXMLData();
		JTree mytree = nodeTree.createTree(
				this.sortNodeHierarchy(PSSIFCanonicMetamodelCreator.nodes.values()), mfilter);
		JScrollPane scrollpane = new JScrollPane();
	    scrollpane.getViewport().add(mytree);
	    return scrollpane;
	}
	

	private TreeNode sortNodeHierarchy (Collection<MetamodelNode> nodeTypes)
	{
		TreeNode mytree = new TreeNode(null);
		for (MetamodelNode n : nodeTypes)
		{
			if (existNode(n, mytree) == null)
			{
				TreeNode tnode = new TreeNode(n);
				mytree = addNode(tnode, mytree);	
			}
		}
		
		return mytree;
	}
	
	private TreeNode addNode(TreeNode leaf, TreeNode tree)
	{
		if (leaf.getContent() == null)
			return tree;
		if (leaf.getContent().getParent() == null)
		{
			tree.getLeaves().add(leaf);
			return tree;
		}
		TreeNode temp = existNode(leaf.getContent().getParent(), tree);
		if (temp != null)
		{
			temp.getLeaves().add(leaf);
			return tree;
		}
		else
		{
			temp = new TreeNode(leaf.getContent().getParent());
			temp.getLeaves().add(leaf);
			return addNode(temp, tree);
		}
	}
	
	private TreeNode existNode(MetamodelNode node, TreeNode tree)
	{
		if (tree == null)
			return null;
		if (tree.getContent() != null)
			if (tree.getContent().getName().equals(node.getName()))
				return tree;
		
		for (TreeNode tnode : tree.getLeaves())
		{
			TreeNode temp =  existNode(node, tnode);
			if (temp != null)
				return temp;
		}
		
		return null;
	}
	
	
}




