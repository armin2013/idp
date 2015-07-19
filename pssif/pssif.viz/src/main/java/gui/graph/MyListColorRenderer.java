package gui.graph;

import graph.model.MyNodeType;

import java.awt.Color;
import java.awt.Component;
import java.util.HashMap;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
/**
 * Allows to change the background color of a entry in a JList
 * @author Luc
 *
 */
public class MyListColorRenderer extends DefaultListCellRenderer  
	{  
		private static final long serialVersionUID = 1L;
		private HashMap<MyNodeType, Color> colorMapper;

	    public Component getListCellRendererComponent( @SuppressWarnings("rawtypes") JList list,  
	    		Object value, int index, boolean isSelected,  
	            boolean cellHasFocus )  
	    {  
	        super.getListCellRendererComponent( list, value, index,  
	                isSelected, cellHasFocus );  
	         
	        MyNodeType t = (MyNodeType) value;
	        if( colorMapper.containsKey( t ) )  
	        {  
	            Color c = colorMapper.get(t);

	            setBackground(c);
	        }  


	        return( this );  
	    }
	    
	    public void setColor (MyNodeType type , Color c)
	    {
	    	this.colorMapper.put(type, c);
	    }
	    
	    public HashMap<MyNodeType, Color> getColorMapping()
	    {
	    	return this.colorMapper;
	    }
	    
	    public MyListColorRenderer()
	    {
	    	super();
	    	colorMapper = new HashMap<MyNodeType, Color>();
	    	
	    }
	    
	    public void setColors(HashMap<MyNodeType,Color> map)
	    {
	    	this.colorMapper = map;	
	    }
}
