package reqtool.handler.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import model.ModelBuilder;
import reqtool.bus.ReqToolReqistry;
import reqtool.controller.RequirementToolbox;
import reqtool.event.CreateTestCaseEvent;
import reqtool.event.HideContainementEvent;
import reqtool.event.NewDeriveRequirementEvent;
import reqtool.event.NewVersionEvent;
import reqtool.event.ReqInfoEvent;
import reqtool.event.ResolveIssueEvent;
import reqtool.event.ShowContainementEvent;
import reqtool.event.VerifyTestCaseEvent;
import reqtool.event.menu.CreateReqMenuEvent;
import reqtool.event.menu.TraceReqMenuEvent;
import reqtool.event.menu.VersionVisibilityMenuEvent;
import reqtool.handler.EventHandler;

import com.google.common.eventbus.Subscribe;

import de.tum.pssif.core.metamodel.external.PSSIFCanonicMetamodelCreator;

/**
 * The Class CreateReqMenuEventHandler.
 */
public class CreateReqMenuEventHandler implements EventHandler {

	/**
	 * Handle requirement node menu.
	 *
	 * @param event the event
	 */
	@Subscribe
	public void handleReqPopupMenuEvent(CreateReqMenuEvent event) {
		if (event.getSelectedNode().getNodeType().equals((ModelBuilder.getNodeTypes().getValue(PSSIFCanonicMetamodelCreator.TAGS.get("N_REQUIREMENT"))))) {
			
			JMenu reqMenu = new JMenu("Requirement Tool");

			ReqToolReqistry.getInstance().post(new TraceReqMenuEvent(event.getSelectedNode(), reqMenu, event.getGViz()));;
			
			JMenuItem subItem1 = getInfoMenuItem(event);
			JMenuItem deriveReqItem = deriveRequirementMenuItem(event);
			JMenuItem subItem2 = createTestCaseMenuItem(event);
			
			reqMenu.add(subItem1);
			reqMenu.add(deriveReqItem);
			reqMenu.add(subItem2);
			reqMenu.add(getVersMngMenuItem(event));
			
			event.getPopup().add(reqMenu);
			
		} else if (event.getSelectedNode().getNodeType().equals((ModelBuilder.getNodeTypes().getValue(PSSIFCanonicMetamodelCreator.TAGS.get("N_TEST_CASE"))))) {
			
			JMenuItem subItem = verifyTestCaseMenuItem(event);
			event.getPopup().add(subItem);
		
		} else if (event.getSelectedNode().getNodeType().equals((ModelBuilder.getNodeTypes().getValue(PSSIFCanonicMetamodelCreator.TAGS.get("N_ISSUE"))))) {
			
			JMenuItem subItem = resolveIssueMenuItem(event);
			event.getPopup().add(subItem);
		
		} else if (RequirementToolbox.getSpecArtifTypes().contains(event.getSelectedNode().getNodeType())){
				
			JMenuItem subItem = showHideContaineMenuItemr(event);
			event.getPopup().add(subItem);
		}
		
		if (ModelBuilder.getNodeTypes().getValue(PSSIFCanonicMetamodelCreator.TAGS.get("N_SOL_ARTIFACT")).getType().isAssignableFrom(event.getSelectedNode().getNodeType().getType())) {

			event.getPopup().add(getVersMngMenuItem(event));

		}

	}
	
	/**
	 * Gets the version manager menu item.
	 *
	 * @param event the event
	 * @return the vers mng item
	 */
	private JMenu getVersMngMenuItem(CreateReqMenuEvent event) {
		JMenu versMenu = new JMenu("Version Management");
		
		JMenuItem subItem = createNewVersionMenuItem(event);
		versMenu.add(subItem);
		ReqToolReqistry.getInstance().post(new VersionVisibilityMenuEvent(event.getSelectedNode(), versMenu, event.getGViz()));
		
		return versMenu;
	}
	
	/**
	 * Creates the test case menu item.
	 *
	 * @param event the event
	 * @return the j menu item
	 */
	private JMenuItem createTestCaseMenuItem(final CreateReqMenuEvent event) {
    	JMenuItem submenu;
    	submenu = new JMenuItem("Create Test Case");
    	submenu.addActionListener(new ActionListener() {

    		@Override
        	public void actionPerformed(ActionEvent e) {
    			ReqToolReqistry.getInstance().post(new CreateTestCaseEvent(event.getSelectedNode(), event.getGViz()));
        	}
		});
 		return submenu;
 	}
	
	/**
	 * Resolve issue menu item.
	 *
	 * @param event the event
	 * @return the j menu item
	 */
	private JMenuItem resolveIssueMenuItem(final CreateReqMenuEvent event)  {
    	JMenuItem submenu;
    	submenu = new JMenuItem("Resolve Issue");
    	submenu.addActionListener(new ActionListener() {
    		@Override
        	public void actionPerformed(ActionEvent e) {
    			ReqToolReqistry.getInstance().post(new ResolveIssueEvent(event.getSelectedNode(), event.getGViz()));
    		}
		});
 		return submenu;
	}
	
	/**
	 * Creates the new version menu item.
	 *
	 * @param event the event
	 * @return the j menu item
	 */
	private JMenuItem createNewVersionMenuItem(final CreateReqMenuEvent event) {
    	JMenuItem submenu;
    	submenu = new JMenuItem("Create new version");
    	submenu.addActionListener(new ActionListener() {
    		@Override
        	public void actionPerformed(ActionEvent e) {
    			ReqToolReqistry.getInstance().post(new NewVersionEvent(event.getSelectedNode(), event.getGViz()));
            }

        });
 		return submenu;
 	}
	
	/**
	 * Derive requirement menu item.
	 *
	 * @param event the event
	 * @return the j menu item
	 */
	private JMenuItem deriveRequirementMenuItem(final CreateReqMenuEvent event) {
    	JMenuItem submenu;
    	submenu = new JMenuItem("Derive Requirement");
    	submenu.addActionListener(new ActionListener() {
    		@Override
        	public void actionPerformed(ActionEvent e) {
    			ReqToolReqistry.getInstance().post(new NewDeriveRequirementEvent(event.getSelectedNode(), event.getGViz()));
            }

        });
 		return submenu;
 	}
	
	/**
	 * Verify test case menu item.
	 *
	 * @param event the event
	 * @return the j menu item
	 */
	private JMenuItem verifyTestCaseMenuItem(final CreateReqMenuEvent event) {
    	JMenuItem submenu;
    	submenu = new JMenuItem("Verify Test Case");
    	submenu.addActionListener(new ActionListener() {
    		@Override
        	public void actionPerformed(ActionEvent e) {
    			ReqToolReqistry.getInstance().post(new VerifyTestCaseEvent(event.getSelectedNode(), event.getGViz()));
        	}
		});
 		return submenu;
	}
	
	/**
	 * Show hide container menu item.
	 *
	 * @param event the event
	 * @return the j menu item
	 */
	private JMenuItem showHideContaineMenuItemr(final CreateReqMenuEvent event) {
		JMenuItem submenu = new JMenuItem("");
		
		if (RequirementToolbox.hasContainment(event.getSelectedNode())) {
			if (RequirementToolbox.containmentIsVisible(event.getSelectedNode())) {

				submenu = new JMenuItem("Hide Containment");
				submenu.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						ReqToolReqistry.getInstance().post(new HideContainementEvent(event.getSelectedNode(), event.getGViz()));
					}
				});
			} else {
				submenu = new JMenuItem("Show Containment");
				submenu.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						ReqToolReqistry.getInstance().post(new ShowContainementEvent(event.getSelectedNode(), event.getGViz()));
					}
				});
			}
		} else {
			submenu = new JMenuItem("");
		}

		return submenu;
	}
	
	/**
	 * Gets the info menu item.
	 *
	 * @param event the event
	 * @return the info menu item
	 */
	private JMenuItem getInfoMenuItem(final CreateReqMenuEvent event) {
    	JMenuItem submenu;
    	submenu = new JMenuItem("Get Info");
    	submenu.addActionListener(new ActionListener() {
    		@Override
        	public void actionPerformed(ActionEvent e) {
    			ReqToolReqistry.getInstance().post(new ReqInfoEvent(event.getSelectedNode()));
        	}
		});
 		return submenu;
	}
	
}
