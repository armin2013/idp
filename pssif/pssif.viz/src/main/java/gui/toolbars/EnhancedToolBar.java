package gui.toolbars;

import java.awt.Image;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JToolBar;
import javax.swing.JButton;


 
@SuppressWarnings("serial")
public class EnhancedToolBar extends JToolBar{


	BoxLayout layout;
    public EnhancedToolBar(int orientation)
    {
    	super();
    	this.setOrientation(orientation);
    }
    
    public JButton addButton(String text, ActionListener action, String tooltip, boolean icon)
    {
    	JButton button;
    	if (icon)
    	{
    		ImageIcon img = new ImageIcon(text);
    		img.setImage(img.getImage().getScaledInstance(32, 32, Image.SCALE_DEFAULT));
    		button = new JButton(img);
    	}
    	else
    	{
    		button = new JButton();
    		button.setText(text);
    	}
    	button.addActionListener(action);
    	button.setBorder(null);
    	if (tooltip != null && tooltip != "")
    		button.setToolTipText(tooltip);
        this.add(button);
        return button;
    }
    
    
}