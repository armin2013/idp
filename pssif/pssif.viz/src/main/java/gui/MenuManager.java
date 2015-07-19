package gui;

import graph.operations.AttributeAggregation;
import graph.operations.AttributeFilter;
import graph.operations.GraphViewContainer;
import graph.operations.MasterFilter;
import gui.graph.AttributeFilterPopup;
import gui.graph.CreateNewGraphViewPopup;
import gui.graph.GraphVisualization;
import gui.graph.HighlightNodePopup;
import gui.graph.NodeColorPopup;
import gui.graph.NodeIconPopup;
import gui.metamodel.MetamodelPopUp;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import viewplugin.gui.ManageViewsPopup;
import de.tum.pssif.transform.mapper.db.PssifToDBMapperImpl;
import model.FileExporter;

public class MenuManager {
	
	private JMenuItem manageViews;
	private JMenu projectViews;
	
	private PssifToDBMapperImpl toDBMapper = null;
	
	private JFrame frame = null;
	private MainFrame mainFrame = null;
	GraphView graphView = null;
	MatrixView matrixView = null;
	MasterFilter masterFilter = null;
	
	FileCommands fcommands;
	
	public MenuManager(MainFrame mainFrame)
	{
		this.mainFrame = mainFrame;
		this.frame = this.mainFrame.getFrame();
		fcommands = this.mainFrame.getFileCommands();
	
	}
	public void update()
	{
		this.frame = this.mainFrame.getFrame();
		fcommands = this.mainFrame.getFileCommands();
	}
	
	public void setValues(GraphView graphView, MatrixView matrixView,MasterFilter masterFilter)
	{
		this.graphView = graphView;
		this.matrixView = matrixView;
		this.masterFilter = masterFilter;
	}
	
	
	private JMenuItem resetGraph;
	private JMenuItem resetMatrix;
	private JMenuItem colorNodes;
	private JMenuItem shapeNodes;
	private JMenuItem newProject;
	private JMenuItem modelStatistics;
	private JMenuItem createView;
	private JMenuItem attributFilter;
	private JMenuItem graphVizualistation;
	private JMenuItem matrixVizualistation;
	private JMenuItem importFile;
	private JMenuItem exportFile;
	private JMenuItem importDB;
	private JMenuItem exportDB;
	private JMenuItem followEdges;
	private JCheckBoxMenuItem KKLayout;
	private JCheckBoxMenuItem FRLayout;
	private JCheckBoxMenuItem SpringLayout;
	private JCheckBoxMenuItem ISOMLayout;
	private JCheckBoxMenuItem CircleLayout;
	private JMenu applyView;
	private JMenu deleteView;
	private JMenu graphLayout;
	private JMenu graphOperations;
	private JMenu applyNodeFilter;
	private JMenu applyEdgeFilter;
	private JMenu deleteNodeFilter;
	private JMenu deleteEdgeFilter;
	private JMenu exportDBTurtle;
	private JMenuItem exportDBTurtleItem;
	
	
	
	/**
	 * Create MenuBar with only the Import Button
	 * 
	 * @return MenuBar
	 */
	public JMenuBar createFileMenu() {
		JMenuBar menuBar = new JMenuBar();

		JMenu fileMenu = new JMenu("File");

		newProject = new JMenuItem("New Project from Req");
		newProject.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				fcommands.createNewFile();
			}
		});

		fileMenu.add(newProject);

		importFile = new JMenuItem("Import File");
		importFile.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				fcommands.importFromFile();
			}
		});

		fileMenu.add(importFile);

		// needed to import Graph from DB @author Andrea
		importDB = new JMenuItem("Import from DB");
		importDB.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				fcommands.importFromDB();
			}
		});

		fileMenu.add(importDB);

		menuBar.add(fileMenu);
		
		
		exportDBTurtle = new JMenu("Export DB");
		exportDBTurtleItem = new JMenuItem("Export in Turtle File");
		exportDBTurtleItem.addActionListener(new ActionListener() {

			@SuppressWarnings("static-access")
			@Override
			public void actionPerformed(ActionEvent e) {
			

				// Just make sure that there exists an instance of fromDB and
				// toDB to get the rdfModel
				// if (fromDBMapper == null) {
				 if (toDBMapper == null) {
				toDBMapper = new PssifToDBMapperImpl();
			
				 } 
				// else
				// model = toDBMapper.rdfModel;
				// } else
				// model = fromDBMapper.rdfModel;

				// Use a FileChooser to select the location for the file
				JFileChooser finder = new JFileChooser();
				finder.setSelectedFile(new File(System.getProperty("user.home")
						+ "\\TurtleExport.ttl"));

				int returnVal = finder.showSaveDialog(null);
				if (returnVal == javax.swing.JFileChooser.APPROVE_OPTION) {
					java.io.File file = finder.getSelectedFile();
					// save the model of the DB into selected file
					try {
						BufferedWriter writer = new BufferedWriter(
								new OutputStreamWriter(new FileOutputStream(
										file, false)));
						toDBMapper.rdfModel.write(writer, "TTL");
						JOptionPane.showMessageDialog(null, "Export successful",
								"PSSIF", JOptionPane.INFORMATION_MESSAGE);
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					}
					
				}
			}
		});
		exportDBTurtle.add(exportDBTurtleItem);
		menuBar.add(exportDBTurtle);
		

		return menuBar;
	}

	/**
	 * create A MenuBar with all the possible SubMenus
	 * 
	 * @return
	 */
	public JMenuBar createMenu() {
		// contains already the file Menu and Import
		JMenuBar menuBar = createFileMenu();

		// add additional functions to the File Menu
		menuBar = addFileMenu(menuBar);

		// Which GraphLayout was chosen
		menuBar.add(addChangeLayouts());

		// Which Visualization Mode
		menuBar.add(addVizModes());

		// Reset the Graph or Matrix
		menuBar.add(addResetOptions());

		// Color Options
		menuBar.add(addGraphOperations());

		// Open Menu to display and manipulate the metamodel
		menuBar.add(addMetaModelChangeOption());
		
		// Project View Menu
		menuBar.add(addProjectViewMenu());

		return menuBar;
	}

	/**
	 * Adds the missing options to the "File" Menu to the MenuBar (Import File
	 * not included)
	 * 
	 * @param menuBar
	 *            where to place the menu
	 * @return the updated menuBar
	 */
	private JMenuBar addFileMenu(JMenuBar menuBar) {
		exportFile = new JMenuItem("Export File");

		exportFile.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				FileExporter exporter = new FileExporter();

				if (exporter.showPopup(frame)) {
					JPanel errorPanel = new JPanel();

					errorPanel.add(new JLabel("Export successful."));

					JOptionPane.showMessageDialog(null, errorPanel, "Success!",
							JOptionPane.INFORMATION_MESSAGE);
				}

			}
		});

		menuBar.getMenu(0).add(exportFile);

		// needed to export Graph to DB @author Andrea
		exportDB = new JMenuItem("Save to DB");
		exportDB.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if (toDBMapper == null){
						toDBMapper = new PssifToDBMapperImpl();
					}
					// If the model was merged then delete DB and build new
					// rdfModel from activeModel
					if (PssifToDBMapperImpl.merge == true) {
						toDBMapper.modelToDB();
						PssifToDBMapperImpl.merge = false;
					}
					// If the database could be deleted compeletely because of
					// not importing the database before importing the model...
					// Then ask the user if he wants to delete the DB or add the
					// new model to the DB
					else if (PssifToDBMapperImpl.deleteAll == true) {
						int delete = JOptionPane.showConfirmDialog(null,
								"Should the Database be deleted?");

						// If the DB should be deleted use the modelToDB Method
						// to save the activeModel
						if (delete == 0)
							toDBMapper.modelToDB();
						// If the DB should not be deleted use the saveToDB
						// Method to just save the changes
						else if (delete == 1)
							toDBMapper.saveToDB();
						else
							return;
						PssifToDBMapperImpl.deleteAll = false;
						// If there is just a minor change in the model of the
						// DB, just save the changes
					} else
						toDBMapper.saveToDB();

					JOptionPane.showMessageDialog(null, "Successfully saved!",
							"PSSIF", JOptionPane.INFORMATION_MESSAGE);
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(null, "Error with saving!\n"
							+ e1.getMessage(), "PSSIF",
							JOptionPane.ERROR_MESSAGE);
				}

			}
		});

		menuBar.getMenu(0).add(exportDB);

		colorNodes = new JMenuItem("Choose Node Color");
		colorNodes.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				NodeColorPopup popup = new NodeColorPopup(graphView);

				popup.showPopup();
			}
		});

		menuBar.getMenu(0).add(colorNodes);
		
		shapeNodes = new JMenuItem("Choose Node Shape");
		shapeNodes.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				NodeIconPopup popup = new NodeIconPopup(graphView);

				popup.showPopup();
			}
		});

		menuBar.getMenu(0).add(shapeNodes);

		modelStatistics = new JMenuItem("Statistics");
		modelStatistics.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				AttributeAggregation agg = new AttributeAggregation();

				agg.showInfoPanel();

			}
		});
		menuBar.getMenu(0).add(modelStatistics);

		return menuBar;
	}

	/**
	 * Adds the Option to view and change the metamodel
	 * 
	 * @return The updated MenuBar
	 */
	private JMenu addMetaModelChangeOption() {
		JMenu metamodelMenu = new JMenu();
		metamodelMenu.setText("Metamodel");
		metamodelMenu.setIcon(null);
		metamodelMenu.setPreferredSize(new Dimension(80, 20));

		JMenuItem changeOption = new JMenuItem("Manipulate Metamodel");
		metamodelMenu.add(changeOption);

		changeOption.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				new MetamodelPopUp().showMetamodelView();
			}
		});

		return metamodelMenu;
	}

	/*
	 * 
	 * Project View Menu
	 */
	private JMenu addProjectViewMenu() {

		projectViews = new JMenu();
		projectViews.setText("Project View");

		/* Reset View */
		// resetView = new JMenuItem();
		// resetView.setText("Reset View");
		//
		// projectViews.add(resetView);
		//
		// resetView.addActionListener(new ActionListener() {
		// @Override
		// public void actionPerformed(ActionEvent e) {
		//
		// }
		// });

		/* ViewManager */
		manageViews = new JMenuItem();
		manageViews.setText("Manage Views");

		projectViews.add(manageViews);

		manageViews.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new ManageViewsPopup(frame, graphView, masterFilter);
				// ProjectView pv =
				// ViewManager.createNewProjectView("Standard View - No Filters",
				// graphView, masterFilter);
				// new ManageViewsPopup(frame, pv);
			}
		});

		return projectViews;
	}
	
	
	/**
	 * Adds the Option to change the Graph Layout to the MenuBar
	 * 
	 * @return The updated MenuBar
	 */
	private JMenu addChangeLayouts() {
		graphLayout = new JMenu("Graph Layout");

		KKLayout = new JCheckBoxMenuItem(GraphVisualization.KKLayout);
		KKLayout.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				graphView.getGraph().changeLayout(GraphVisualization.KKLayout);
				FRLayout.setSelected(false);
				SpringLayout.setSelected(false);
				ISOMLayout.setSelected(false);
				CircleLayout.setSelected(false);
				KKLayout.setSelected(true);
				// TestLayout.setSelected(false);
			}
		});
		graphLayout.add(KKLayout);

		FRLayout = new JCheckBoxMenuItem(GraphVisualization.FRLayout);
		FRLayout.setSelected(true);
		FRLayout.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				graphView.getGraph().changeLayout(GraphVisualization.FRLayout);
				KKLayout.setSelected(false);
				SpringLayout.setSelected(false);
				ISOMLayout.setSelected(false);
				CircleLayout.setSelected(false);
				FRLayout.setSelected(true);
				// TestLayout.setSelected(false);
			}
		});
		graphLayout.add(FRLayout);

		SpringLayout = new JCheckBoxMenuItem(GraphVisualization.SpringLayout);
		SpringLayout.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				graphView.getGraph().changeLayout(
						GraphVisualization.SpringLayout);
				KKLayout.setSelected(false);
				FRLayout.setSelected(false);
				ISOMLayout.setSelected(false);
				CircleLayout.setSelected(false);
				SpringLayout.setSelected(true);
				// TestLayout.setSelected(false);
			}
		});
		graphLayout.add(SpringLayout);

		ISOMLayout = new JCheckBoxMenuItem(GraphVisualization.ISOMLayout);
		ISOMLayout.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				graphView.getGraph()
						.changeLayout(GraphVisualization.ISOMLayout);
				KKLayout.setSelected(false);
				FRLayout.setSelected(false);
				SpringLayout.setSelected(false);
				CircleLayout.setSelected(false);
				ISOMLayout.setSelected(true);
				// TestLayout.setSelected(false);
			}
		});
		graphLayout.add(ISOMLayout);

		CircleLayout = new JCheckBoxMenuItem(GraphVisualization.CircleLayout);
		CircleLayout.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				graphView.getGraph().changeLayout(
						GraphVisualization.CircleLayout);
				KKLayout.setSelected(false);
				FRLayout.setSelected(false);
				SpringLayout.setSelected(false);
				ISOMLayout.setSelected(false);
				CircleLayout.setSelected(true);
				// TestLayout.setSelected(false);
			}
		});
		graphLayout.add(CircleLayout);

		/*
		 * TestLayout = new JCheckBoxMenuItem(GraphVisualization.TestLayout);
		 * TestLayout.addActionListener(new ActionListener() {
		 * 
		 * @Override public void actionPerformed(ActionEvent e) {
		 * graphView.getGraph().changeLayout(GraphVisualization.TestLayout);
		 * KKLayout.setSelected(false); FRLayout.setSelected(false);
		 * SpringLayout.setSelected(false); ISOMLayout.setSelected(false);
		 * TestLayout.setSelected(true); } }); graphLayout.add(TestLayout);
		 */

		return graphLayout;
	}

	/**
	 * Adds the possibility to change to the GraphView or MatrixView to the
	 * MenuBar
	 * 
	 * @return The updated MenuBar
	 */
	private JMenu addVizModes() {
		JMenu visualisationMenu = new JMenu("Visualization Mode");
		graphVizualistation = new JMenuItem("Graph");
		graphVizualistation.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				frame.getContentPane().removeAll();
				Dimension d = frame.getSize();

				frame.getContentPane().add(graphView.getGraphPanel());
				graphView.setActive(true);
				matrixView.setActive(false);
				adjustButtons();
				frame.setPreferredSize(d);

				frame.pack();
				frame.repaint();
			}
		});
		visualisationMenu.add(graphVizualistation);
		visualisationMenu.setIcon(null);

		matrixVizualistation = new JMenuItem("Matrix");
		matrixVizualistation.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				frame.getContentPane().removeAll();
				Dimension d = frame.getSize();

				frame.getContentPane().add(matrixView.getVisualization());
				graphView.setActive(false);
				matrixView.setActive(true);
				adjustButtons();
				frame.setPreferredSize(d);

				frame.pack();
				frame.repaint();
			}
		});
		visualisationMenu.add(matrixVizualistation);
		visualisationMenu.setIcon(null);

		return visualisationMenu;
	}

	/**
	 * Adds the Reset Matrix and Graph Options to the MenuBar
	 * 
	 * @return The updated MenuBar
	 */
	private JMenu addResetOptions() {
		JMenu resetMenu = new JMenu("Reset");
		resetGraph = new JMenuItem("Reset Graph");
		resetGraph.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				graphView.resetGraph();
				mainFrame.initFrame();
			}
		});

		resetMenu.add(resetGraph);

		resetMatrix = new JMenuItem("Reset Matrix Filter");
		resetMatrix.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				boolean res = matrixView.chooseEdgesAndNodes();

				frame.getContentPane().removeAll();
				if (res) {
					// create the Matrix View
					frame.getContentPane().add(matrixView.getVisualization());
				} else {
					// create an information Panel and display no Matrix
					JPanel allPanel = new JPanel(new GridBagLayout());
					allPanel.setBorder(BorderFactory.createEmptyBorder(10, 10,
							10, 10));
					JLabel label = new JLabel(
							"Please select a least one EdgeType and one NodeType");
					label.setFont(new Font("Arial", Font.ITALIC, 25));
					allPanel.add(label);
					frame.add(allPanel);
				}
				frame.pack();
				frame.repaint();

			}
		});
		resetMenu.add(resetMatrix);
		resetMenu.setIcon(null);

		return resetMenu;
	}

	/**
	 * Adds the Graph Filters (View, AttributeFilter, FollowEdges) to the
	 * MenuBar
	 * 
	 * @return The updated MenuBar
	 */
	private JMenu addGraphOperations() {
		graphOperations = new JMenu("Graph Operations");

		attributFilter = new JMenuItem("Create a new Attribute Filter");
		attributFilter.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				AttributeFilterPopup filter = new AttributeFilterPopup();

				String result = filter.showPopup();

				if (result != null
						&& result.contains(AttributeFilterPopup.newEdge)) {
					String condition = result.substring(result.indexOf("|") + 1);

					// Tell the MasterFilter
					masterFilter.addEdgeAttributFilter(condition, true);

					// update the MenuBar
					resetApplyEdgeFilters(condition);
					resetDeleteEdgeFilters();
				}

				if (result != null
						&& result.contains(AttributeFilterPopup.newNode)) {
					String condition = result.substring(result.indexOf("|") + 1);

					// Tell the MasterFilter
					masterFilter.addNodeAttributFilter(condition, true);

					// update the MenuBar
					resetApplyNodeFilters(condition);
					resetDeleteNodeFilters();
				}

			}
		});

		graphOperations.add(attributFilter);

		applyNodeFilter = new JMenu("Apply Node Attribute Filters");
		// create the SubMenus
		addApplyNodeFilters(null);
		graphOperations.add(applyNodeFilter);

		deleteNodeFilter = new JMenu("Remove Node Attribute Filters");
		// create the SubMenus
		addRemoveNodeFilters();
		graphOperations.add(deleteNodeFilter);

		applyEdgeFilter = new JMenu("Apply Edge Attribute Filters");
		// create the SubMenus
		addApplyEdgeFilters(null);
		graphOperations.add(applyEdgeFilter);

		deleteEdgeFilter = new JMenu("Remove Edge Attribute Filters");
		// create the SubMenus
		addRemoveEdgeFilters();
		graphOperations.add(deleteEdgeFilter);

		followEdges = new JMenuItem("Choose Follow EdgeTypes");
		followEdges.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				HighlightNodePopup popup = new HighlightNodePopup(graphView);

				popup.showPopup();
			}
		});

		graphOperations.add(followEdges);

		createView = new JMenuItem("Create a new GraphView");
		createView.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				createNewGraphView();
				resetReadGraphViews();
			}
		});
		graphOperations.add(createView);

		applyView = new JMenu("Apply GraphView");

		graphOperations.add(applyView);
		// create the SubMenus
		readGraphViews();

		deleteView = new JMenu("Delete GraphView");

		graphOperations.add(deleteView);
		// create the SubMenus
		deleteGraphView();

		return graphOperations;
	}

	/**
	 * defines which Items of the MenuBar are currently visible
	 */
	public void adjustButtons() {
		if (graphView.isActive()) {
			resetGraph.setEnabled(true);
			resetMatrix.setEnabled(false);
			graphVizualistation.setEnabled(false);
			matrixVizualistation.setEnabled(true);
			graphLayout.setEnabled(true);
			graphOperations.setEnabled(true);
			modelStatistics.setEnabled(true);
		}

		if (matrixView.isActive()) {
			resetGraph.setEnabled(false);
			resetMatrix.setEnabled(true);
			graphVizualistation.setEnabled(true);
			matrixVizualistation.setEnabled(false);
			graphLayout.setEnabled(false);
			graphOperations.setEnabled(false);
			modelStatistics.setEnabled(true);
		}

	}

	/**
	 * Displays the Popup to create a new GraphView and updates the MenuBar
	 * accordingly to the users choices
	 */
	private void createNewGraphView() {
		CreateNewGraphViewPopup popup = new CreateNewGraphViewPopup(graphView,
				masterFilter);
		boolean res = popup.showPopup();

		if (res) {
			// update the menus
			resetDeleteGraphViews();
			resetReadGraphViews();
		}
	}

	/**
	 * Reads all the GraphViews and display for each View a MenuItem to
	 * apply/undo the GraphView filter
	 */
	private void readGraphViews() {
		final HashMap<String, GraphViewContainer> views = graphView.getGraph()
				.getAllGraphViews();

		if (views.size() == 0) {
			applyView.setEnabled(false);
		} else {
			applyView.setEnabled(true);

			for (final String name : views.keySet()) {
				JCheckBoxMenuItem menuItem = new JCheckBoxMenuItem(name);

				menuItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {

						JCheckBoxMenuItem item = (JCheckBoxMenuItem) e
								.getSource();
						if (item.isSelected()) {
							masterFilter.applyNodeAndEdgeTypeFilter(name);
						} else {
							masterFilter.undoNodeAndEdgeTypeFilter(name);
						}
					}
				});

				applyView.add(menuItem);
			}
		}
	}

	/**
	 * Removes all the apply View MenuItems and creates them from scratch
	 */
	private void resetReadGraphViews() {
		applyView.removeAll();
		readGraphViews();

	}

	/**
	 * Reads all the GraphViews and display for each View a MenuItem to delete
	 * the selected the GraphView filter
	 */
	private void deleteGraphView() {
		final HashMap<String, GraphViewContainer> views = graphView.getGraph()
				.getAllGraphViews();

		if (views.size() == 0) {
			deleteView.setEnabled(false);
		} else {
			deleteView.setEnabled(true);

			for (final String name : views.keySet()) {
				JMenuItem menuItem = new JMenuItem(name);

				menuItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {

						graphView.getGraph().deleteGraphView(views.get(name));

						masterFilter.removeNodeAndEdgeTypeFilter(name);

						resetReadGraphViews();
						resetDeleteGraphViews();
					}
				});

				deleteView.add(menuItem);
			}
		}
	}

	/**
	 * Removes all the delete View MenuItems and creates them from scratch
	 */
	private void resetDeleteGraphViews() {
		deleteView.removeAll();
		deleteGraphView();
	}

	/**
	 * Creates all the MenuItems to apply the Node Attribute Filters
	 * 
	 * @param newCondition
	 *            a condition which should be applied immediately ( can be null,
	 *            so no condition is applied)
	 */
	private void addApplyNodeFilters(String newCondition) {
		LinkedList<String> conditions = AttributeFilter.getAllNodeConditions();

		if (conditions.size() == 0) {
			applyNodeFilter.setEnabled(false);
		} else {
			applyNodeFilter.setEnabled(true);

			for (String name : conditions) {
				JCheckBoxMenuItem menuItem = new JCheckBoxMenuItem(name);

				menuItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {

						try {
							JCheckBoxMenuItem item = (JCheckBoxMenuItem) e
									.getSource();
							String condition = item.getText();
							if (item.isSelected()) {
								masterFilter
										.applyNodeAttributeFilter(condition);
							} else {
								masterFilter.undoNodeAttributeFilter(condition);
							}
						} catch (Exception ex) {
							ex.printStackTrace();
							JPanel errorPanel = new JPanel();

							errorPanel
									.add(new JLabel(
											"There was a problem applying the attribute filter"));

							JOptionPane.showMessageDialog(null, errorPanel,
									"Ups something went wrong",
									JOptionPane.ERROR_MESSAGE);
						}
					}
				});

				if ((newCondition != null && name.equals(newCondition))
						|| masterFilter.NodeAttributFilterActive(name)) {
					menuItem.setSelected(true);
				}
				applyNodeFilter.add(menuItem);
			}
		}
	}

	/**
	 * Creates all the MenuItems to apply the Edge Attribute Filters
	 * 
	 * @param newCondition
	 *            a condition which should be applied immediately ( can be null,
	 *            so no condition is applied)
	 */
	private void addApplyEdgeFilters(String newCondition) {
		LinkedList<String> conditions = AttributeFilter.getAllEdgeConditions();

		if (conditions.size() == 0) {
			applyEdgeFilter.setEnabled(false);
		} else {
			applyEdgeFilter.setEnabled(true);

			for (String name : conditions) {
				JCheckBoxMenuItem menuItem = new JCheckBoxMenuItem(name);

				menuItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {

						try {
							JCheckBoxMenuItem item = (JCheckBoxMenuItem) e
									.getSource();
							String condition = item.getText();
							if (item.isSelected()) {
								masterFilter
										.applyEdgeAttributeFilter(condition);
							} else {
								masterFilter.undoEdgeAttributeFilter(condition);
							}
						} catch (Exception ex) {
							ex.printStackTrace();
							JPanel errorPanel = new JPanel();

							errorPanel
									.add(new JLabel(
											"There was a problem applying the attribute filter"));

							JOptionPane.showMessageDialog(null, errorPanel,
									"Ups something went wrong",
									JOptionPane.ERROR_MESSAGE);
						}
					}
				});

				if ((newCondition != null && name.equals(newCondition))
						|| masterFilter.EdgeAttributFilterActive(name)) {
					menuItem.setSelected(true);
				}
				applyEdgeFilter.add(menuItem);
			}
		}
	}

	/**
	 * Creates all the MenuItems to remove the Node Attribute Filters
	 */
	private void addRemoveNodeFilters() {
		LinkedList<String> conditions = AttributeFilter.getAllNodeConditions();

		if (conditions.size() == 0) {
			deleteNodeFilter.setEnabled(false);
		} else {
			deleteNodeFilter.setEnabled(true);

			for (final String name : conditions) {
				JMenuItem menuItem = new JMenuItem(name);

				menuItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							masterFilter.removeNodeAttributFilter(name);

							resetApplyNodeFilters(null);
							resetDeleteNodeFilters();
						} catch (Exception ex) {
							ex.printStackTrace();
							JPanel errorPanel = new JPanel();

							errorPanel
									.add(new JLabel(
											"There was a problem deleting the attribute filter"));

							JOptionPane.showMessageDialog(null, errorPanel,
									"Ups something went wrong",
									JOptionPane.ERROR_MESSAGE);
						}
					}
				});

				deleteNodeFilter.add(menuItem);
			}
		}
	}

	/**
	 * Creates all the MenuItems to remove the Edge Attribute Filters
	 */
	private void addRemoveEdgeFilters() {
		LinkedList<String> conditions = AttributeFilter.getAllEdgeConditions();

		if (conditions.size() == 0) {
			deleteEdgeFilter.setEnabled(false);
		} else {
			deleteEdgeFilter.setEnabled(true);

			for (final String name : conditions) {
				JMenuItem menuItem = new JMenuItem(name);

				menuItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							masterFilter.removeEdgeAttributFilter(name);

							resetApplyEdgeFilters(null);
							resetDeleteEdgeFilters();
						} catch (Exception ex) {
							ex.printStackTrace();
							JPanel errorPanel = new JPanel();

							errorPanel
									.add(new JLabel(
											"There was a problem deleting the attribute filter"));

							JOptionPane.showMessageDialog(null, errorPanel,
									"Ups something went wrong",
									JOptionPane.ERROR_MESSAGE);
						}
					}
				});
				deleteEdgeFilter.add(menuItem);
			}
		}
	}

	/**
	 * Removes all the apply Node Filter MenuItems and creates them from scratch
	 * 
	 * @param newCondition
	 *            a condition which should be applied immediately ( can be null,
	 *            so no condition is applied)
	 */
	private void resetApplyNodeFilters(String newConditon) {
		applyNodeFilter.removeAll();
		addApplyNodeFilters(newConditon);
	}

	/**
	 * Removes all the delete Node Filter MenuItems and creates them from
	 * scratch
	 */
	private void resetDeleteNodeFilters() {
		deleteNodeFilter.removeAll();
		addRemoveNodeFilters();
	}

	/**
	 * Removes all the apply Edge Filter MenuItems and creates them from scratch
	 * 
	 * @param newCondition
	 *            a condition which should be applied immediately ( can be null,
	 *            so no condition is applied)
	 */
	private void resetApplyEdgeFilters(String newConditon) {
		applyEdgeFilter.removeAll();
		addApplyEdgeFilters(newConditon);
	}

	/**
	 * Removes all the delete Edge Filter MenuItems and creates them from
	 * scratch
	 */
	private void resetDeleteEdgeFilters() {
		deleteEdgeFilter.removeAll();
		addRemoveEdgeFilters();
	}
}
