package model;

import java.awt.Component;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import de.tum.pssif.core.model.Model;
import de.tum.pssif.transform.Mapper;
import de.tum.pssif.transform.MapperFactory;


public class FileImporter extends FileHandler{
	private Model importedModel;

  public FileImporter() {
	super("Select a Import File Format:");
  }

  /**
   * Read the selected file. Select the correct importer. Load the Nodes and Edges with the ModelBuilder
   * @param file the selected file which should be imported
   * @return true if no errors occured, false otherwise
   */
  private boolean importFile(File file) {
    // Read the Inputfile 
    InputStream in;
    try {
      in = new FileInputStream(file);

      Mapper importer = MapperFactory.getMapper(super.getSelectedMapperFactoryValue());

      //Model model;
      try {
    	  importedModel = importer.read(ModelBuilder.getMetamodel(), in);
        if(importedModel!=null)
        {
	        // Create the Viz Model
	        ModelBuilder.addModel(ModelBuilder.getMetamodel(), importedModel);
	        return true;
        }
        else
        {
        	createErrorPopup("This Transformation is not implemented in the PSS-IF Transfrom project");
	        return false;
        }    
      }
      catch (Exception e ) {
	        e.printStackTrace();
	        createErrorPopup("There was a problem transforming the selected file. Was the file type chosen correctly?");
	        return false;
      }
      
    } catch (FileNotFoundException e) {
    	e.printStackTrace();
    	createErrorPopup("There was a problem importing the selected file.");
    	return false;
    }
  }

  /**
   * Display the popup to the User
   * @param caller the component which called to display the popup
   * @return true if the entire import went fine, otherwise false
   */
  public boolean showPopup(Component caller) {
	super.getFileChooser().setCurrentDirectory(new File("C:\\Users\\ami\\Dropbox\\IDP-PSS-IF-Shared\\Modelle PE"));
	// super.getFileChooser().setCurrentDirectory(new File("C:\\Users\\Luc\\Desktop\\Dropbox\\IDP-PSS-IF-Shared\\Modelle PE"));
    int returnVal = super.getFileChooser().showOpenDialog(caller);

    if (returnVal == JFileChooser.APPROVE_OPTION) {

      File file = super.getFileChooser().getSelectedFile();

      return importFile(file);

    }
    else {
      return false;
    }
  }
  
  public Model getLastImportedModel() {
	  return importedModel;
  }
  
  private void createErrorPopup (String text)
  {
	  JPanel errorPanel = new JPanel();
		
      errorPanel.add(new JLabel(text));

      JOptionPane.showMessageDialog(null, errorPanel, "Ups something went wrong", JOptionPane.ERROR_MESSAGE);
  }

}
