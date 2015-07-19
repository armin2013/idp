package viewplugin.gui;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import viewplugin.logic.ViewManager;

/**
 * 
 * @author deniz
 *
 *         Popup for importing ViewCondigurations from XML Files
 *
 */
@SuppressWarnings("serial")
public class ImportViewConfigPopup extends JFileChooser {

	
	
	public void showPopup(ManageViewsPopup parent) {
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"XML Files", "xml");
		this.setFileFilter(filter);
		int returnVal = this.showOpenDialog(parent);
		handleOptions(returnVal);
		parent.refreshJList();
	}

	private void handleOptions(int returnVal) {

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File importXML = this.getSelectedFile();
			ViewManager.importExternalViewConfig(importXML);
		}
	}
	

}
