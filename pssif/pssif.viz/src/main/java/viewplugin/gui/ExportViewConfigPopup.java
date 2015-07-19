package viewplugin.gui;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileNameExtensionFilter;

import viewplugin.logic.ViewManager;


/**
 * 
 * @author deniz
 *
 *Popup for exporting ViewConfigurations to XML Format
 *
 */
@SuppressWarnings("serial")
public class ExportViewConfigPopup extends JFileChooser {

	public void showPopup(JFrame parent, String selectedProjectViewName) {
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"XML Files", "xml");
		this.setFileFilter(filter);
		int returnVal = this.showSaveDialog(parent);
		handleOptions(returnVal, selectedProjectViewName);
	}

	private void handleOptions(int returnVal, String projectViewName) {

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			String fileDir = this.getCurrentDirectory().getAbsolutePath();
			String fileName = this.getSelectedFile().getName();
			ViewManager.exportSingleProjectViewConfig(projectViewName, fileDir, fileName);
		}
	}

}
