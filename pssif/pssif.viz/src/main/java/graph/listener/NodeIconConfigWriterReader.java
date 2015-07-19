package graph.listener;


import graph.model.MyEdgeType;
import graph.model.MyNodeType;
import graph.operations.GraphViewContainer;
import gui.graph.ImageImporter;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import model.ModelBuilder;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Allows to read/write the users configuration to an XML file
 * @author Luc
 *
 */
public class NodeIconConfigWriterReader {
	

	private static File CONFIG_FILE;
	private static String ROOT_CONFIG = "config";
	private static String NODE_ICONS = "nodeIcons";
	private static String NODE_TYPES = "nodeType";
	private static String ATTR_ICON = "icon";
	
	private static String GRAPH_VIEWS ="graphViews";
	private static String GRAPH_VIEW ="graphView";
	private static String ATTR_VIEWNAME ="viewName";
	private static String VISIBLE_NODETYPES ="visibleNodeTypes";
	private static String VISIBLE_NODETYPE ="visibleNodeType";
	private static String VISIBLE_EDGETYPES ="visibleEdgeTypes";
	private static String VISIBLE_EDGETYPE ="visibleEdgeTypes";
	
	/**
	 * Creates a new ConfigWriterReader class
	 */
	public NodeIconConfigWriterReader()
	{
		CONFIG_FILE = new File("iconConfig.xml");
		
		addStandardIcons();
	}
	
	/**
	 * Writes a MyNodeType icon configuration to the XML file
	 * @param iconmapping : a Mapping from the appropriate MyNodeTypes to their icon
	 */
	public void setIcons(HashMap<MyNodeType,Icon> iconmapping)
	{
		writeNewConfig(iconmapping);
		/*
		if (!CONFIG_FILE.exists())
			writeNewConfig(iconmapping);
		else
		{
			
			if (!iconConfigExists())
			{
				// The file exists but the corresponding xml icon configuration tags are not yet there
				// so we have to add them
				addBasicIconInfo();
			}
			// call the update function
			updateIcons(iconmapping);
		}
		*/
	}
	/**
	 * Writes a new graph view to the XML file
	 * @param view : GraphViewContainer contains the view name and the corresponding MyNodeTypes and MyEdgedTypes
	 */
	public void setGraphView(GraphViewContainer view)
	{
		if (!CONFIG_FILE.exists())
			writeNewConfig(view);
		else
			updateGraphView(view);
	}
	
	/**
	 * Write a new Icon configuration from scratch
	 * @param iconmapping : HashMap<MyNodeType,Icon>
	 */
	private void writeNewConfig(HashMap<MyNodeType,Icon> iconmapping)
	{
		
		 try {
			 
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
 
		// root elements
		Document doc = docBuilder.newDocument();
		Element rootElement = doc.createElement(ROOT_CONFIG);
		doc.appendChild(rootElement);
 
		// nodecolors elements
		Element nodeicons = doc.createElement(NODE_ICONS);
		rootElement.appendChild(nodeicons);
 
		Set<MyNodeType> nodetypes = iconmapping.keySet();
		
		// add every Nodetype and his icon to the xml file
		for (MyNodeType t :nodetypes)
		{
			Element node = doc.createElement(NODE_TYPES);
			node.appendChild(doc.createTextNode(t.getName()));
			
			Attr attr = doc.createAttribute(ATTR_ICON);
			ImageIcon temp = (ImageIcon) iconmapping.get(t);
			attr.setValue(temp.getDescription());
			node.setAttributeNode(attr);
			
			nodeicons.appendChild(node);
			
		}

		// write the content into xml file
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(CONFIG_FILE);

		transformer.transform(source, result);
 
 
	  } catch (ParserConfigurationException pce) {
		pce.printStackTrace();
	  } catch (TransformerException tfe) {
		tfe.printStackTrace();
	  }
	}
	
	/**
	 * If there are already some MyNodeType and Icon mappings in the xml file, we have to override certains with the new Icons
	 * @param newIconMapping : HashMap<MyNodeType,Icon>
	 */
	private void updateIcons (HashMap<MyNodeType,Icon> newIconMapping)
	{
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(CONFIG_FILE);
			
			doc.getDocumentElement().normalize();
	 
			// Get the NodeIcons element by tag name directly
			NodeList iconmapping = doc.getElementsByTagName(NODE_TYPES);
			
			Node oldNodeIcons = doc.getElementsByTagName(NODE_ICONS).item(0);
			
			// Get all the values from the newIconMapping
			
			Set<MyNodeType> newNodeTypes = newIconMapping.keySet();
			
			// Update already all the MyNodeTypes which already exist in the xml file with the new Icon
			Set<MyNodeType> checkedNodeTypes = tryUpdate(iconmapping, newIconMapping);
			
			// remove all the MyNodeTypes which were already checked
			newNodeTypes.removeAll(checkedNodeTypes);
			
			//add the new MyNodeTypes to the xml file
			for (MyNodeType currentType: newNodeTypes)
			{
				Element node = doc.createElement(NODE_TYPES);
				node.appendChild(doc.createTextNode(currentType.getName()));
				
				Attr attr = doc.createAttribute(ATTR_ICON);
				ImageIcon temp = (ImageIcon) newIconMapping.get(currentType);
				attr.setValue(temp.getDescription());
				node.setAttributeNode(attr);
				
				oldNodeIcons.appendChild(node);
			}
			
	 
			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(CONFIG_FILE);
			transformer.transform(source, result);

	 
		   } catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		   } catch (TransformerException tfe) {
			tfe.printStackTrace();
		   } catch (IOException ioe) {
			ioe.printStackTrace();
		   } catch (SAXException sae) {
			sae.printStackTrace();
		   }
	}
	
	/**
	 * Check if certain MyNodeType are already in the xml file. If yes update these MyNodeType with the new Icons
	 * @param Iconmapping : NodeList || all the XML MyNodeType Nodes with their Icon
	 * @param newIconMapping : HashMap<MyNodeType,Icon> || the new MyNodeType - Icon mapping
	 * @return a Set<MyNodeType> with all the MyNodeType elements which were already updated
	 */
	private Set<MyNodeType> tryUpdate(NodeList iconmapping, HashMap<MyNodeType,Icon> newIconMapping)
	{
		Set<MyNodeType> checkedNodeTypes = new HashSet<MyNodeType>();
		
		for (int i = 0; i<iconmapping.getLength();i++)
		{
			Node current = iconmapping.item(i);
			
			if (current.getNodeType() == Node.ELEMENT_NODE) 
			{
				 
				Element eElement = (Element) current;
				
				String nodeTypeValue = eElement.getChildNodes().item(0).getNodeValue();
				//System.out.println("From XML " +nodeTypeValue);
				
				// get the Icon value from the xml file
				//String IconValue = eElement.getAttribute(ATTR_Icon);
				
				// Build a  MyNodeType from the String value from the xml
				MyNodeType t = ModelBuilder.getNodeTypes().getValue(nodeTypeValue);

				// get the new Icon
				Icon newIcon = newIconMapping.get(t);
			//	System.out.println("new Icon "+newIcon);
				
				//Color oldColor = new Color(Integer.valueOf(colorValue));
				//System.out.println("From XML Color "+oldColor);
				
				// even if we do not need to update the MyNodeType, we do not need to check it later again
				checkedNodeTypes.add(t);
				
				// if we have a new color, update the xml file
				if (newIcon!=null)
				{
					System.out.println("changed");
					ImageIcon temp = (ImageIcon) newIcon;
					eElement.setAttribute(ATTR_ICON, temp.getDescription());
				}
			//	System.out.println("------------------------");
				
			}
		}
		
		return checkedNodeTypes;
	}
	
	/**
	 * Read all the MyNodeType - Color mappings from the XML
	 * @return HashMap<MyNodeType, Color> with all the configurations. Might be empty if there is no configuration available
	 */
	public HashMap<MyNodeType, Icon> readIcons ()
	{
		HashMap<MyNodeType, Icon> res = new HashMap<MyNodeType, Icon>();
		
		// check if the file exits
		if (CONFIG_FILE.exists())
		{
			try
			{
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document doc = dBuilder.parse(CONFIG_FILE);
			 
				doc.getDocumentElement().normalize();
			 
				NodeList nList = doc.getElementsByTagName(NODE_TYPES);
			 
				//System.out.println("----------------------------");
				
				// Loop over all the NODE_TYPES xml tags
				for (int temp = 0; temp < nList.getLength(); temp++) 
				{
					// get a specific Node
					Node nNode = nList.item(temp);
			 
					//System.out.println("\nCurrent Element :" + nNode);
			 
					if (nNode.getNodeType() == Node.ELEMENT_NODE) {
			 
						Element eElement = (Element) nNode;
						
						// get the String value of the MyNodeType
						String nodeTypeValue = eElement.getChildNodes().item(0).getNodeValue();	
						
						// Build a real MyNodeType from the String value of the xml
						MyNodeType current = ModelBuilder.getNodeTypes().getValue(nodeTypeValue);
						
						// Get the MyNodeType Icon
						//Icon c = new ImageIcon(eElement.getAttribute(ATTR_ICON));
						String fileaddr = eElement.getAttribute(ATTR_ICON);
						ImageIcon c = ImageImporter.loadImageBySize(new File(fileaddr), ImageImporter.IMG_WIDTH, ImageImporter.IMG_HEIGHT);
						c.setDescription(fileaddr);
						res.put(current, c);
					}
				}
		    } catch (Exception e) {
		    	e.printStackTrace();
		    }
		}
	//	System.out.println("Found Nb Colors: "+ res.keySet().size());
		return res;
	  }
	
	
	
	//------------------------------------------------------------------------------------------
	// Views
	
	/**
	 * Write a new Graph View configuration from scratch to the XML file
	 * @param view : GraphViewContainer || the new Graph View
	 */
	private void writeNewConfig(GraphViewContainer view)
	{
		
	 try {
			 
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
 
		// root elements
		Document doc = docBuilder.newDocument();
		Element rootElement = doc.createElement(ROOT_CONFIG);
		doc.appendChild(rootElement);
		
		// graphViews element
		Element graphViewsNode = doc.createElement(GRAPH_VIEWS);
		rootElement.appendChild(graphViewsNode);
		
		// write the view specific content to the xml file
		createGraphView(doc,graphViewsNode,view);

		// write the content into xml file
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(CONFIG_FILE);
 
		transformer.transform(source, result);
 
		//System.out.println("File saved!");
 
	  } catch (ParserConfigurationException pce) {
		pce.printStackTrace();
	  } catch (TransformerException tfe) {
		tfe.printStackTrace();
	  }
	}
	
	/**
	 *  Writes the content of the GraphViewContainer to the xml file 
	 * @param doc : Document || in which Document the data should be written
	 * @param graphViewsNode : Element || the "root" XML Node, where under which the new Graph view should be placed
	 * @param view : GraphViewContainer || which information should be written to the file
	 */
	private void createGraphView(Document doc, Element graphViewsNode, GraphViewContainer view)
	{
				// graphView element
				Element graphViewNode = doc.createElement(GRAPH_VIEW);
				Attr attr = doc.createAttribute(ATTR_VIEWNAME);
				attr.setValue(view.getViewName());
				graphViewNode.setAttributeNode(attr);
				
				//System.out.println();
				graphViewsNode.appendChild(graphViewNode);
				
				Element visibleNodeTypesNode = doc.createElement(VISIBLE_NODETYPES);
				graphViewNode.appendChild(visibleNodeTypesNode);
				
				// loop over the Node Types
				for (MyNodeType t :view.getSelectedNodeTypes())
				{
					Element visibleNodeTypeNode = doc.createElement(VISIBLE_NODETYPE);
					visibleNodeTypeNode.appendChild(doc.createTextNode(t.getName()));
					
					visibleNodeTypesNode.appendChild(visibleNodeTypeNode);
					
				}
				
				Element visibleEdgeTypesNode = doc.createElement(VISIBLE_EDGETYPES);
				graphViewNode.appendChild(visibleEdgeTypesNode);
				
				// loop over the Edge Types
				for (MyEdgeType t :view.getSelectedEdgeTypes())
				{
					Element visibleEdgeTypeNode = doc.createElement(VISIBLE_EDGETYPE);
					visibleEdgeTypeNode.appendChild(doc.createTextNode(t.getName()));
					
					visibleEdgeTypesNode.appendChild(visibleEdgeTypeNode);
					
				}
	}
	/**
	 * If there exists already a XML  configuration File we can update this file, with the new content
	 * @param view : GraphViewContainer
	 */
	private void updateGraphView (GraphViewContainer view)
	{
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(CONFIG_FILE);
			
			doc.getDocumentElement().normalize();
			
			// Get the root element
			Node root = doc.getFirstChild();
			
			// Get all the GrapView elements by tag name directly
			NodeList graphviewNodes = doc.getElementsByTagName(GRAPH_VIEW);
			
			// check if there is already a view with the same name in the xml file
			Element currentGraphViewNode = graphViewAlreadyExists(view.getViewName(), graphviewNodes);
			
			if (currentGraphViewNode==null)
			{
				// graphView does not exist yet. Create a new
				
				//check if there is any View defined yet
				NodeList graphViewsNodes = doc.getElementsByTagName(GRAPH_VIEWS);
				
				Element graphViewsNode;
				if (graphViewsNodes==null || graphViewsNodes.getLength()==0)
				{
					// graphViews element
					graphViewsNode = doc.createElement(GRAPH_VIEWS);
					root.appendChild(graphViewsNode);
				}
				else
				{
					graphViewsNode = (Element) doc.getElementsByTagName(GRAPH_VIEWS).item(0);
				}
				/*else
				{
					//!!!!!!!
					graphViewsNode = (Element) graphviewNodes.item(0);
					// check if graphViews has subNodes
					if (graphViewsNode==null || graphViewsNode.getChildNodes()==null || graphViewsNode.getChildNodes().getLength()==0)
					{
						// if it has none. Create a new Object
						
						graphViewsNode = doc.createElement(GRAPH_VIEWS);
						root.appendChild(graphViewsNode);
					}
					
				}*/
				
			//	System.out.println("graphViewsNode null ? "+graphViewsNode==null);
				createGraphView(doc,graphViewsNode,view);
				
			}
			else
			{
				// graphView does exist. Delete the old one and insert a new one				
				root.removeChild(currentGraphViewNode);
				Element graphViewsNode = (Element) doc.getElementsByTagName(GRAPH_VIEWS).item(0);
				
				createGraphView(doc,graphViewsNode,view);
			}	
			
			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(CONFIG_FILE);
			transformer.transform(source, result);
	 
			//System.out.println("Done");
	 
		   } catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		   } catch (TransformerException tfe) {
			tfe.printStackTrace();
		   } catch (IOException ioe) {
			ioe.printStackTrace();
		   } catch (SAXException sae) {
			sae.printStackTrace();
		   }
	}
	
	/**
	 * Checks if the given viewName already exists in the XML file
	 * @param viewName : String || the new viewName
	 * @param graphviewNodes : NodeList || all the ViewNames from the XML file
	 * @return if the viewName does already exits it returns the appropriate Element, else null
	 */
	private Element graphViewAlreadyExists(String viewName, NodeList graphviewNodes)
	{
		// are there any GraphView Nodes yet?
		if (graphviewNodes!=null)
		{
			// loop over all the  GraphView Nodes
			for (int i=0; i<graphviewNodes.getLength();i++)
			{
				Node currentGraphView = graphviewNodes.item(i);
				
				if (currentGraphView.getNodeType() == Node.ELEMENT_NODE) 
				{
					 
					Element eElement = (Element) currentGraphView;
					
					String currentViewName = eElement.getAttribute(ATTR_VIEWNAME);
					// check if they have the same name. If yes, break the loop an return the current Element
					if (currentViewName.equals(viewName))
						return eElement;
				}
			}
		}
		// there was no GraphView with the given name
		return null;
	}

	/**
	 * Reads all the Graph Views from the XML File
	 * @return HashMap<String, GraphViewContainer> || a mapping from the name of the view to the GraphViewContainer with all the information. Might be empty
	 */
	public HashMap<String, GraphViewContainer> readViews ()
	{
		HashMap<String, GraphViewContainer> res = new HashMap<String, GraphViewContainer>();
		
		if (CONFIG_FILE.exists())
		{
			try
			{
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document doc = dBuilder.parse(CONFIG_FILE);
			 
				doc.getDocumentElement().normalize();
			 
				NodeList nList = doc.getElementsByTagName(GRAPH_VIEW);
			 
				//System.out.println("----------------------------");
				
				// Loop over all the GRAPH_VIEW elements
				for (int temp = 0; temp < nList.getLength(); temp++) 
				{
			 
					Node nNode = nList.item(temp);
			 
					//System.out.println("\nCurrent Element :" + nNode);
			 
					if (nNode.getNodeType() == Node.ELEMENT_NODE) {
			 
						Element graphViewElement = (Element) nNode;
						
						// get all the information form the current GRAPH_VIEW Node
						String viewName = String.valueOf(graphViewElement.getAttribute(ATTR_VIEWNAME));
						
						Node NodeTypes = graphViewElement.getElementsByTagName(VISIBLE_NODETYPES).item(0);
						Node EdgeTypes = graphViewElement.getElementsByTagName(VISIBLE_EDGETYPES).item(0);
						
						NodeList NodeTypeList = ((Element)NodeTypes).getElementsByTagName(VISIBLE_NODETYPE);
						NodeList EdgeTypeList = ((Element)EdgeTypes).getElementsByTagName(VISIBLE_EDGETYPE);
						
						LinkedList<MyNodeType> nodetypes = new LinkedList<MyNodeType>();
						
						// Loop over all the NodeType elements from the XMl file
						for (int i = 0; i < NodeTypeList.getLength(); i++)
						{
							Node currentNode = NodeTypeList.item(i);
							 
							//System.out.println("\nCurrent Element :" + nNode);
					 
							if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
					 
								Element eElement = (Element) currentNode;
								
								String nodetypename = eElement.getChildNodes().item(0).getNodeValue();
								MyNodeType current = ModelBuilder.getNodeTypes().getValue(nodetypename);
								
								nodetypes.add(current);
							}
						}
						
						LinkedList<MyEdgeType> edgetypes = new LinkedList<MyEdgeType>();
						// Loop over all the EdgeType elements from the XMl file
						for (int i = 0; i < EdgeTypeList.getLength(); i++)
						{
							Node currentNode = EdgeTypeList.item(i);
							 
							//System.out.println("\nCurrent Element :" + nNode);
					 
							if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
					 
								Element eElement = (Element) currentNode;
								
								String edgetypename = eElement.getChildNodes().item(0).getNodeValue();
								MyEdgeType current = ModelBuilder.getEdgeTypes().getValue(edgetypename);
								
								edgetypes.add(current);
							}
						}
						
						// create the container with the information and add it to the Hashmap
						GraphViewContainer view = new GraphViewContainer(nodetypes, edgetypes, viewName);
						
						res.put(viewName, view);

					}
				}
		    } catch (Exception e) {
		    	e.printStackTrace();
		    }
		}
		return res;
	  }
	
	/**
	 * Checks if there are already some MyNodeType - Icon mappings in the XML available
	 * @return boolean || true if there are some, false if there are none
	 */
	private boolean iconConfigExists()
	{
		boolean result = false;
		
		try
		{
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(CONFIG_FILE);
		 
			doc.getDocumentElement().normalize();
		 
			NodeList nList = doc.getElementsByTagName(NODE_ICONS);
			
			if (nList==null || nList.getLength()==0)
				result =false;
			else
				result=true;
				
		}
	    catch (Exception e) {
	    	e.printStackTrace();
	    }
		
		return result;
	}
	
	/**
	 * Adds the basic XML structure for the MyNodeType - Icon mappings to the XML File
	 */
	private void addBasicIconInfo()
	{
		 try {
			 
			 	DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
				Document doc = docBuilder.parse(CONFIG_FILE);
				
				doc.getDocumentElement().normalize();
				
				// Get the root element
				Element rootElement = (Element) doc.getFirstChild();
		 
				// nodeicons elements
				Element nodeicons = doc.createElement(NODE_ICONS);
				rootElement.appendChild(nodeicons);
				
				// write the content into xml file
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(doc);
				StreamResult result = new StreamResult(CONFIG_FILE);
		 
				transformer.transform(source, result);
		 
				//System.out.println("File saved!");
		 }
		    catch (Exception e) {
		    	e.printStackTrace();
		    }
	}
	
	/**
	 * Deletes a View from the XML file
	 * @param deleteView : String || the name of the view which should be deleted
	 */
	public void deleteView(String deleteView)
	{
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(CONFIG_FILE);
			doc.getDocumentElement().normalize();
			
			// Get the graphview elements
			NodeList viewList = doc.getElementsByTagName(GRAPH_VIEW);
			
			Node deleteNode=null;
			
			//iterate over the views
			for (int i =0; i<viewList.getLength();i++)
			{
				Node currentView = viewList.item(i);
				
				NamedNodeMap attr = currentView.getAttributes();
				Node nodeAttr = attr.getNamedItem(ATTR_VIEWNAME);
				
				//System.out.println("delete test: "+nodeAttr.getNodeValue());
				// check if is the current Node
				if (nodeAttr.getNodeValue().equals(deleteView))
				{
				//	System.out.println("delete selected				: "+nodeAttr.getNodeValue());
					deleteNode=currentView;
				}
			}
			
			Node views = doc.getElementsByTagName(GRAPH_VIEWS).item(0);
			Node root = doc.getFirstChild();
			// delete the view which we found in the XML file
			if (views!=null && deleteNode!=null)
			{
				views.removeChild(deleteNode);
				
				// if it was the last view, delete all the XML tags associated with GraphViews
				if (views.getChildNodes().getLength()==0)
				{
					root.removeChild(views);
				}
			}
			
			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(CONFIG_FILE);
			transformer.transform(source, result);
			
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		   } catch (TransformerException tfe) {
			tfe.printStackTrace();
		   } catch (IOException ioe) {
			ioe.printStackTrace();
		   } catch (SAXException sae) {
			sae.printStackTrace();
		   }
	}
	
	private void addStandardIcons()
	{
		HashMap<MyNodeType,Icon> iconmapping = new HashMap<MyNodeType, Icon>();
		
		if (!CONFIG_FILE.exists())
			writeNewConfig(iconmapping);
		else
		{
			if (!iconConfigExists())
			{
				addBasicIconInfo();
				
				updateIcons(iconmapping);
			}
		}
	}

	
}
