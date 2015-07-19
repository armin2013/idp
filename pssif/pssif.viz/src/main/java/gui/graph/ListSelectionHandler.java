package gui.graph;

import graph.model.MyNodeType;

import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class ListSelectionHandler implements ListSelectionListener  {
	
	NodeIconPopup nsp;
	public ListSelectionHandler(NodeIconPopup nsp){
		this.nsp = nsp;
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		@SuppressWarnings("unchecked")
		JList<MyNodeType> nodeList = (JList<MyNodeType>)e.getSource();
		MyNodeType selectedNode = nodeList.getSelectedValue();
		nsp.ii.setCurrentNode(selectedNode);
		
		if(nsp.getIconMapper().containsKey(selectedNode))
			nsp.ii.showImage(nsp.getIconMapper().get(selectedNode));
		else
			nsp.ii.wipeImage();
	}
	

}
