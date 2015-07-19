package gui.checkboxtree;



import java.util.Enumeration;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Provides some Tree related special Checkbox. (if parent is selected, select also all the children)
 * @author Luc
 *
 */
@SuppressWarnings("serial")
public class CheckNode extends DefaultMutableTreeNode {

	  public final static int SINGLE_SELECTION = 0;

	  public final static int DIG_IN_SELECTION = 4;

	  protected int selectionMode;

	  public boolean isSelected;

	  public CheckNode() {
	    this(null);
	  }

	  public CheckNode(Object userObject) {
	    this(userObject, true, false);
	  }

	  public CheckNode(Object userObject, boolean allowsChildren, boolean isSelected) 
	  {
	    super(userObject, allowsChildren);
	    this.isSelected = isSelected;
	    setSelectionMode(DIG_IN_SELECTION);
	  }

	  public void setSelectionMode(int mode) {
	    selectionMode = mode;
	  }

	  public int getSelectionMode() {
	    return selectionMode;
	  }

	  public void setSelected(boolean isSelected) {
	    this.isSelected = isSelected;

	    if ((selectionMode == DIG_IN_SELECTION) && (children != null)) {
	      @SuppressWarnings("rawtypes")
		Enumeration e = children.elements();
	      while (e.hasMoreElements()) {
	        CheckNode node = (CheckNode) e.nextElement();
	        node.setSelected(isSelected);
	      }
	    }
	  }
	  
	  public boolean isSelected() {
	    return isSelected;
	  }
	}
