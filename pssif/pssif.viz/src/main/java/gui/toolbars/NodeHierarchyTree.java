package gui.toolbars;

import graph.model.MyNodeType;
import graph.operations.MasterFilter;
import gui.checkboxtree.CheckBoxRenderer;
import gui.checkboxtree.CheckNode;


import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import model.ModelBuilder;

/**
 * A Tree to selected Edge Types. Provides already certain type of functionality
 * @author Khadem, Basirati
 *
 */
public class NodeHierarchyTree{
	
	private LinkedList<String> selectedValues;
	
	/**
	 * create a new Instance
	 */
	public NodeHierarchyTree(){
		selectedValues = new LinkedList<String>();
	}
	
	/**
	 * Create a new JTree with checkboxes
	 * @param metanodes a Map(Name of the NodeType, NodeType Object) which should be displayed in the Tree
	 * @param selectedNodes which edges where already previously selected. So the should now also be selected
	 * @return a new JTree with the appropriated preferences
	 */
	public JTree createTree(TreeNode root, MasterFilter mfilter)
	{
		// the root
		CheckNode node = new CheckNode("Node Types");
		
		// add all the others
		node = addNodesToTree(root, node);
		node.setSelected(true);
	    JTree tree = new JTree(node);
	    tree.setCellRenderer(new CheckBoxRenderer());
	    
	    evalChildrenSelected(tree, (CheckNode )tree.getModel().getRoot());
	  
	    tree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
	    tree.putClientProperty("JTree.lineStyle", "Angled");
	    tree.addMouseListener(new NodeSelectionListener(tree, mfilter));
	    
	    return tree;
	}
	
	private CheckNode addNodesToTree(TreeNode tn, CheckNode node) {
		
		for (TreeNode t : tn.getLeaves())
		{
			CheckNode cn = new CheckNode(t.getContent().getName()); 
			node.add(addNodesToTree(t, cn));
		}
		return node;
	}

	
	public void printSelecteds()
	{
		int i = 1;
		System.out.println("--------------------------------------------------");
		for (String mn : selectedValues)
		{
			System.out.println(i++ + ". " + mn);
		}
		System.out.println("--------------------------------------------------");
	}
	
	
	/**
	 * Recursive function which checks, if all the children of the current CheckNode are selected
	 * @param tree the tree where to execute the evaluation
	 * @param current the current place in the tree
	 * @return true if all subNodes are selected, false otherwise
	 */
	private boolean evalChildrenSelected (JTree tree, CheckNode current)
	{
		int childcount = current.getChildCount();
		// Check if current is a Leaf or not
		if (childcount>0)
		{
			boolean res = true;
			
			// get all the children and execute the same test on them again
			for (int i=0; i<childcount;i++)
			{
				CheckNode child = (CheckNode) tree.getModel().getChild(current, i);
				boolean fu = evalChildrenSelected(tree, child);
				res = res && fu;
			}
			current.isSelected = res;
			
			return res;
			
		}
		// Node wasa Leaf so return his state
		else
			return current.isSelected;
	}
	
	/**
	 * Provides some basic functionalities in the Tree Gui
	 * @author Luc
	 *
	 */
	
	class NodeSelectionListener extends MouseAdapter
	{
		JTree tree;
		private MasterFilter masterFilter;
	
		final String Filter_Name = "Temp_H";
    
		NodeSelectionListener(JTree tree, MasterFilter mfilter)
		{
			this.tree = tree;
			this.masterFilter = mfilter;
		}
    
		public void mouseClicked(MouseEvent e)
		{
			int x = e.getX();
			int y = e.getY();
			int row = tree.getRowForLocation(x, y);
			TreePath  path = tree.getPathForRow(row);
			if (path != null)
			{
				CheckNode node = (CheckNode) path.getLastPathComponent();
				boolean isSelected = ! (node.isSelected());
				node.setSelected(isSelected);
				if (node.getSelectionMode() == CheckNode.DIG_IN_SELECTION)
				{
					if (isSelected) 
						tree.expandPath(path);
					else 
						tree.collapsePath(path);
          
				}
        
				CheckNode root = (CheckNode )tree.getModel().getRoot();
				
				((DefaultTreeModel) tree.getModel()).nodeChanged(node);
        
				tree.revalidate();
				tree.repaint();
				
				
				// which nodeType is now really selected
				selectedValues.clear();
				evalMouseSelection(root);
				applyGraphView();
				
				
			}
		}
		
		private void applyGraphView()
		{
			LinkedList<MyNodeType> selectedNodes = new LinkedList<MyNodeType>();
			for (String s : selectedValues)
				selectedNodes.add(ModelBuilder.getNodeTypes().getValue(s));
			
			masterFilter.undoNodeAndEdgeTypeFilter(Filter_Name);
			masterFilter.removeNodeAndEdgeTypeFilter(Filter_Name);
			masterFilter.addNodeAndEdgeTypeFilter(selectedNodes, ModelBuilder.getEdgeTypes().getAllEdgeTypes()
					,Filter_Name , false);
			masterFilter.applyNodeAndEdgeTypeFilter(Filter_Name);
		}
		
		/**
		 * Check if the the given Checkbox is selected or not. Evaluate this event 
		 * @param node the checkbox which changed
		 */
		private void evalMouseSelection(CheckNode node)
		{
			String name =String.valueOf(node.getUserObject());
			if(node.isSelected)
				selectedValues.add(name);
			else
				selectedValues.remove(name);
				
			if (!node.isLeaf())
			{
		    	for (int i=0; i<node.getChildCount(); i++)
		    		evalMouseSelection( (CheckNode) node.getChildAt(i));
			}    	
		}
	}
	
}