package model;

import java.awt.Component;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import de.tum.pssif.transform.Mapper;
import de.tum.pssif.transform.MapperFactory;

/**
 * Provides the possibility to export the current PSS-IF Model Different export
 * Formats can be chosen
 * 
 * @author Luc
 *
 */
public class FileExporter extends FileHandler {

	public FileExporter() {
		super("Select a Export File Format:");
	}

	/**
	 * Read the selected file. Select the correct exporter.
	 * 
	 * @param file
	 *            the selected file which should be imported
	 * @return true if no errors occurred, false otherwise
	 */
	private boolean exportFile(File file) {
		// Check if the OutputFile exists

		if (!file.isDirectory()) {
			try {
				boolean fileCreation = true;
				if (!file.exists())
					fileCreation = file.createNewFile();
				else {
					/*
					 * createErrorPopup("Filename already exists");
					 * System.out.println("filename exists"); return false;
					 */
					file.delete();
					fileCreation = file.createNewFile();

				}

				if (!fileCreation) {
					createErrorPopup("Filename already exists");
					System.out.println("filename exists");
					return false;
				}

				OutputStream out = new FileOutputStream(file);

				Mapper exporter = MapperFactory.getMapper(super
						.getSelectedMapperFactoryValue());

				exporter.write(ModelBuilder.getMetamodel(),
						ModelBuilder.getModel(), out);

				return true;

			} catch (FileNotFoundException e) {
				e.printStackTrace();

				createErrorPopup("There was a problem exporting the selected file.");
			} catch (IOException e) {
				e.printStackTrace();
				createErrorPopup("An error occured while creating the new file");
			}

			catch (Exception e) {
				e.printStackTrace();
				createErrorPopup("There was a problem transforming the Model.");
			}
			return false;
		} else {
			System.out.println("File is no file test" + file.getAbsolutePath());
			return false;
		}

	}

	/**
	 * Display the popup to the User
	 * 
	 * @param caller
	 *            the component which called to display the popup
	 * @return true if the entire export went fine, otherwise false
	 */
	public boolean showPopup(Component caller) {
		int returnVal = super.getFileChooser().showSaveDialog(caller);

		if (returnVal == JFileChooser.APPROVE_OPTION) {

			File file = super.getFileChooser().getSelectedFile();

			return exportFile(file);

		} else {
			return false;
		}
	}

	private void createErrorPopup(String text) {
		JPanel errorPanel = new JPanel();

		errorPanel.add(new JLabel(text));

		JOptionPane.showMessageDialog(null, errorPanel,
				"Ups something went wrong", JOptionPane.ERROR_MESSAGE);
	}
}
