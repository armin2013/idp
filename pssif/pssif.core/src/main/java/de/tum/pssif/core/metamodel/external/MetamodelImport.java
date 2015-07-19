package de.tum.pssif.core.metamodel.external;

import java.util.HashMap;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Initiates the import of the metamodel-xml file
 * 
 * @author Alex
 *
 */
public class MetamodelImport {
	private XMLContentHandler handler;

	/**
	 * Initiate the parser and return its result
	 * 
	 * @return A list of the component as imported by the parser
	 */
	public void runParser() {

		try {
			// Create Reader
			XMLReader xmlReader = XMLReaderFactory.createXMLReader();

			// Path to file
			// String path = System.getProperty("user.dir");
			// FileReader reader = new FileReader(path.substring(0,
			// path.length()-9) + "Meta-Modell.xml");
			FileReader reader = new FileReader("C:\\Users\\Meta-Modell.xml");
			InputSource inputSource = new InputSource(reader);

			// XMLConentHandler is handed over
			handler = new XMLContentHandler();
			xmlReader.setContentHandler(handler);

			// Initialize the parser
			xmlReader.parse(inputSource);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get a HashMap containing every tag of the imported components
	 * 
	 * @return The tags
	 */
	public HashMap<String, String> getTagMap() {
		return handler.getTagMap();
	}

	/**
	 * Get a HashMap containing every conjunction
	 * 
	 * @return The conjunctions
	 */
	public HashMap<String, MetamodelConjunction> getConjunctions() {
		return handler.getConjunctions();
	}

	/**
	 * Get a HashMap containing every node
	 * 
	 * @return The nodes
	 */
	public HashMap<String, MetamodelNode> getNodes() {
		return handler.getNodes();
	}

	/**
	 * Get a HashMap containing every edge
	 * 
	 * @return The edges
	 */
	public HashMap<String, MetamodelEdge> getEdges() {
		return handler.getEdges();
	}

	/**
	 * Get a HashMap containing every attribute
	 * 
	 * @return The attributes
	 */
	public HashMap<String, MetamodelAttribute> getAttributes() {
		return handler.getAttributes();
	}
}
