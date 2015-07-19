package viewplugin.gui;

import graph.model.MyEdgeType;
import graph.model.MyNodeType;
import graph.operations.MasterFilter;
import gui.GraphView;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import viewplugin.logic.ProjectView;
import viewplugin.logic.ViewManager;

/**
 * 
 * @author deniz
 *
 *         Main Popup of ViewPlugin
 * 
 *         Here the different Project Views can be managed. Features: create
 *         View change View delete View import/export View apply View
 * 
 *
 */
@SuppressWarnings("serial")
public class ManageViewsPopup extends JFrame {

	private ManageViewsPopup frame;
	private JPanel panel;
	private JList<String> viewList;
	private DefaultListModel<String> listModel;
	private GridBagConstraints c;
	private JButton createButton;
	private JButton changeButton;
	private JButton deleteButton;
	private JButton importButton;
	private JButton exportButton;
	private JButton applyProjectViewButton;
	private JButton closeButton;
	private JButton printViewInformation;
	private int selectedViewIndex;
	private GraphView graphView;
	private MasterFilter masterFilter;
	private ProjectView selectedProjectView;

	/**
	 * Constructor
	 * 
	 * @param parent
	 *            - the parent JFrame
	 * @param graphView
	 * @param masterFilter
	 */
	public ManageViewsPopup(JFrame parent, GraphView graphView,
			MasterFilter masterFilter) {

		this.graphView = graphView;
		this.masterFilter = masterFilter;
		frame = this;

		showPopup();
		this.setTitle("Manage Project Views");
		this.setContentPane(panel);
		this.setLocationRelativeTo(parent);
		this.pack();
		this.setVisible(true);
	}

	private void showPopup() {

		panel = new JPanel();

		panel.setLayout(new GridBagLayout());

		/* ViewList */
		listModel = new DefaultListModel<String>();
		refreshJList();

		viewList = new JList<String>(listModel);
		viewList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		viewList.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
				"Project Views"));
		viewList.setBackground(new Color(204, 255, 255));

		viewList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!viewList.isSelectionEmpty()) {
					selectedViewIndex = viewList.getSelectedIndex();
					selectedProjectView = ViewManager.getProjectViews().get(viewList.getSelectedValue());
					refreshApplyButtonText();
					changeButton.setEnabled(true);
					exportButton.setEnabled(true);
					deleteButton.setEnabled(true);
					applyProjectViewButton.setEnabled(true);
					printViewInformation.setEnabled(true);
				} else {
					changeButton.setEnabled(false);
					exportButton.setEnabled(false);
					deleteButton.setEnabled(false);
					applyProjectViewButton.setEnabled(false);
					printViewInformation.setEnabled(false);
				}

			}
		});

		/* Buttons */

		/* CREATE */
		createButton = new JButton("Create View");
		createButton.setAlignmentX(LEFT_ALIGNMENT);
		createButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new CreateNewProjectViewPopup(frame, graphView, masterFilter);
			}
		});

		/* CHANGE */
		changeButton = new JButton("Change View");
		changeButton.setAlignmentX(LEFT_ALIGNMENT);
		changeButton.setEnabled(false);
		changeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new ChangeViewPopup(frame, selectedProjectView, masterFilter, graphView);
			}
		});

		/* DELETE */
		deleteButton = new JButton("Delete");
		deleteButton.setAlignmentX(LEFT_ALIGNMENT);
		deleteButton.setEnabled(false);
		deleteButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				/*
				 * DELETE VIEW VIA VIEWMANAGER
				 */
				String deleteViewName = viewList.getSelectedValue();
				ViewManager.deleteProjectView(deleteViewName);
				listModel.remove(selectedViewIndex);
			}
		});

		/* IMPORT */
		importButton = new JButton("Import View Config");
		importButton.setAlignmentX(LEFT_ALIGNMENT);
		importButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new ImportViewConfigPopup().showPopup(frame);
			}
		});

		/* EXPORT */
		exportButton = new JButton("Export View Config");
		exportButton.setAlignmentX(LEFT_ALIGNMENT);
		exportButton.setEnabled(false);
		exportButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new ExportViewConfigPopup().showPopup(frame,
						viewList.getSelectedValue());
			}
		});

		/* Apply Button */
		applyProjectViewButton = new JButton("Apply selected View");
		applyProjectViewButton.setEnabled(false);
		applyProjectViewButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				String name = viewList.getSelectedValue();
				if (applyProjectViewButton.getText() == "Apply selected View") {
					ViewManager.applyProjectView(name);
				} else {
					ViewManager.undoProjectView(name);
				}
				refreshApplyButtonText();
			}
		});

		/* Print Information about selected View */
		printViewInformation = new JButton(
				"Print Information about selected View");
		printViewInformation.setEnabled(false);
		printViewInformation.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) throws NullPointerException {
				ProjectView pv = ViewManager.getProjectViews().get(
						viewList.getSelectedValue());
				System.out
						.println("********************** Information about Project View "
								+ pv.getName() + " **********************");
				System.out.println("Activated Node Types:");
				for (MyNodeType t : pv.getGraphViewContainer()
						.getSelectedNodeTypes()) {
					System.out.print(t.getName() + " ");
				}
				System.out.println("\nActivated Edge Types:");
				for (MyEdgeType t : pv.getGraphViewContainer()
						.getSelectedEdgeTypes()) {
					System.out.print(t.getName() + " ");
				}
				System.out.println("\nNode Attribute Filters:");
				System.out.println(pv.getNodeAttributeFilterConditions().keySet());
				System.out.println("Edge Attribute Filters:");
				System.out.println(pv.getEdgeAttributeFilterConditions().keySet());
				System.out
						.println("********************************************************************************");

			}
		});

		/* Close Button */
		closeButton = new JButton("Close Window");
		closeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
			}
		});

		/* LAYOUT */
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(10, 10, 10, 10);
		c.ipadx = 200;
		c.ipady = 200;
		c.gridx = 0;
		c.gridy = 0;

		panel.add(new JScrollPane(viewList), c);
		
		c.gridx = 1;
		c.ipady = 0;
		panel.add(createButtonPanel(), c);

	}

	private JPanel createButtonPanel() {
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(10, 10, 10, 10);
		c.ipadx = 50;
		c.ipady = 0;
		c.gridx = 0;
		c.gridy = 0;
		c.gridy = 1;
		buttonPanel.add(createButton, c);
		c.gridy = 2;
		 buttonPanel.add(changeButton, c);
		c.gridy = 3;
		buttonPanel.add(deleteButton, c);
		c.gridy = 4;
		buttonPanel.add(importButton, c);
		c.gridy = 5;
		buttonPanel.add(exportButton, c);
		c.gridy = 6;
		buttonPanel.add(applyProjectViewButton, c);
		c.gridy = 7;
		buttonPanel.add(printViewInformation, c);
		c.gridy = 8;
		buttonPanel.add(closeButton, c);

		return buttonPanel;
	}

	/**
	 * Updates the Project View List via Class ViewManager
	 */
	public void refreshJList() {
		if (ViewManager.getProjectViews() != null) {
			for (String name : ViewManager.getProjectViews().keySet()) {
				if (!listModel.contains(name)) {
					listModel.addElement(name);
				}
			}
		}
	}

	/*
	 * If selected Project View is activated, change Text to
	 * "Undo selected View", else "Apply selected View"
	 */
	private void refreshApplyButtonText() {
		if (!viewList.isSelectionEmpty()) {
			if (ViewManager.getProjectViews().get(viewList.getSelectedValue())
					.isActivated()) {
				applyProjectViewButton.setText("Undo selected View");
			} else {
				applyProjectViewButton.setText("Apply selected View");
			}
		}
	}
}
