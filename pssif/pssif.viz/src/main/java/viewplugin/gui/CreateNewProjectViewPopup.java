package viewplugin.gui;

import graph.operations.GraphViewContainer;
import graph.operations.MasterFilter;
import gui.GraphView;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.TreeMap;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import viewplugin.logic.ProjectView;
import viewplugin.logic.ViewManager;

/**
 * 
 * @author deniz
 *
 *
 *         Popup for creating a new ProjectView
 */
@SuppressWarnings("serial")
public class CreateNewProjectViewPopup extends JFrame {

	private JFrame frame;
	private JButton createView;
	private JButton cancel;
	private JLabel viewName;
	private JTextField nameField;
	private JLabel nodeAndEdgeTypes;
	private JButton selectNodeAndEdgeTypes;
	private JLabel attributeFilter;
	private JButton addAttributeFilter;
	private JPanel panel;
	private GraphView graphView;
	private MasterFilter masterFilter;
	private ManageViewsPopup parent;
	private ProjectView projectView;
	private GraphViewContainer container;
	private TreeMap<String, String[]> nodeAttributeFilters;
	private TreeMap<String, String[]> edgeAttributeFilters;

	public CreateNewProjectViewPopup(ManageViewsPopup parent,
			GraphView graphView, MasterFilter masterFilter) {

		frame = this;
		this.parent = parent;
		this.graphView = graphView;
		this.masterFilter = masterFilter;

		showPopup();

		this.setTitle("Create New Project View");
		this.setContentPane(panel);
		this.pack();
		this.setLocationRelativeTo(parent);
		this.setVisible(true);
	}

	private void showPopup() {

		panel = new JPanel();
		panel.setPreferredSize(new Dimension(600, 400));

		panel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.ipadx = 150;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(10, 10, 10, 10);

		viewName = new JLabel("View Name:");

		nameField = new JTextField();
		nameField.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				if (nameField.getText().isEmpty()) {
					createView.setEnabled(false);
					selectNodeAndEdgeTypes.setEnabled(false);
					addAttributeFilter.setEnabled(false);
				}
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				if (!nameField.getText().isEmpty()) {
					createView.setEnabled(true);
					selectNodeAndEdgeTypes.setEnabled(true);
					addAttributeFilter.setEnabled(true);
				}
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
			}
		});

		nodeAndEdgeTypes = new JLabel("Node and Edge Types:");
		selectNodeAndEdgeTypes = new JButton("Select...");
		selectNodeAndEdgeTypes.setEnabled(false);
		selectNodeAndEdgeTypes.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				/*
				 * Select Node & Edge Types Popup returns GraphViewContainer
				 * Create new ProjectView
				 */
				SelectNodeAndEdgeTypesPopup popup = new SelectNodeAndEdgeTypesPopup(
						frame, graphView, masterFilter, nameField.getText());
				container = popup.showPopup();
			}
		});

		attributeFilter = new JLabel("Filter Attributes:");
		addAttributeFilter = new JButton("Add...");
		addAttributeFilter.setEnabled(false);
		addAttributeFilter.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				TreeMap<String, String[]> tmp = new AddAttributeFiltersPopup().showPopup();
				String result = tmp.firstKey();
				String[] params = tmp.get(tmp.firstKey());
				String condition = result.substring(result.indexOf("|") + 1);
				tmp.put(condition, params);
				if(result.startsWith(AddAttributeFiltersPopup.newNode)) {
					nodeAttributeFilters = tmp;
				} else {
					edgeAttributeFilters = tmp;
				}
				
			}
		});

		createView = new JButton("Create View");
		createView.setEnabled(false);
		createView.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String name = nameField.getText();
				if (!name.isEmpty()) {
					projectView = ViewManager.createNewProjectView(name,
							container, nodeAttributeFilters,
							edgeAttributeFilters);
					parent.refreshJList();
					ViewManager.writeProjectViewsToConfig();
				}
				frame.dispose();
			}
		});

		cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
			}
		});

		c.gridx = 0;
		c.gridy = 0;
		panel.add(viewName, c);
		c.gridx = 1;
		c.gridy = 0;
		panel.add(nameField, c);
		c.gridx = 0;
		c.gridy = 1;
		panel.add(nodeAndEdgeTypes, c);
		c.gridx = 1;
		c.gridy = 1;
		panel.add(selectNodeAndEdgeTypes, c);
		c.gridx = 0;
		c.gridy = 2;
		panel.add(attributeFilter, c);
		c.gridx = 1;
		c.gridy = 2;
		panel.add(addAttributeFilter, c);
		c.gridx = 0;
		c.gridy = 3;
		panel.add(createView, c);
		c.gridx = 1;
		c.gridy = 3;
		panel.add(cancel, c);

		this.add(panel);

	}
}
