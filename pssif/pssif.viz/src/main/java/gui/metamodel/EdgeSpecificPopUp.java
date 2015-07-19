package gui.metamodel;

import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import de.tum.pssif.core.metamodel.external.MetamodelEdge;
import de.tum.pssif.core.model.Tupel;

/**
 * Initiate every component for an edge specific view
 * @author Alex
 *
 */
public class EdgeSpecificPopUp {

	private JPanel edgePanel;
	private JPanel mappingPanel;
	private MetamodelEdge edge;
	private JLabel mappingLabel;
	private JButton addMappingButton;
	private JScrollPane mappingScroller;
	private GridBagConstraints cMapFromField;
	private GridBagConstraints cMapLabel;
	private GridBagConstraints cMapToField;
	private GridBagConstraints cButton;
	private GridBagConstraints cLabel;
	private GridBagConstraints cScrollPane;
	// Defines line shift
	private int mappingCounter = 0;
	// The mapping which was configured in the combo box the user changed
	private String formerSelected;
	// List of all the currently displayed mappings
	private ArrayList<String> currentSetMappings;
	private ArrayList<String> possibleMappingCLients;
	private ComponentDetailPopup parent;

	/**
	 * Constructor for the edge specific view
	 * @param edgePanel Panel to add the view components to
	 * @param edge The component containing the data
	 * @param possibleMappingCLients Every node which is potentially involved in a mapping situation
	 * @param parent The originalView to revalidate the save button
	 */
	public EdgeSpecificPopUp(JPanel edgePanel, MetamodelEdge edge,
			ArrayList<String> possibleMappingCLients,
			ComponentDetailPopup parent) {
		this.edgePanel = edgePanel;
		this.edge = edge;
		this.possibleMappingCLients = possibleMappingCLients;
		this.parent = parent;
	}

	/**
	 * Create the edge related view components
	 */
	public void createEdgeView() {

		// Constraint for every Button
		cButton = new GridBagConstraints();
		cButton.ipadx = 15;
		cButton.ipady = 5;

		// Constraint for every Scroll Panel for Attributes
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

		// Constraint for every Label
		cLabel = new GridBagConstraints();
		cLabel.ipadx = 50;
		cLabel.gridwidth = 1;
		cLabel.anchor = GridBagConstraints.WEST;

		mappingLabel = new JLabel("Mappings");
		cLabel.gridx = 0;
		cLabel.gridy = 5;
		cLabel.ipadx = 0;
		edgePanel.add(mappingLabel, cLabel);

		mappingPanel = new JPanel(new GridBagLayout());
		mappingScroller = new JScrollPane(mappingPanel);
		mappingScroller
				.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

		// Constraint for every Label for Mappings
		cMapLabel = new GridBagConstraints();
		cMapLabel.ipadx = 10;
		cMapLabel.gridwidth = 1;
		cMapLabel.anchor = GridBagConstraints.CENTER;

		// Constraint for every TextField for Attributes
		cMapFromField = new GridBagConstraints();
		cMapFromField.ipadx = 70;
		cMapFromField.gridwidth = 1;
		cMapFromField.fill = GridBagConstraints.HORIZONTAL;
		cMapFromField.anchor = GridBagConstraints.CENTER;

		cMapToField = new GridBagConstraints();
		cMapToField.ipadx = 70;
		cMapToField.gridwidth = 1;
		cMapToField.fill = GridBagConstraints.HORIZONTAL;
		cMapToField.anchor = GridBagConstraints.CENTER;

		final JLabel from = new JLabel("from");
		cMapFromField.gridx = 0;
		cMapFromField.gridy = 0;
		mappingPanel.add(from, cMapFromField);

		final JLabel to = new JLabel("to");
		cMapToField.gridx = 2;
		cMapToField.gridy = 0;
		mappingPanel.add(to, cMapToField);

		addMappingButton = new JButton("+");
		addMappingButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				addMapping(mappingPanel, possibleMappingCLients.get(0),
						possibleMappingCLients.get(0));
				mappingPanel.revalidate();
				mappingPanel.repaint();
			}
		});
		addMappingButton.setPreferredSize(new Dimension(350, 20));
		cButton.gridx = 0;
		cButton.gridy = 7;
		cButton.gridwidth = 3;
		cButton.fill = GridBagConstraints.HORIZONTAL;
		edgePanel.add(addMappingButton, cButton);

		if (edge.getMappings() != null) {

			for (Tupel mappings : edge.getMappings()) {
				addMapping(mappingPanel, mappings.getFirst(),
						mappings.getSecond());
			}
		}

		mappingScroller.setPreferredSize(new Dimension(500, 250));
		edgePanel.add(mappingScroller, cScrollPane);

	}

	/**
	 * Add a mapping to the provided JPanel
	 * 
	 * @param mappingPanel
	 *            Panel to add the mapping to
	 * @param from
	 *            Origin of the mapping
	 * @param to
	 *            Destination of the mapping
	 */
	private void addMapping(final JPanel mappingPanel, String from, String to) {

		if(currentSetMappings == null) {
			currentSetMappings = new ArrayList<String>();
		}
		
		String[] mappingClients = possibleMappingCLients
				.toArray(new String[possibleMappingCLients.size()]);

		final JComboBox<String> fromCombo = new JComboBox<String>(
				mappingClients);
		fromCombo.setSelectedItem(from);
		fromCombo.setPreferredSize(new Dimension(90, 25));
		cMapFromField.gridx = 0;
		cMapFromField.gridy = mappingCounter + 2;
		mappingPanel.add(fromCombo, cMapFromField);

		final JLabel connector = new JLabel("-");
		cMapLabel.gridx = 1;
		cMapLabel.gridy = mappingCounter + 2;
		mappingPanel.add(connector, cMapLabel);

		final JComboBox<String> toCombo = new JComboBox<String>(mappingClients);
		toCombo.setSelectedItem(to);
		toCombo.setPreferredSize(new Dimension(90, 25));
		cMapFromField.gridx = 2;
		cMapFromField.gridy = mappingCounter + 2;
		mappingPanel.add(toCombo, cMapFromField);

		currentSetMappings.add(from + ";" + to);
		parent.revalidateSaveButton();

		fromCombo.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent itemEvent) {

				if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
					currentSetMappings.add((String) fromCombo.getSelectedItem()
							+ ";" + (String) toCombo.getSelectedItem());
					currentSetMappings.remove(formerSelected);
					parent.revalidateSaveButton();
				} else {
					formerSelected = (String) itemEvent.getItem()
							+ ";" + (String) toCombo.getSelectedItem();
				}
			}
		});
		toCombo.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent itemEvent) {

				if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
					currentSetMappings.add((String) fromCombo.getSelectedItem()
							+ ";" + (String) toCombo.getSelectedItem());
					currentSetMappings.remove(formerSelected);
					parent.revalidateSaveButton();
				} else {
					formerSelected = (String) fromCombo.getSelectedItem()
							+ ";" + (String) itemEvent.getItem();
				}
			}
		});

		// / Delete Mapping Button
		// Path to Icon
		String path = System.getProperty("user.dir");
		String delete = path.substring(0, path.length() - 9)
				+ "button\\deletebutton.png";

		Icon icon = new javax.swing.ImageIcon(delete);
		((ImageIcon) icon).setImage(((ImageIcon) icon).getImage()
				.getScaledInstance(15, 15, Image.SCALE_DEFAULT));
		final JButton deleteMappingButton = new JButton(icon);

		GridBagConstraints deleteButtonConst = new GridBagConstraints();
		deleteMappingButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				currentSetMappings.remove((String) fromCombo.getSelectedItem()
						+ ";" + (String) toCombo.getSelectedItem());
				parent.revalidateSaveButton();

				mappingPanel.remove(fromCombo);
				mappingPanel.remove(connector);
				mappingPanel.remove(toCombo);
				mappingPanel.remove(deleteMappingButton);

				mappingPanel.revalidate();
				mappingPanel.repaint();
			}
		});
		deleteMappingButton.setPreferredSize(new Dimension(20, 20));
		deleteMappingButton.setFont(new Font("serif", Font.PLAIN, 6));
		deleteButtonConst.gridheight = 1;
		deleteButtonConst.gridx = 3;
		deleteButtonConst.gridy = mappingCounter + 2;
		deleteButtonConst.gridwidth = 3;
		deleteButtonConst.insets = new Insets(5, 5, 5, 5);
		mappingPanel.add(deleteMappingButton, deleteButtonConst);

		mappingCounter++;
	}
	
	/**
	 * Return all mappings currently set by the user
	 * @return The requested mappings
	 */
	public ArrayList<String> getCurrentMappings() {
		return currentSetMappings;
	}

}
