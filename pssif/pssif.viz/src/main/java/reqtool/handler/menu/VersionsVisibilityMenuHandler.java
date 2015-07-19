package reqtool.handler.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;

import reqtool.bus.ReqToolReqistry;
import reqtool.controller.RequirementVersionManager;
import reqtool.event.HideVersionsEvent;
import reqtool.event.ShowVersionsEvent;
import reqtool.event.menu.VersionVisibilityMenuEvent;

import com.google.common.eventbus.Subscribe;

/**
 * The Class VersionsVisibilityMenuHandler.
 */
public class VersionsVisibilityMenuHandler implements MenuEventHandler {

	/**
	 * Handle versions visibility menu event.
	 *
	 * @param event the menu event
	 */
	@Subscribe
	public void handleEventMenu(final VersionVisibilityMenuEvent event) {
		JMenuItem submenu;
    	
    	if (RequirementVersionManager.getMinVersion(event.getSelectedNode()).isVisible()
    			&& !RequirementVersionManager.getMinVersion(event.getSelectedNode()).equals(RequirementVersionManager.getMaxVersion(event.getSelectedNode()))
    			){
    	
    		submenu = new JMenuItem("Hide Versions");
        	submenu.addActionListener(new ActionListener() {
    			
        		@Override
            	public void actionPerformed(ActionEvent e) {
        			ReqToolReqistry.getInstance().post(new HideVersionsEvent(event.getSelectedNode(), event.getGViz()));
            	}
    		}
        	);
    	} else {
    		submenu = new JMenuItem("Show Versions");
        	submenu.addActionListener(new ActionListener() {
    			
        		@Override
            	public void actionPerformed(ActionEvent e){
        			ReqToolReqistry.getInstance().post(new ShowVersionsEvent(event.getSelectedNode(), event.getGViz()));
            	}
    		});
    		
    	}
    	event.getMenu().add(submenu);
	}
	
}
