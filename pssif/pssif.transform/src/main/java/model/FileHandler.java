package model;

import java.awt.FlowLayout;
import java.util.TreeMap;

import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;

import de.tum.pssif.transform.MapperFactory;
/**
 * Initializes the Import and Export File Dialog. Should not be instantiated 
 * @author Luc
 *
 */
public class FileHandler {

	  private TreeMap<String, String> comboBoxValues;
	  private JFileChooser fileChooser;
	  private JComboBox<String> fileType;
	  
	  /**
	   * 
	   * @param labelText ("Select a Export File Format:") or ("Select a Import File Format:")
	   */
	  public FileHandler(String labelText) {
	    //Add the Export and Import Values
		comboBoxValues = new TreeMap<String, String>();

	    comboBoxValues.put("Umsatzorientierte Funktionsplanung", MapperFactory.UOFP);
	   // comboBoxValues.put("BPMN", MapperFactory.BPMN);
	    comboBoxValues.put("EPK", MapperFactory.EPK);
	    comboBoxValues.put("SysML", MapperFactory.SYSML);
	    comboBoxValues.put("PSS-IF", MapperFactory.PSSIF);
	    comboBoxValues.put("ReqIf", MapperFactory.REQ_IF);
	    comboBoxValues.put("RDF/TTL", MapperFactory.RDF_TTL);
	    comboBoxValues.put("RDF/XML", MapperFactory.RDF_XML);

	    // bind the label and the combobox to the dialog
	    fileChooser = new JFileChooser();

	    JPanel panel1 = (JPanel) fileChooser.getComponent(3);
	    JPanel panel2 = (JPanel) panel1.getComponent(0);

	    JPanel importeType = new JPanel(new FlowLayout());
	    JLabel label1 = new JLabel(labelText);
	    importeType.add(label1);
	    fileType = new JComboBox<String>(comboBoxValues.keySet().toArray(new String[0]));
	    fileType.setSelectedIndex(0);
	    importeType.add(fileType);

	    panel2.add(importeType);
	  }
	
	/**
	 * Get the File Chooser Dialog
	 * @return File Chooser Dialog
	 */
	public JFileChooser getFileChooser() {
		return fileChooser;
	}


	/**
	 * Get the selected File Format
	 * @return the String which the Mapper Factory needs, to create the correct Mapper
	 */
	public String getSelectedMapperFactoryValue()
	{
		 String selectedFileType = String.valueOf(fileType.getSelectedItem());
		 
		 return comboBoxValues.get(selectedFileType);
	}
	  
}
