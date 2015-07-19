package viewplugin.logic;

import graph.listener.ConfigWriterReader;
import graph.model.MyEdgeType;
import graph.model.MyNodeType;
import graph.operations.AttributeFilter;
import graph.operations.AttributeOperations;
import graph.operations.GraphViewContainer;
import graph.operations.MasterFilter;
import gui.GraphView;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import model.ModelBuilder;

import org.apache.commons.io.FileUtils;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

/**
 * 
 * @author deniz
 *
 *         Manages the created ProjectViews. Functionality for Import & Export
 *         of ViewConfigs
 * 
 */
public class ViewManager {

	static GraphView graphView;
	static MasterFilter masterFilter;

	private static final String CONFIG = "config.xml";
	private static File CONFIG_FILE;

	private static int doubleNameCounter = 0;

	/**
	 * Map with all ProjectViews and their Names
	 */
	private static TreeMap<String, ProjectView> projectViews = new TreeMap<String, ProjectView>();

	/**
	 * Tell the ViewManager which GraphView & MasterFilter to use
	 */
	public static void initViewManager(GraphView gv, MasterFilter mf) {
		graphView = gv;
		masterFilter = mf;
		printPluginRelatedInformation("Added GraphView and MasterFilter to ViewManager.");

		/* Import Config */
		CONFIG_FILE = ConfigWriterReader.getCONFIG_FILE();
		importProjectViewsFromConfig(CONFIG_FILE, true);
		printPluginRelatedInformation("Imported Project Views from "
				+ CONFIG_FILE.getAbsolutePath() + ".");
}

	/**
	 * Create a new ProjectView with Attribute Filters
	 */
	public static ProjectView createNewProjectView(String name,
			GraphViewContainer graphViewContainer,
			TreeMap<String, String[]> nodeAttributeFilterConditions,
			TreeMap<String, String[]> edgeAttributeFilterConditions) {
		if (nodeAttributeFilterConditions == null) {
			nodeAttributeFilterConditions = new TreeMap<String, String[]>();
		}
		if (edgeAttributeFilterConditions == null) {
			edgeAttributeFilterConditions = new TreeMap<String, String[]>();
		}
		ProjectView pv;
		if (projectViews.containsKey(name)) {
			pv = projectViews.get(name);
			pv.setGraphViewContainer(graphViewContainer);
			pv.setNodeAttributeFilterConditions(nodeAttributeFilterConditions);
			pv.setEdgeAttributeFilterConditions(edgeAttributeFilterConditions);
		} else {
			pv = new ProjectView(name, graphViewContainer,
					nodeAttributeFilterConditions,
					edgeAttributeFilterConditions);
			projectViews.put(name, pv);
		}
		// graphView.getGraph().createNewGraphView(graphViewContainer);
		pv.setActivated(false);
		// writeProjectViewsToConfig();
		printPluginRelatedInformation("Created new ProjectView " + name);
		return pv;
	}

	/**
	 * Create a new ProjectView without Attribute Filters
	 */
	public static ProjectView createNewProjectView(String name,
			GraphViewContainer graphViewContainer) {
		ProjectView pv;
		if (projectViews.containsKey(name)) {
			pv = projectViews.get(name);
			pv.setGraphViewContainer(graphViewContainer);
		} else {
			pv = new ProjectView(name, graphViewContainer);
			projectViews.put(name, pv);
		}
		// graphView.getGraph().createNewGraphView(graphViewContainer);
		pv.setActivated(false);
		writeProjectViewsToConfig();
		printPluginRelatedInformation("Created new ProjectView " + name);
		return pv;
	}

	/**
	 * Applies a Project View
	 * 
	 * @param name
	 *            - the Project View's Name
	 */
	public static void applyProjectView(String name) {
		ProjectView projectView = projectViews.get(name);
		printPluginRelatedInformation("Applying Project View " + name + "....");
		/* Undo all applied Filters */
		HashMap<String, GraphViewContainer> x = graphView.getGraph()
				.getAllGraphViews();
		for (String s : x.keySet()) {
			// printPluginRelatedInformation("Disabled ProjectView " + s);
			masterFilter.undoNodeAndEdgeTypeFilter(s);
			try {
				ProjectView tmp = ViewManager.getProjectViews().get(s);
				tmp.setActivated(false);
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
		}

		/* Set this one activated */
		projectView.setActivated(true);

		/* Node & Edge Types */
		GraphViewContainer container = projectView.getGraphViewContainer();
		// graphView.getGraph().createNewGraphView(container);
		masterFilter.addNodeAndEdgeTypeFilter(container.getSelectedNodeTypes(),
				container.getSelectedEdgeTypes(), container.getViewName(),
				false);
		masterFilter.applyNodeAndEdgeTypeFilter(container.getViewName());
		printPluginRelatedInformation("\tNode & Edge Type Filters successfully applied.");

		/* Node Attribute Filter */
		TreeMap<String, String[]> nodeAttributeConditions = projectView
				.getNodeAttributeFilterConditions();
		if (nodeAttributeConditions != null
				&& !nodeAttributeConditions.isEmpty()) {
			for (String condition : nodeAttributeConditions.keySet()) {
				try {

					String[] params = nodeAttributeConditions.get(condition);
					AttributeOperations op = AttributeOperations
							.getValueOf(params[1]);
					AttributeFilter.filterNode(params[0], op, params[2]);
					AttributeFilter.addNodeCondition(params[0], op, params[2]);
					masterFilter.addNodeAttributFilter(condition, true);
					printPluginRelatedInformation("\tNode Attribute Condition "
							+ condition + " successfully applied.");
				} catch (NullPointerException e) {
					printPluginRelatedInformation("\tProject View " + name
							+ " has no Node Attribute Filters.");
				} catch (Exception e) {
					printPluginRelatedInformation("\tNode Attribute Condition "
							+ condition + " could not be applied.");
				}
			}
		}

		/* Edge Attribute Filter */
		TreeMap<String, String[]> edgeAttributeConditions = projectView
				.getEdgeAttributeFilterConditions();
		if (edgeAttributeConditions != null
				&& !edgeAttributeConditions.isEmpty()) {
			for (String condition : edgeAttributeConditions.keySet()) {
				try {
					String[] params = edgeAttributeConditions.get(condition);
					AttributeOperations op = AttributeOperations
							.getValueOf(params[1]);
					AttributeFilter.filterEdge(params[0], op, params[2]);
					AttributeFilter.addEdgeCondition(params[0], op, params[2]);
					masterFilter.addEdgeAttributFilter(condition, true);
					printPluginRelatedInformation("\tEdge Attribute Condition "
							+ condition + " successfully applied.");
					AttributeFilter.applyEdgeCondition(condition);
					// masterFilter.applyEdgeAttributeFilter(condition);
				} catch (NullPointerException e) {
					printPluginRelatedInformation("\tProject View " + name
							+ " has no Edge Attribute Filters.");
				} catch (Exception e) {
					printPluginRelatedInformation("\tEdge Attribute Condition "
							+ condition + " could not be applied.");
				}
			}
		}

		printPluginRelatedInformation("....Project View "
				+ projectView.getName() + " successfully applied.");
	}

	/**
	 * Undo a specific Project View
	 * 
	 * @param name
	 *            - the Project View's Name
	 */
	public static void undoProjectView(String name) {
		printPluginRelatedInformation("Deactivating Project View " + name
				+ "....");

		/* Undo Node/Edge Type Filters */
		if (masterFilter.NodeAndEdgeTypeFilterActive(name)) {
			masterFilter.undoNodeAndEdgeTypeFilter(name);
			printPluginRelatedInformation("\tNode & Edge Type Filters successfully deactivated.");
		}

		/* Undo Attribute Filters */
		ProjectView pv = getProjectViews().get(name);
		TreeMap<String, String[]> nodeConditions = pv
				.getNodeAttributeFilterConditions();
		TreeMap<String, String[]> edgeConditions = pv
				.getEdgeAttributeFilterConditions();
		if (nodeConditions != null) {
			for (String c : nodeConditions.keySet()) {
				try {
					masterFilter.undoNodeAttributeFilter(c);
					// AttributeFilter.undoNodeCondition(c);
					printPluginRelatedInformation("\tNode Attribute Condition "
							+ c + " successfully deactivated.");
				} catch (Exception e) {
					printPluginRelatedInformation("\tNode Attribute Condition "
							+ c + " could not be deactivated.");
				}
			}
		}
		if (edgeConditions != null) {
			for (String c : edgeConditions.keySet()) {
				try {
					masterFilter.undoEdgeAttributeFilter(c);
					// AttributeFilter.undoNodeCondition(c);
					printPluginRelatedInformation("\tEdge Attribute Condition "
							+ c + " successfully deactivated.");
				} catch (Exception e) {
					printPluginRelatedInformation("\tEdge Attribute Condition "
							+ c + " could not be deactivated.");
				}
			}
		}
		/* Set deactivated */
		ViewManager.getProjectViews().get(name).setActivated(false);
		printPluginRelatedInformation("....Successfully deactivated Project View "
				+ name + ".");
	}

	/**
	 * Delete ProjectView
	 */
	public static void deleteProjectView(String name) {
		ProjectView pv = projectViews.get(name);
		masterFilter.undoNodeAndEdgeTypeFilter(name);
		graphView.getGraph().deleteGraphView(pv.getGraphViewContainer());
		projectViews.remove(name);
		ViewManager
				.printPluginRelatedInformation("Deleted ProjectView " + name);
	}

	/**
	 * Delete ALL ProjectViews
	 */
	public static void deleteAllProjectViews() {
		for (String name : projectViews.keySet()) {
			deleteProjectView(name);
		}
		projectViews.clear();
	}

	/**
	 * Stores ProjectView Data in config.xml
	 */
	public static void writeProjectViewsToConfig() {
		try {
			// File copyConfig = new File("config2.xml");
			File config = CONFIG_FILE;
			// FileUtils.copyFile(CONFIG_FILE, CONFIG_FILE_2);

			DocumentBuilderFactory docFactory = DocumentBuilderFactory
					.newInstance();
			Document doc;
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			doc = docBuilder.parse(config);
			doc.getDocumentElement().normalize();

			NodeList graphViews = doc.getElementsByTagName("graphView");
			/*
			 * Iterate over all GraphViews * Node/Edge Filters already exist *
			 * Here the Attribute Filters are added to the CONFIG File.
			 */
			for (int i = 0; i < graphViews.getLength(); i++) {
				Element graphViewNode = (Element) graphViews.item(i);
				String name = graphViewNode.getAttributes().item(0)
						.getTextContent(); // Name of the i-th GraphView
				ProjectView currentProjectView = projectViews.get(name);
				boolean alreadyCreated = false;
				NodeList childNodes = graphViewNode.getChildNodes();
				for (int j = 0; j < childNodes.getLength(); j++) {
					Node childNode = childNodes.item(j);
					if (childNode.getNodeName().equals("attributeFilters")) {
						alreadyCreated = true;
					}
				}
				Element attributeFilters;
				if (alreadyCreated) {
					attributeFilters = (Element) graphViewNode
							.getElementsByTagName("attributeFilters").item(0);
				} else {
					attributeFilters = doc.createElement("attributeFilters");
					graphViewNode.appendChild(attributeFilters);
				}

				/* Iterate over the current Project View's NodeAttributeFilters */
				TreeMap<String, String[]> nodeFilters = currentProjectView
						.getNodeAttributeFilterConditions();
				for (String condition : nodeFilters.keySet()) {
					String[] array = nodeFilters.get(condition);
					Element newAttributeFilter = doc
							.createElement("attributeFilter");
					newAttributeFilter.setAttribute("type", "node");
					newAttributeFilter.setAttribute("attributeName", array[0]);
					newAttributeFilter.setAttribute("operation", array[1]);
					newAttributeFilter.setAttribute("value", array[2]);
					attributeFilters.appendChild(newAttributeFilter);
				}

				/* Iterate over the current Project View's EdgeAttributeFilters */
				TreeMap<String, String[]> edgeFilters = currentProjectView
						.getEdgeAttributeFilterConditions();
				if (edgeFilters != null) {
					for (String condition : edgeFilters.keySet()) {
						String[] array = edgeFilters.get(condition);
					}
				}

			}

			/* Write to File */
			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(config);
			transformer.transform(source, result);
			printPluginRelatedInformation("Updated Config File "
					+ config.getAbsolutePath());

		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Automatic Import from Config File
	 */
	private static void importProjectViewsFromConfig(File importFile,
			boolean isStandardConfig) {
		if (isStandardConfig) {
			deleteAllProjectViews();
		}

		// File config = CONFIG_FILE;
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory
					.newInstance();
			Document doc;
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			doc = docBuilder.parse(importFile);
			doc.getDocumentElement().normalize();

			NodeList projectViewNodes = doc.getElementsByTagName("graphView");

			LinkedList<MyNodeType> selectedNodes = new LinkedList<MyNodeType>();
			LinkedList<MyEdgeType> selectedEdges = new LinkedList<MyEdgeType>();
			TreeMap<String, String[]> nodeAttributeFilters = new TreeMap<String, String[]>();
			TreeMap<String, String[]> edgeAttributeFilters = new TreeMap<String, String[]>();

			/* Iterate through ProjectView Nodes */
			for (int i = 0; i < projectViewNodes.getLength(); i++) {
				Element projectViewNode = (Element) projectViewNodes.item(i);
				String name = projectViewNode.getAttributes().item(0)
						.getTextContent();

				/* Nodes */
				NodeList activeNodes = projectViewNode
						.getElementsByTagName("visibleNodeType");
				for (int j = 0; j < activeNodes.getLength(); j++) {
					Node currentNodeType = activeNodes.item(j);
					MyNodeType nodeType = ModelBuilder.getNodeTypes().getValue(
							currentNodeType.getTextContent());
					selectedNodes.add(nodeType);
				}

				/* Edges */
				NodeList activeEdges = projectViewNode
						.getElementsByTagName("visibleEdgeType");
				for (int j = 0; j < activeEdges.getLength(); j++) {
					Node currentEdgeType = activeEdges.item(j);
					MyEdgeType edgeType = ModelBuilder.getEdgeTypes().getValue(
							currentEdgeType.getTextContent());
					selectedEdges.add(edgeType);
				}

				/* Attribute Filters */
				NodeList attributeFilters = projectViewNode
						.getElementsByTagName("attributeFilter");
				for (int j = 0; j < attributeFilters.getLength(); j++) {
					Element attributeFilter = (Element) attributeFilters
							.item(j);
					String type = attributeFilter.getAttribute("type");
					String attributeName = attributeFilter
							.getAttribute("attributeName");
					String operation = attributeFilter
							.getAttribute("operation");
					String value = attributeFilter.getAttribute("value");
					String key = attributeName + " " + operation + " " + value;
					String[] params = new String[3];
					params[0] = attributeName;
					params[1] = operation;
					params[2] = value;
					if (type.equals("node")) {
						nodeAttributeFilters.put(key, params);
					} else {
						edgeAttributeFilters.put(key, params);
					}
				}

				/* Create ProjectView from Config Information */
				GraphViewContainer container = graphView.getGraph()
						.getAllGraphViews().get(name);
				if (container == null) {
					container = new GraphViewContainer(selectedNodes,
							selectedEdges, name);
				}
				ProjectView currentProjectView = createNewProjectView(name,
						container, nodeAttributeFilters, edgeAttributeFilters);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Manually import ProjectView Stores the imported ProjectView in the Config
	 * file
	 */
	public static void importExternalViewConfig(File importFile) {
		importProjectViewsFromConfig(importFile, false);
		printPluginRelatedInformation("Successfully imported Project View Config " + importFile.getAbsolutePath());
		writeProjectViewsToConfig();
	}

	/**
	 * Export ProjectViews to specific location
	 */
	public static void exportSingleProjectViewConfig(String projectViewName,
			String fileDir, String fileName) {
		if (!fileName.endsWith(".xml")) {
			fileName += ".xml";
		}
		File exportFile = new File(fileDir + "\\" + fileName);

		try {
			/* Read config.xml */
			DocumentBuilderFactory docFactory = DocumentBuilderFactory
					.newInstance();
			Document doc;
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			doc = docBuilder.parse(CONFIG);
			doc.getDocumentElement().normalize();

			/* Find selected Project View Tag */
			Element searchedProjectView = null;
			NodeList projectViews = doc.getElementsByTagName("graphView");
			for (int i = 0; i < projectViews.getLength(); i++) {
				Node currentProjectView = projectViews.item(i);
				String name = currentProjectView.getAttributes().item(0)
						.getTextContent();
				if (name.equals(projectViewName)) {
					searchedProjectView = (Element) currentProjectView;
				}
			}

			/* Write Project View to export File */
			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(searchedProjectView);
			StreamResult result = new StreamResult(exportFile);
			transformer.transform(source, result);
			printPluginRelatedInformation("Exported Project View Config to "
					+ exportFile.getAbsolutePath());

		} catch (SAXException | IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 
	 * @return - returns the ProjectViews
	 */
	public static TreeMap<String, ProjectView> getProjectViews() {
		return projectViews;
	}

	public static void printPluginRelatedInformation(String text) {
		System.out.println("...ViewPlugin:\t" + text);
	}

}
