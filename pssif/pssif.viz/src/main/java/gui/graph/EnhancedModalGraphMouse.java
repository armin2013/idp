package gui.graph;

import java.awt.event.ItemEvent;

import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;


public class EnhancedModalGraphMouse<V,E> extends DefaultModalGraphMouse<V, E>{
	/**
     * setter for the Mode.
     */
    public void setMode(Mode mode) {
        if(this.mode != mode) {
            fireItemStateChanged(new ItemEvent(this, ItemEvent.ITEM_STATE_CHANGED,
                    this.mode, ItemEvent.DESELECTED));
            this.mode = mode;
            if(mode == Mode.TRANSFORMING) {
                setTransformingMode();
            } else if(mode == Mode.PICKING) {
                setPickingMode();
            } else if(mode == Mode.EDITING) {
            	setEditingMode();
            }
        
            if(modeBox != null) {
                modeBox.setSelectedItem(mode);
            }
            fireItemStateChanged(new ItemEvent(this, ItemEvent.ITEM_STATE_CHANGED, mode, ItemEvent.SELECTED));
        }
    }

	private void setEditingMode() {
		remove(translatingPlugin);
        remove(rotatingPlugin);
        remove(shearingPlugin);
        remove(pickingPlugin);
        add(animatedPickingPlugin);	
	}
	
	public Mode getMode()
	{
		return this.mode;
	}
}
