package viewplugin.gui;

import graph.model.MyEdgeType;
import graph.model.MyNodeType;
import graph.operations.GraphViewContainer;
import graph.operations.MasterFilter;
import gui.GraphView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import viewplugin.logic.ProjectView;
import viewplugin.logic.ViewManager;

/**
 * 
 * @author deniz
 *
 *         Class for changing a selected Project View. This Popup provides
 *         relevant Information about the selected View.
 */
@SuppressWarnings("serial")
public class ChangeViewPopup extends JFrame {

	private JButton changeNodeAndEdgeTypes;
	private JButton changeAttributeFilter;
	private JButton deleteAttributeFilters;
	private JPanel panel;
	private MasterFilter masterFilter;
	private GraphView graphView;
	private JFrame frame;
	private ProjectView projectView;
	private GraphViewContainer container;
	private JButton changeProjectView;
	private TreeMap<String, String[]> nodeAttributeFilterConditions;
	private TreeMap<String, String[]> edgeAttributeFilterConditions;
	private ManageViewsPopup parent;
	private JList nodeTypes;
	private DefaultListModel<String> nodeModel;
	private JList edgeTypes;
	private DefaultListModel<String> edgeModel;
	private JList attributeFilters;
	private DefaultListModel<String> attributeFilterModel;

	public ChangeViewPopup(final ManageViewsPopup parent, ProjectView pv,
			MasterFilter mf, GraphView gf) {
		this.parent = parent;
		this.projectView = pv;
		this.masterFilter = mf;
		this.graphView = gf;
		this.frame = this;
		this.projectView = pv;
		this.container = this.projectView.getGraphViewContainer();

		panel = new JPanel();

		nodeModel = new DefaultListModel<String>();
		nodeTypes = new JList<String>(nodeModel);

		edgeModel = new DefaultListModel<String>();
		edgeTypes = new JList<String>(edgeModel);

		attributeFilterModel = new DefaultListModel<String>();
		attributeFilters = new JList<String>(attributeFilterModel);

		refreshLists();

		changeNodeAndEdgeTypes = new JButton("Change active Node & Edge Types");
		changeNodeAndEdgeTypes.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				SelectNodeAndEdgeTypesPopup popup = new SelectNodeAndEdgeTypesPopup(
						frame, graphView, masterFilter, projectView.getName());
				container = popup.showPopup();
				refreshLists();
			}
		});

		changeAttributeFilter = new JButton("Change Attribute Filter");
		changeAttributeFilter.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				TreeMap<String, String[]> tmp = new AddAttributeFiltersPopup()
						.showPopup();
				String result = tmp.firstKey();
				String[] params = tmp.get(tmp.firstKey());
				String condition = result.substring(result.indexOf("|") + 1);
				tmp.put(condition, params);
				if (result.startsWith(AddAttributeFiltersPopup.newNode)) {
					nodeAttributeFilterConditions = tmp;
				} else {
					edgeAttributeFilterConditions = tmp;
				}
			}
		});

		deleteAttributeFilters = new JButton("Remove Attribute Filters");
		deleteAttributeFilters.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				ViewManager.createNewProjectView(projectView.getName(),
						container, new TreeMap<String, String[]>() {
						}, new TreeMap<String, String[]>() {
						});
				ViewManager.writeProjectViewsToConfig();
				refreshLists();
			}
		});

		changeProjectView = new JButton("Confirm Changes");
		changeProjectView.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				ViewManager.createNewProjectView(projectView.getName(),
						container, nodeAttributeFilterConditions,
						edgeAttributeFilterConditions);
				parent.refreshJList();
				frame.dispose();
			}
		});

		panel.add(new JScrollPane(this.nodeTypes));
		panel.add(new JScrollPane(this.edgeTypes));
		panel.add(new JScrollPane(this.attributeFilters));
		panel.add(changeNodeAndEdgeTypes);
		panel.add(changeAttributeFilter);
		panel.add(deleteAttributeFilters);
		panel.add(changeProjectView);

		this.setTitle("Change Project View " + pv.getName());
		this.setContentPane(panel);
		this.pack();
		this.setLocationRelativeTo(parent);
		this.setVisible(true);

	}

	public void refreshLists() {
		LinkedList<MyNodeType> nodeTypes = container.getSelectedNodeTypes();
		LinkedList<MyEdgeType> edgeTypes = container.getSelectedEdgeTypes();
		nodeAttributeFilterConditions = projectView
				.getNodeAttributeFilterConditions();
		edgeAttributeFilterConditions = projectView
				.getEdgeAttributeFilterConditions();
		nodeModel.clear();
		edgeModel.clear();

		for (MyNodeType x : nodeTypes) {
			nodeModel.addElement(x.getName());
		}
		for (MyEdgeType x : edgeTypes) {
			edgeModel.addElement(x.getName());
		}
		if (nodeAttributeFilterConditions != null) {
			Set<String> node = nodeAttributeFilterConditions.keySet();
			for (String x : node) {
				attributeFilterModel.addElement(x);
			}
		}
		if (edgeAttributeFilterConditions != null) {
			Set<String> edge = edgeAttributeFilterConditions.keySet();
			for (String x : edge) {
				attributeFilterModel.addElement(x);
			}
		}
	}

}
