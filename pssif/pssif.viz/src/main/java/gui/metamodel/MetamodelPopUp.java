package gui.metamodel;

import gui.graph.WrapLayout;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import de.tum.pssif.core.common.PSSIFConstants;
import de.tum.pssif.core.metamodel.external.MetamodelAttribute;
import de.tum.pssif.core.metamodel.external.MetamodelComponent;
import de.tum.pssif.core.metamodel.external.MetamodelConjunction;
import de.tum.pssif.core.metamodel.external.MetamodelEdge;
import de.tum.pssif.core.metamodel.external.MetamodelExportRDF;
import de.tum.pssif.core.metamodel.external.MetamodelNode;
import de.tum.pssif.core.metamodel.external.PSSIFCanonicMetamodelCreator;

/**
 * Enable the user to view and manipulate the metamodel
 * 
 * @author Alex
 * 
 */
public class MetamodelPopUp {

	private JPanel allPanel;
	private JPanel conPanel;
	private JPanel edgePanel;
	private JPanel nodePanel;
	private JPanel attributePanel;
	private JPanel bottomPanel;
	private JLabel conLabel;
	private JLabel edgeLabel;
	private JLabel nodeLabel;
	private JLabel attributeLabel;
	private JLabel warningLabel;
	private JLabel restartLabel;
	private JScrollPane conScroller;
	private JScrollPane edgeScroller;
	private JScrollPane nodeScroller;
	private JScrollPane attributeScroller;
	private MetamodelExportRDF metamodelExport;
	private HashMap<String, MetamodelConjunction> conjunctions;
	private HashMap<String, MetamodelNode> nodes;
	private HashMap<String, MetamodelEdge> edges;
	private HashMap<String, MetamodelAttribute> attributes;
	private ArrayList<String> conjunctionComponentsAsString;
	private ArrayList<String> attributeComponentsAsString;
	private ArrayList<String> edgeComponentsAsString;
	private ArrayList<String> nodeComponentsAsString;
	private ArrayList<String> potentialNodesForMappings;
	ArrayList<String> allAttributesByName;
	private MetamodelPopUp thisClass;
	private Box hBox;
	private Box hBox1;
	private Box hBox2;
	private Icon deleteButtonIcon;
	private JButton addConjunctionButton;
	private JButton addNodeButton;
	private JButton addEdgeButton;
	private JButton addAttributeButton;

	/**
	 * Create the Panel(GUI) of the Popup
	 * 
	 * @return a panel with all the components
	 */
	private JPanel createPanel() {

		thisClass = this;
		allPanel = new JPanel();
		allPanel.setLayout(new BoxLayout(allPanel, BoxLayout.Y_AXIS));

		reloadData();

		// Path to Icon
		String path = System.getProperty("user.dir");
		String delete = path.substring(0, path.length() - 9)
				+ "button\\deletebutton.png";

		deleteButtonIcon = new javax.swing.ImageIcon(delete);
		((ImageIcon) deleteButtonIcon).setImage(((ImageIcon) deleteButtonIcon)
				.getImage().getScaledInstance(15, 15, Image.SCALE_DEFAULT));
		
		// //////////////////////////////////////////////////////////////////////////////////////////////////////
		// / Conjunctions
		// //////////////////////////////////////////////////////////////////////////////////////////////////////
		
		conScroller = new JScrollPane();
		conScroller.setMinimumSize(new Dimension(700, 50));
		conScroller.setMaximumSize(new Dimension(700, 100));
		conScroller.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		conLabel = new JLabel("Conjunctions");
		allPanel.add(conLabel);
		conLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

		conPanel = new JPanel();
		conPanel.setLayout(new WrapLayout());

		ArrayList<MetamodelComponent> sortedConjunctions = new ArrayList<MetamodelComponent>(); 
		for (MetamodelConjunction con : conjunctions.values()) {
			sortedConjunctions.add(con);
		}
		Collections.sort(sortedConjunctions, new MyComponentComparator());
		for (MetamodelComponent con : sortedConjunctions) {
			createComponentButton(con, conPanel);
		}

		addConjunctionButton = new JButton("+");
		addConjunctionButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				ComponentDetailPopup popup = new ComponentDetailPopup(
						new MetamodelConjunction("", ""), metamodelExport,
						conjunctionComponentsAsString, thisClass);
				popup.showComponentDetailView();
			}
		});
		addConjunctionButton.setPreferredSize(new Dimension(130, 20));
		conPanel.add(addConjunctionButton);

		conScroller.setViewportView(conPanel);
		allPanel.add(conScroller);

		// Space
		hBox = Box.createVerticalBox();
		hBox.add(Box.createRigidArea(new Dimension(700, 20)));
		allPanel.add(hBox);

		// //////////////////////////////////////////////////////////////////////////////////////////////////////
		// / EDGES
		// //////////////////////////////////////////////////////////////////////////////////////////////////////

		edgeScroller = new JScrollPane();
		edgeScroller.setMinimumSize(new Dimension(700, 50));
		edgeScroller.setMaximumSize(new Dimension(700, 350));
		edgeScroller.setAlignmentX(Component.CENTER_ALIGNMENT);

		edgeLabel = new JLabel("Edges", 0);
		allPanel.add(edgeLabel);
		edgeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

		edgePanel = new JPanel();
		edgePanel.setLayout(new WrapLayout());
		
		ArrayList<MetamodelComponent> sortedEdges = new ArrayList<MetamodelComponent>(); 
		for (MetamodelEdge edge : edges.values()) {
			sortedEdges.add(edge);
		}
		Collections.sort(sortedEdges, new MyComponentComparator());
		for (MetamodelComponent edge : sortedEdges) {
			createComponentButton(edge, edgePanel);
		}

		addEdgeButton = new JButton("+");
		addEdgeButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				ComponentDetailPopup popup = new ComponentDetailPopup(
						new MetamodelEdge("", ""), metamodelExport,
						edgeComponentsAsString, potentialNodesForMappings,
						thisClass);
				popup.showComponentDetailView();
			}
		});
		addEdgeButton.setPreferredSize(new Dimension(130, 20));
		edgePanel.add(addEdgeButton);

		edgeScroller.setViewportView(edgePanel);
		allPanel.add(edgeScroller);

		// Space
		hBox1 = Box.createVerticalBox();
		hBox1.add(Box.createRigidArea(new Dimension(0, 20)));
		allPanel.add(hBox1);

		// //////////////////////////////////////////////////////////////////////////////////////////////////////
		// / NODES
		// //////////////////////////////////////////////////////////////////////////////////////////////////////

		nodeScroller = new JScrollPane();
		nodeScroller.setMinimumSize(new Dimension(700, 50));
		nodeScroller.setMaximumSize(new Dimension(700, 350));
		nodeScroller.setAlignmentX(Component.CENTER_ALIGNMENT);

		nodeLabel = new JLabel("Nodes", 0);
		allPanel.add(nodeLabel);
		nodeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		nodePanel = new JPanel();
		nodePanel.setLayout(new WrapLayout());

		ArrayList<MetamodelComponent> sortedNodes = new ArrayList<MetamodelComponent>(); 
		for (MetamodelNode node : nodes.values()) {
			sortedNodes.add(node);
		}
		Collections.sort(sortedNodes, new MyComponentComparator());
		for (MetamodelComponent node : sortedNodes) {
			createComponentButton(node, nodePanel);
		}

		addNodeButton = new JButton("+");
		addNodeButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				ComponentDetailPopup popup = new ComponentDetailPopup(
						new MetamodelNode("", ""), metamodelExport,
						nodeComponentsAsString, attributes, thisClass);
				popup.showComponentDetailView();
			}
		});
		addNodeButton.setPreferredSize(new Dimension(130, 20));
		nodePanel.add(addNodeButton);

		nodeScroller.setViewportView(nodePanel);
		allPanel.add(nodeScroller);

		// Space
		hBox2 = Box.createVerticalBox();
		hBox2.add(Box.createRigidArea(new Dimension(0, 20)));
		allPanel.add(hBox2);

		// //////////////////////////////////////////////////////////////////////////////////////////////////////
		// / ATTRIBUTES
		// //////////////////////////////////////////////////////////////////////////////////////////////////////

		attributeScroller = new JScrollPane();
		attributeScroller.setMinimumSize(new Dimension(700, 50));
		attributeScroller.setMaximumSize(new Dimension(700, 250));
		attributeScroller.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		attributeLabel = new JLabel("Attributes", 0);
		allPanel.add(attributeLabel);
		attributeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		attributePanel = new JPanel();
		attributePanel.setLayout(new WrapLayout());

				
		ArrayList<MetamodelComponent> sortedAttributes = new ArrayList<MetamodelComponent>();
		for (MetamodelAttribute att : attributes.values()) {
			sortedAttributes.add(att);
		}
		Collections.sort(sortedAttributes, new MyComponentComparator());
		for (MetamodelComponent attribute : sortedAttributes) {
			createComponentButton(attribute, attributePanel);
		}

		addAttributeButton = new JButton("+");
		addAttributeButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				ComponentDetailPopup popup = new ComponentDetailPopup(
						new MetamodelAttribute("", "", "", "", true, "", ""),
						metamodelExport, attributeComponentsAsString, thisClass);
				popup.showComponentDetailView();
			}
		});
		addAttributeButton.setPreferredSize(new Dimension(130, 20));
		attributePanel.add(addAttributeButton);

		attributeScroller.setViewportView(attributePanel);
		allPanel.add(attributeScroller);

		// //////////////////////////////////////////////////////////////////////////////////////////////////////
		// / BOTTOM
		// //////////////////////////////////////////////////////////////////////////////////////////////////////

		// Space box
		bottomPanel = new JPanel();
		bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.PAGE_AXIS));
		bottomPanel.setMinimumSize(new Dimension(700, 50));
		bottomPanel.setMaximumSize(new Dimension(700, 50));
		bottomPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		allPanel.add(bottomPanel);

		warningLabel = new JLabel(
				"Warning: Modifing the metamodel may result in unexpected behavior");
		warningLabel.setForeground(Color.RED);
		warningLabel.setPreferredSize(new Dimension(700, 15));
		warningLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		warningLabel.setAlignmentY(Component.BOTTOM_ALIGNMENT);
		
		bottomPanel.add(warningLabel);
		
		restartLabel = new JLabel("Changes require a restart of the programm");
		restartLabel.setMinimumSize(new Dimension(700,15));
		restartLabel.setForeground(Color.RED);
		restartLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		restartLabel.setAlignmentY(Component.BOTTOM_ALIGNMENT);
		
		bottomPanel.add(restartLabel);

		allPanel.setPreferredSize(new Dimension(750, 600));
		allPanel.setMaximumSize(new Dimension(750, 600));
		allPanel.setMinimumSize(new Dimension(750, 600));

		return allPanel;
	}

	/**
	 * Display the Popup to the user
	 */
	public void showMetamodelView() {
		JOptionPane.showConfirmDialog(null, createPanel(),
				"Metamodel Overview", JOptionPane.DEFAULT_OPTION);
	}

	/**
	 * Create a button panel including a delete functionality
	 * 
	 * @param component
	 *            Component to add as a button
	 * @param componentPanel
	 *            Panel to add the button to
	 */
	private void createComponentButton(final MetamodelComponent component,
			final JPanel componentPanel) {

		final JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER,
				0, 0));

		final JButton newButton = new JButton(component.getName());
		newButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
			
						ComponentDetailPopup popup;
						if (component.getType().equals("EDGE")) {
							popup = new ComponentDetailPopup(
									edges.get(((JButton) arg0.getSource()).getText()), metamodelExport,
									edgeComponentsAsString,
									potentialNodesForMappings, thisClass);
						} else if (component.getType().equals("NODE")) {
							popup = new ComponentDetailPopup(
									nodes.get(((JButton) arg0.getSource()).getText()), metamodelExport,
									nodeComponentsAsString, attributes,
									thisClass);
						} else if (component.getType().equals("CONJUNCTION")) {
							popup = new ComponentDetailPopup(
									conjunctions.get(((JButton) arg0.getSource()).getText()),
									metamodelExport,
									conjunctionComponentsAsString, thisClass);
						} else {
							popup = new ComponentDetailPopup(
									attributes.get(((JButton) arg0.getSource()).getText()),
									metamodelExport,
									attributeComponentsAsString, thisClass);
						}

						popup.showComponentDetailView();
					}			
		});
		newButton.setPreferredSize(new Dimension(130, 20));
		buttonPanel.add(newButton);

		final JButton deleteButton = new JButton(deleteButtonIcon);

		deleteButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {

				int selected = -1;

				// Checked in order to ensure the user is only asked what he
				// want's to do with the components children if any
				boolean hasChildren = false;
				if (component.getType().equals("NODE")) {
					for (MetamodelNode currentNode : nodes.values()) {
						if (currentNode.getParent() != null
								&& currentNode.getParent().equals(component)) {
							hasChildren = true;
							break;
						}
					}
				} else {
					for (MetamodelEdge currentEdge : edges.values()) {
						if (currentEdge.getParent() != null
								&& currentEdge.getParent().equals(component)) {
							hasChildren = true;
							break;
						}
					}
				}

				if (hasChildren
						&& (component.getType().equals("NODE") || component
								.getType().equals("EDGE"))) {
					// OptionsPane to decide what to do with interdependencies
					// of deleted components

					String[] options = {
							"Set parent of children to parent of this component",
							"Remove this component as a parent of its children",
							"Abort" };
					selected = JOptionPane
							.showOptionDialog(
									null,
									"Decide what to do with potential interdependencies",
									"Interdependencies",
									JOptionPane.DEFAULT_OPTION,
									JOptionPane.INFORMATION_MESSAGE, null,
									options, options[2]);
				} else {
					selected = 3;
				}
				metamodelExport.removeComponent(component, selected);

				if (selected != 2 && selected != -1) {
					reloadData();
					componentPanel.remove(buttonPanel);
					componentPanel.revalidate();
					componentPanel.repaint();
				}
			}
		});
		deleteButton.setPreferredSize(new Dimension(20, 20));
		buttonPanel.add(deleteButton);

		componentPanel.add(buttonPanel);
	}

	/**
	 * Reset the current view and resources entirely
	 */
	public void resetView() {
		Window w = SwingUtilities.getWindowAncestor(allPanel);
		w.dispose();
		JOptionPane.showConfirmDialog(null, createPanel(),
				"Metamodel Overview", JOptionPane.DEFAULT_OPTION);
	}

	/**
	 * Used for initially setting up the data or reload in later in order to
	 * prevent data inconsistencies
	 */
	private void reloadData() {
		PSSIFCanonicMetamodelCreator.loadXMLData();
		metamodelExport = new MetamodelExportRDF();

		// Get and sort all components of each type category
		conjunctions = PSSIFCanonicMetamodelCreator.conjunctions;
		nodes = PSSIFCanonicMetamodelCreator.nodes;
		edges = PSSIFCanonicMetamodelCreator.edges;
		attributes = PSSIFCanonicMetamodelCreator.attributes;

		// Conjunction data
		conjunctionComponentsAsString = new ArrayList<String>();
		for (MetamodelConjunction con : conjunctions.values()) {
			conjunctionComponentsAsString.add(con.getName());
		}
		Collections.sort(conjunctionComponentsAsString);

		// Edge data
		// Get possible mapping Clients
		potentialNodesForMappings = new ArrayList<String>();
		for (MetamodelNode node : nodes.values()) {
			potentialNodesForMappings.add(node.getName());
		}
		potentialNodesForMappings.add(PSSIFConstants.ROOT_NODE_TYPE_NAME);
		Collections.sort(potentialNodesForMappings);

		edgeComponentsAsString = new ArrayList<String>();
		for (MetamodelEdge edge : edges.values()) {
			edgeComponentsAsString.add(edge.getName());
		}
		edgeComponentsAsString.add("");
		Collections.sort(edgeComponentsAsString);

		// Node data
		nodeComponentsAsString = new ArrayList<String>();
		for (MetamodelNode node : nodes.values()) {
			nodeComponentsAsString.add(node.getName());
		}
		nodeComponentsAsString.add("");
		Collections.sort(nodeComponentsAsString);

		// Attribute data
		attributeComponentsAsString = new ArrayList<String>();
		for (MetamodelAttribute attribute : attributes.values()) {
			attributeComponentsAsString.add(attribute.getName());
		}
		Collections.sort(attributeComponentsAsString);

	}
}

/**
 * Comparator to compare components due to their names
 * 
 * @author Alex
 * 
 */
class MyComponentComparator implements Comparator<MetamodelComponent> {

	@Override
	public int compare(MetamodelComponent comp1, MetamodelComponent comp2) {
		return comp1.getName().compareToIgnoreCase(comp2.getName());
	}
}
