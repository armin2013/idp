package gui.metamodel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import de.tum.pssif.core.metamodel.external.MetamodelAttribute;
import de.tum.pssif.core.metamodel.external.MetamodelComponent;
import de.tum.pssif.core.metamodel.external.MetamodelConjunction;
import de.tum.pssif.core.metamodel.external.MetamodelEdge;
import de.tum.pssif.core.metamodel.external.MetamodelExportRDF;
import de.tum.pssif.core.metamodel.external.MetamodelNode;
import de.tum.pssif.core.metamodel.external.PSSIFCanonicMetamodelCreator;
import de.tum.pssif.core.model.Tupel;

/**
 * Enables the user to see all details of the components in detail
 * 
 * @author Alex
 * 
 */
public class ComponentDetailPopup {

	private JPanel allPanel;
	private JTextField tagfield;
	private JTextField namefield;
	private JComboBox<String> parentbox;
	private JLabel tag;
	private JLabel name;
	private JLabel parent;
	private JButton saveButton;
	private GridBagConstraints cScrollPane;
	private GridBagConstraints cLabel;
	private GridBagConstraints cField;
	private GridBagConstraints cButton;
	private String originalTag = "";
	private String originalName = "";
	private MetamodelExportRDF metamodelExport;
	private HashMap<String, MetamodelAttribute> potentialAttributes;
	private ArrayList<String> nodeComponentsAsString;
	private ArrayList<String> edgeComponentsAsString;
	private ArrayList<String> conjunctionComponentsAsString;
	private ArrayList<String> attributeComponentsAsString;
	private ArrayList<String> potentialNodesForMappings;
	private ArrayList<String> potentialParents;
	private MetamodelConjunction conjunction;
	private MetamodelNode node;
	private MetamodelEdge edge;
	private MetamodelAttribute attribute;
	private MetamodelPopUp metamodelOverview;
	private NodeSpecificPopUp nodePopUp;
	private EdgeSpecificPopUp edgePopUp;
	private AttributeSpecificPopUp attributePopUp;

	/**
	 * Constructor for conjunctions
	 * 
	 * @param component
	 *            The component to display
	 * @param metamodelExport
	 *            Export class to save changes with
	 * @param conjunctionComponentsAsString
	 *            A list of existing components of this type
	 * @param metamodelOverview
	 *            A reference to the overview class
	 */
	public ComponentDetailPopup(MetamodelConjunction conjunction,
			MetamodelExportRDF metamodelExport,
			ArrayList<String> conjunctionComponentsAsString,
			MetamodelPopUp metamodelOverview) {

		this.conjunctionComponentsAsString = conjunctionComponentsAsString;
		this.metamodelExport = metamodelExport;
		this.originalName = conjunction.getName();
		this.originalTag = conjunction.getTag();
		this.conjunction = conjunction;
		this.metamodelOverview = metamodelOverview;
	}

	/**
	 * Constructor for nodes
	 * 
	 * @param component
	 *            The component to display
	 * @param metamodelExport
	 *            Export class to save changes with
	 * @param typeComponentsAsString
	 *            A list of existing components of this type
	 * @param panelContent
	 *            Further resources like attributes
	 * @param metamodelOverview
	 *            A reference to the overview class
	 */
	public ComponentDetailPopup(MetamodelNode node,
			MetamodelExportRDF metamodelExport, ArrayList<String> existingNodes,
			HashMap<String, MetamodelAttribute> potentialAttributes,
			MetamodelPopUp metamodelOverview) {
		this.node = node;
		this.metamodelExport = metamodelExport;
		this.metamodelOverview = metamodelOverview;
		this.potentialParents = existingNodes;
		this.originalName = node.getName();
		this.originalTag = node.getTag();
		this.nodeComponentsAsString = existingNodes;
		this.potentialAttributes = potentialAttributes;
	}

	/**
	 * Constructor for edges
	 * 
	 * @param component
	 *            The component to display
	 * @param metamodelExport
	 *            Export class to save changes with
	 * @param typeComponentsAsString
	 *            A list of existing components of this type
	 * @param panelContent
	 *            Further resources like potential mapping components
	 * @param metamodelOverview
	 *            A reference to the overview class
	 */
	public ComponentDetailPopup(MetamodelEdge edge,
			MetamodelExportRDF metamodelExport, ArrayList<String> existingEdges,
			ArrayList<String> potentialMappingComponents,
			MetamodelPopUp metamodelOverview) {
		this.edge = edge;
		this.metamodelExport = metamodelExport;
		this.metamodelOverview = metamodelOverview;
		this.potentialParents = existingEdges;
		this.originalName = edge.getName();
		this.originalTag = edge.getTag();

		this.edgeComponentsAsString = existingEdges;
		this.potentialNodesForMappings = potentialMappingComponents;

	}

	/**
	 * Constructor for attributes
	 * 
	 * @param component
	 *            The component to display
	 * @param metamodelExport
	 *            Export class to save changes with
	 * @param typeComponentsAsString
	 *            A list of existing components of this type
	 * @param metamodelOverview
	 *            A reference to the overview class
	 */
	public ComponentDetailPopup(MetamodelAttribute attribute,
			MetamodelExportRDF metamodelExport,
			ArrayList<String> existingComponents,
			MetamodelPopUp metamodelOverview) {

		this.attributeComponentsAsString = existingComponents;
		this.metamodelExport = metamodelExport;
		this.originalName = attribute.getName();
		this.originalTag = attribute.getTag();
		this.attribute = attribute;
		this.metamodelOverview = metamodelOverview;
	}

	/**
	 * Create the Panel(GUI) of the Popup
	 * 
	 * @return a panel with all the components
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private JPanel createPanel() {
		allPanel = new JPanel();
		allPanel.setLayout(new GridBagLayout());

		cScrollPane = new GridBagConstraints();
		cScrollPane.anchor = GridBagConstraints.NORTHWEST;
		cScrollPane.gridx = 0;
		cScrollPane.gridy = 6;
		cScrollPane.fill = GridBagConstraints.HORIZONTAL;
		cScrollPane.ipady = 300;
		cScrollPane.ipadx = 320;
		cScrollPane.weightx = 0;
		cScrollPane.weighty = 0;
		cScrollPane.gridwidth = 4;

		// /////////////////////////////////////////////////////////////////////////////////////////////////////
		// /// COMMON
		// /////////////////////////////////////////////////////////////////////////////////////////////////////

		// Constraint for every Label
		cLabel = new GridBagConstraints();
		cLabel.ipadx = 50;
		cLabel.gridwidth = 1;
		cLabel.anchor = GridBagConstraints.WEST;

		// Constraint for every TextField
		cField = new GridBagConstraints();
		cField.ipadx = 150;
		cField.gridwidth = 2;
		cField.fill = GridBagConstraints.HORIZONTAL;
		cField.anchor = GridBagConstraints.WEST;

		// Constraint for every Button
		cButton = new GridBagConstraints();
		cButton.ipadx = 15;
		cButton.ipady = 5;

		tag = new JLabel("Tag:");
		cLabel.gridx = 0;
		cLabel.gridy = 0;
		allPanel.add(tag, cLabel);

		tagfield = new JTextField((conjunction != null) ? conjunction.getTag()
				: (node != null) ? node.getTag()
						: (edge != null) ? edge.getTag() : attribute.getTag());
		cField.gridx = 1;
		cField.gridy = 0;
		allPanel.add(tagfield, cField);
		tagfield.getDocument().addDocumentListener(new MyDocumentListener());
		if (tagfield.getText().isEmpty()) {
			tagfield.setBorder(BorderFactory.createLineBorder(Color.RED));
		}

		name = new JLabel("Name:");
		cLabel.gridx = 0;
		cLabel.gridy = 1;
		allPanel.add(name, cLabel);

		namefield = new JTextField(
				(conjunction != null) ? conjunction.getName()
						: (node != null) ? node.getName()
								: (edge != null) ? edge.getName()
										: attribute.getName());
		cField.gridx = 1;
		cField.gridy = 1;
		allPanel.add(namefield, cField);
		namefield.getDocument().addDocumentListener(new MyDocumentListener());
		if (namefield.getText().isEmpty()) {
			namefield.setBorder(BorderFactory.createLineBorder(Color.RED));
		}

		// /////////////////////////////////////////////////////////////////////////////////////////////////////
		// /// Specific
		// /////////////////////////////////////////////////////////////////////////////////////////////////////

		// Set parent
		if (node != null || edge != null) {
			parent = new JLabel("Parent:");
			cLabel.gridx = 0;
			cLabel.gridy = 2;
			allPanel.add(parent, cLabel);

			parentbox = new JComboBox(
					potentialParents.toArray(new String[potentialParents.size()]));
			cField.gridx = 1;
			cField.gridy = 2;
			allPanel.add(parentbox, cField);

			if (node != null) {

				if (node.getParent() != null
						&& !node.getParent().getName().equals("")) {
					parentbox.setSelectedItem(node.getParent().getName());
				} else {
					parentbox.setSelectedItem("");
				}
			} else if (edge != null) {
				if (edge.getParent() != null
						&& !edge.getParent().getName().equals("")) {
					parentbox.setSelectedItem(edge.getParent().getName());
				} else {
					parentbox.setSelectedItem("");
				}
			}
		}

		// Path to Icon
		String path = System.getProperty("user.dir");
		String save = path.substring(0, path.length() - 9)
				+ "button\\savebutton.png";

		Icon icon = new javax.swing.ImageIcon(save);
		((ImageIcon) icon).setImage(((ImageIcon) icon).getImage()
				.getScaledInstance(25, 15, Image.SCALE_DEFAULT));
		saveButton = new JButton(icon);

		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {

				MetamodelComponent componentToSave = null;

				if (conjunction != null) {
					componentToSave = new MetamodelConjunction(tagfield
							.getText(), namefield.getText());
				}
				if (node != null) {
					MetamodelNode nodeToSave = node;

					nodeToSave.setConnectedAttributes(nodePopUp
							.getCurrentAttributeNames());

					if (!parentbox.getSelectedItem().equals("")) {
						nodeToSave.setParent(PSSIFCanonicMetamodelCreator.nodes.get((String) parentbox.getSelectedItem()));
					}
					componentToSave = nodeToSave;

				} else if (edge != null) {
					MetamodelEdge edgeToSave = edge;

					ArrayList<String> currentSetMappings = edgePopUp
							.getCurrentMappings();
					ArrayList<Tupel> convertedMappings = new ArrayList<Tupel>();

					if (currentSetMappings != null) {
						for (String connectedMapping : currentSetMappings) {
							String[] parts = connectedMapping.split(";");
							convertedMappings
									.add(new Tupel(parts[0], parts[1]));
						}

						edgeToSave.setMappings(convertedMappings);
					}

					if (!parentbox.getSelectedItem().equals("")) {
						edgeToSave.setParent(PSSIFCanonicMetamodelCreator.edges.get((String) parentbox.getSelectedItem())); 
					}

					componentToSave = edgeToSave;
				} else if (attribute != null) {
					componentToSave = attributePopUp.getCurrentSetAttribute();
				}

				componentToSave.setTag(tagfield.getText());
				componentToSave.setName(namefield.getText());

				metamodelExport.saveChangedComponent(originalName,
						componentToSave);

				Window w = SwingUtilities.getWindowAncestor(allPanel);
				w.dispose();
				metamodelOverview.resetView();
			}
		});
		saveButton.setPreferredSize(new Dimension(350, 20));
		cButton.gridx = 0;
		cButton.gridy = 8;
		cButton.gridheight = 20;
		cButton.gridwidth = 3;
		cButton.fill = GridBagConstraints.HORIZONTAL;
		cButton.insets = new Insets(5, 5, 5, 5);
		allPanel.add(saveButton, cButton);

		// Add specific views
		if (node != null) {
			nodePopUp = new NodeSpecificPopUp(allPanel, node,
					potentialAttributes, this);
			nodePopUp.createNodeView();
		} else if (edge != null) {
			edgePopUp = new EdgeSpecificPopUp(allPanel, edge,
					potentialNodesForMappings, this);
			edgePopUp.createEdgeView();
		} else if (attribute != null) {
			attributePopUp = new AttributeSpecificPopUp(allPanel, attribute,
					this);
			attributePopUp.createAttributeView();
		}

		// Prevent the user from saving an empty component
		boolean nameFieldIsEmpty = (conjunction != null) ? (conjunction
				.getName().equals("")) : (node != null) ? (node.getName()
				.equals("")) : (edge != null) ? (edge.getName().equals(""))
				: (attribute.getName().equals(""));
		boolean tagFieldIsEmpty = (conjunction != null) ? (conjunction.getTag()
				.equals("")) : (node != null) ? (node.getTag().equals(""))
				: (edge != null) ? (edge.getTag().equals("")) : (attribute
						.getTag().equals(""));
		if (nameFieldIsEmpty || tagFieldIsEmpty) {
			if (nameFieldIsEmpty) {
				namefield.setToolTipText("Name must not be empty");
			}
			if (tagFieldIsEmpty) {
				tagfield.setToolTipText("Tag must not be empty");
			}
			saveButton.setEnabled(false);
		}

		allPanel.setPreferredSize(new Dimension(500, 500));
		allPanel.setMaximumSize(new Dimension(500, 500));
		allPanel.setMinimumSize(new Dimension(500, 500));

		return allPanel;
	}

	/**
	 * Display the Popup to the user
	 */
	public void showComponentDetailView() {

		JOptionPane.showConfirmDialog(null, createPanel(), "Component detail",
				JOptionPane.DEFAULT_OPTION);
	}

	/**
	 * Disable the save button
	 */
	public void disableSaveButton() {
		saveButton.setEnabled(false);
	}

	/**
	 * Revalidate the save button
	 */
	public void revalidateSaveButton() {
		new MyDocumentListener().changed();
	}

	/**
	 * Checks if the user currently has two times the same mapping in the view.
	 * If so, the it is tried to disable the saveButton.
	 */
	public boolean checkForDoubles(ArrayList<String> listToCheck) {
		ArrayList<String> usedNames = new ArrayList<String>();
		for (String component : listToCheck) {
			if (usedNames.contains(component)) {
				return true;
			}
			usedNames.add(component);
		}
		return false;
	}

	/**
	 * DocumentListener to disable the save button if required text fields are
	 * empty
	 * 
	 * @author Alex
	 * 
	 */
	private class MyDocumentListener implements DocumentListener {

		public MyDocumentListener() {
		}

		public void changedUpdate(DocumentEvent e) {
			changed();
		}

		public void removeUpdate(DocumentEvent e) {
			changed();
		}

		public void insertUpdate(DocumentEvent e) {
			changed();
		}

		public void changed() {

			// Checked in order to ensure a component is not
			// created two times, with an empty name etc.
			boolean nameFieldIsEmpty = namefield.getText().isEmpty();
			boolean tagFieldIsEmpty = tagfield.getText().isEmpty();
			boolean componentTagAlreadyExisting = (PSSIFCanonicMetamodelCreator.TAGS
					.containsKey(tagfield.getText()))
					&& !originalTag.equals(tagfield.getText());
			boolean contentNameAlreadyExisting = false;
			boolean contentIsCurrentlyConflicting = false;

			if (nameFieldIsEmpty) {
				namefield.setBorder(BorderFactory.createLineBorder(Color.RED));
				namefield.setToolTipText("Name must not be emtpy");
				saveButton.setEnabled(false);
			}

			if (componentTagAlreadyExisting) {
				tagfield.setBorder(BorderFactory.createLineBorder(Color.RED));
				tagfield.setToolTipText("Component with this tag already exists");
				saveButton.setEnabled(false);
			}

			if (tagFieldIsEmpty) {
				tagfield.setBorder(BorderFactory.createLineBorder(Color.RED));
				tagfield.setToolTipText("Tag must not be emtpy");
				saveButton.setEnabled(false);
			}

			if (conjunction != null) {
				contentNameAlreadyExisting = conjunctionComponentsAsString
						.contains(namefield.getText())
						&& !namefield.getText().equals("")
						&& !originalName.equals(namefield.getText());

				if (contentNameAlreadyExisting) {
					namefield.setBorder(BorderFactory
							.createLineBorder(Color.RED));
					namefield
							.setToolTipText("This conjunction is already existing");
					saveButton.setEnabled(false);
				}
			} else if (node != null) {

				contentNameAlreadyExisting = nodeComponentsAsString
						.contains(namefield.getText())
						&& !namefield.getText().equals("")
						&& !originalName.equals(namefield.getText());

				contentIsCurrentlyConflicting = (nodePopUp
						.getCurrentAttributeNames() != null) ? checkForDoubles(nodePopUp
						.getCurrentAttributeNames()) : false;

				if (contentNameAlreadyExisting || contentIsCurrentlyConflicting) {
					if (contentNameAlreadyExisting) {
						namefield.setBorder(BorderFactory
								.createLineBorder(Color.RED));
						namefield
								.setToolTipText("This node is already existing");
					}

					saveButton.setEnabled(false);
				}

			} else if (edge != null) {
				contentNameAlreadyExisting = edgeComponentsAsString
						.contains(namefield.getText())
						&& !namefield.getText().equals("")
						&& !originalName.equals(namefield.getText());

				contentIsCurrentlyConflicting = (edgePopUp.getCurrentMappings() != null) ? (checkForDoubles(edgePopUp
						.getCurrentMappings())) : false;

				if (contentNameAlreadyExisting || contentIsCurrentlyConflicting) {
					if (contentNameAlreadyExisting) {
						namefield.setBorder(BorderFactory
								.createLineBorder(Color.RED));
						namefield
								.setToolTipText("This edge is already existing");
					}
					saveButton.setEnabled(false);
				}
			} else if (attribute != null) {
				contentNameAlreadyExisting = attributeComponentsAsString
						.contains(namefield.getText())
						&& !namefield.getText().equals("")
						&& !originalName.equals(namefield.getText());

				if (contentNameAlreadyExisting) {
					namefield.setBorder(BorderFactory
							.createLineBorder(Color.RED));
					namefield
							.setToolTipText("This attribute is already existing");
					saveButton.setEnabled(false);
				}
			}

			// Try enabling
			if (!nameFieldIsEmpty && !contentNameAlreadyExisting) {
				namefield
						.setBorder(BorderFactory.createLineBorder(Color.BLACK));
				namefield.setToolTipText("");
			}

			if (!componentTagAlreadyExisting && !tagFieldIsEmpty) {
				tagfield.setBorder(BorderFactory.createLineBorder(Color.BLACK));
				tagfield.setToolTipText("");
			}

			if (!nameFieldIsEmpty && !componentTagAlreadyExisting
					&& !tagFieldIsEmpty && !contentNameAlreadyExisting
					&& !contentIsCurrentlyConflicting) {
				saveButton.setEnabled(true);
			}
		}
	}
}
