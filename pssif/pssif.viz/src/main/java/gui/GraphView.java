package gui;

import de.tum.pssif.core.common.PSSIFConstants;
import de.tum.pssif.transform.mapper.db.PssifToDBMapperImpl;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.picking.PickedState;
import graph.model.IMyNode;
import graph.model.MyEdge;
import graph.model.MyEdgeType;
import graph.model.MyNode;
import graph.model.MyNodeType;
import graph.operations.MasterFilter;
import gui.graph.GraphVisualization;
import gui.toolbars.NodeHierarchyContainer;
import gui.toolbars.ToolbarManager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.LinkedList;
import java.util.Set;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import model.ModelBuilder;

/**
 * Provides the Graph View of a PSS-IF Model
 * 
 * @author Luc
 *
 */
public class GraphView {
	private JPanel parent;
	private GraphVisualization graph;

	private JPanel nodeInformationPanel;
	private JPanel edgeInformationPanel;
	private JPanel nodeAttributePanel;
	private JPanel edgeAttributePanel;
	private JPanel informationPanel;
	private JLabel nodename;
	private JLabel nodetype;
	private JLabel edgetype;
	private JLabel edgeSource;
	private JLabel edgeDestination;
	private JCheckBox nodeDetails;
	private JButton collapseExpand;
	private boolean active;
	private JTable tableNodeAttributes;
	private DefaultTableModel nodeAttributesModel;
	private JTable tableEdgeAttributes;
	private DefaultTableModel edgeAttributesModel;
	private ButtonGroup group;
	private JRadioButton nodeSelection;
	private JRadioButton edgeSelection;
	private ItemListener nodeListener;
	private ItemListener edgeListener;
	private JSpinner depthSpinner;
	private JSplitPane 		   spliter;
	private JPanel			   showPanel;
	private JScrollPane		   hierachypane;

	private Dimension screenSize;
	private static int betweenComps = 5;
	private static Color bgColor = Color.WHITE;

	
	private MainFrame mainFrame;
	private MasterFilter masterFilter;
	
	
	

	
	/**
	 * Create a new instance of a GraphView
	 */
	public GraphView(MainFrame mainFrame) {
		
		this.mainFrame = mainFrame;
		
		
		active = false;

		screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (screenSize.width);
		int y = (int) (screenSize.height * 0.78);
		if (nodeDetails == null) {
			graph = new GraphVisualization(new Dimension(x, y), true);
		} else {
			graph = new GraphVisualization(new Dimension(x, y),
					nodeDetails.isSelected());
		}

		addNodeChangeListener();
		
		masterFilter = new MasterFilter(this);
	}

	/**
	 * Get the Graph Visualization
	 * 
	 * @return a Panel with the Graph and an additional Information Panel
	 */
	public JPanel getGraphPanel() {
		parent = new JPanel();
	    parent.setLayout(new BorderLayout());
		spliter = new JSplitPane(JSplitPane.VERTICAL_SPLIT ,addGraphViz(), addInformationPanel());
		spliter.setOneTouchExpandable(true);
		spliter.setDividerLocation(520);
		parent.add(spliter, BorderLayout.CENTER);

		final NodeHierarchyContainer nhcp = new NodeHierarchyContainer();
	    hierachypane = nhcp.createNodeHierarchyTree(masterFilter);
	    parent.add(hierachypane, BorderLayout.EAST);
		
	    ToolbarManager toolbarManager = new ToolbarManager();
	    parent.add(toolbarManager.createMouseToolbar(graph), BorderLayout.LINE_START);
	    parent.add(toolbarManager.createStandardToolBar(mainFrame.getFileCommands()), BorderLayout.PAGE_START);
	        
	    return parent;
	}

	/**
	 * Get the Graph Visualization as a panel
	 * 
	 * @return a Panel with the Graph Visualization
	 */
	private JPanel addGraphViz() {
		JPanel graphpanel = new JPanel();

		VisualizationViewer<IMyNode, MyEdge> vv = graph
				.getVisualisationViewer();

		graphpanel.add(vv);
		
		showPanel = new JPanel();
	    JButton showb = new JButton("Show Information Panel");
	    showb.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				informationPanel.setVisible(true);
				showPanel.setVisible(false);
				spliter.setDividerLocation(0.75);
				//spliter.revalidate();
				
			}
		});
	    showPanel.add(showb);
	    showPanel.setVisible(false);
	    graphpanel.add(showPanel, BorderLayout.SOUTH);
		
		return graphpanel;
	}

	  /**
	   * Get an Information Panel ( Additional information about the currently selected Edge or Node) as a Panel
	   * @return a Panel with the Information Panel
	   */
	  private JPanel addInformationPanel() {
	    informationPanel = new JPanel();

		//informationPanel = new EnhancedToolBar(0);

	    int x = (screenSize.width);
	    int y = (int) (screenSize.height * 0.19);
	    Dimension d = new Dimension(x, y);

	    informationPanel.setMaximumSize(d);
	    informationPanel.setMinimumSize(d);
	    informationPanel.setPreferredSize(d);

	    informationPanel.setLayout(new BorderLayout());

	    // Basic Graph operations
	    informationPanel.add(addBasicOperationPanel(), BorderLayout.EAST);

	    // Attributes
	    nodeAttributePanel = addNodeAttributePanel(x, y);
	    edgeAttributePanel = addEdgeAttributePanel(x, y);

	    informationPanel.add(nodeAttributePanel, BorderLayout.CENTER);

	    // Basic information
	    nodeInformationPanel = addNodeInformationPanel();
	    edgeInformationPanel = addEdgeInformationPanel();

	    // as default add the Node Information Panel
	    informationPanel.add(nodeInformationPanel, BorderLayout.WEST);
	    nodeSelection.setSelected(true);

	    return informationPanel;
	  }
	  
	  /**
	   * Build the Node Information Panel (Name and Type of the selected Node)
	   * @return a Panel which holds the requested Information
	   */
	  private JPanel addNodeInformationPanel() {
	    GridBagConstraints c = new GridBagConstraints();
	    int ypos = -1;

	    // selected Node Information
	    JPanel nodeInfos = new JPanel();
	    nodeInfos.setLayout(new GridBagLayout());
	    nodeInfos.setBackground(bgColor);

	    c.gridx = 0;
	    JLabel lblNodeName = new JLabel("Node Name:");
	    lblNodeName.setFont(new Font("Serif", Font.PLAIN, 12));
	    ypos++;
	    c.gridy = ypos;
	    nodeInfos.add(lblNodeName, c);

	    nodename = new JLabel("");
	    ypos++;
	    c.gridy = ypos;
	    nodeInfos.add(nodename, c);

	    ypos++;
	    //c.gridy = ypos;
	    //nodeInfos.add(Box.createVerticalStrut(betweenComps), c);

	    JLabel lblNodeType = new JLabel("Node Type:");
	    lblNodeType.setFont(new Font("Serif", Font.PLAIN, 12));
	    ypos++;
	    c.gridy = ypos;
	    nodeInfos.add(lblNodeType, c);
	    nodetype = new JLabel("");
	    ypos++;
	    c.gridy = ypos;
	    nodeInfos.add(nodetype, c);

	    c.gridx = 1;
	    c.gridheight = ypos;
	    nodeInfos.add(Box.createHorizontalStrut(10), c);

	    return nodeInfos;
	  }

	  /**
	   * Build the Edge Information Panel (Type, Source and Destination of the selected Edge)
	   * @return a Panel which holds the requested Information
	   */
	  private JPanel addEdgeInformationPanel() {
	    GridBagConstraints c = new GridBagConstraints();
	    int ypos = -1;

	    // selected Edge Information
	    JPanel edgeInfos = new JPanel();
	    edgeInfos.setLayout(new GridBagLayout());
	    edgeInfos.setBackground(bgColor);

	    c.gridx = 0;

	    JLabel lblEdgeType = new JLabel("Edge Type:");
	    lblEdgeType.setFont(new Font("Serif", Font.PLAIN, 12));
	    ypos++;
	    c.gridy = ypos;
	    edgeInfos.add(lblEdgeType, c);
	    edgetype = new JLabel("");
	    ypos++;
	    c.gridy = ypos;
	    edgeInfos.add(edgetype, c);

	    ypos++;
	    //c.gridy = ypos;
	    //edgeInfos.add(Box.createVerticalStrut(betweenComps), c);

	    JLabel lblsource = new JLabel("Source:");
	    lblsource.setFont(new Font("Serif", Font.PLAIN, 12));
	    ypos++;
	    c.gridy = ypos;
	    edgeInfos.add(lblsource, c);
	    edgeSource = new JLabel("");
	    ypos++;
	    c.gridy = ypos;
	    edgeInfos.add(edgeSource, c);

	    ypos++;
	    //c.gridy = ypos;
	    //edgeInfos.add(Box.createVerticalStrut(betweenComps), c);

	    JLabel lbldestination = new JLabel("Destination:");
	    lbldestination.setFont(new Font("Serif", Font.PLAIN, 12));
	    ypos++;
	    c.gridy = ypos;
	    edgeInfos.add(lbldestination, c);
	    edgeDestination = new JLabel("");
	    ypos++;
	    c.gridy = ypos;
	    edgeInfos.add(edgeDestination, c);

	    c.gridx = 1;
	    c.gridheight = ypos;
	    edgeInfos.add(Box.createHorizontalStrut(10), c);

	    return edgeInfos;
	  }

	  /**
	   * Build a Panel which allows the user to display all the Node Attributes and allows the user to alter them
	   * @param sizeX The width of the parent Panel
	   * @param sizeY The height of the parent Panel
	   * @return a Panel which holds the requested Information
	   */
	  private JPanel addNodeAttributePanel(int sizeX, int sizeY) {
	    JPanel attributeInfos = new JPanel();
	    attributeInfos.setLayout(new BorderLayout());
	    attributeInfos.setBackground(bgColor);

	    JLabel lblNodeAttributes = new JLabel("Node Attributes");

	    attributeInfos.add(lblNodeAttributes, BorderLayout.NORTH);

	    int scrollx = (int) (sizeX * 0.8);
	    int scrolly = (int) (sizeY * 0.9);

	    JScrollPane jScrollPane = new JScrollPane(createNodeAttributTable());
	    jScrollPane.setBackground(bgColor);
	    jScrollPane.setPreferredSize(new Dimension(scrollx, scrolly));
	    jScrollPane.setMaximumSize(new Dimension(scrollx, scrolly));
	    jScrollPane.setMinimumSize(new Dimension(scrollx, scrolly));

	    attributeInfos.add(jScrollPane, BorderLayout.CENTER);

	    return attributeInfos;
	  }

	  /**
	   * Build a Panel which allows the user to display all the Edge Attributes and allows the user to alter them
	   * @param sizeX The width of the parent Panel
	   * @param sizeY The height of the parent Panel
	   * @return a Panel which holds the requested Information
	   */
	  private JPanel addEdgeAttributePanel(int sizeX, int sizeY) {
	    JPanel attributeInfos = new JPanel();
	    attributeInfos.setLayout(new BorderLayout());
	    attributeInfos.setBackground(bgColor);

	    JLabel lblEdgeAttributes = new JLabel("Edge Attributes");

	    attributeInfos.add(lblEdgeAttributes, BorderLayout.NORTH);

	    int scrollx = (int) (sizeX * 0.8);
	    int scrolly = (int) (sizeY * 0.9);

	    JScrollPane jScrollPane = new JScrollPane(createEdgeAttributTable());
	    jScrollPane.setBackground(bgColor);
	    jScrollPane.setPreferredSize(new Dimension(scrollx, scrolly));
	    jScrollPane.setMaximumSize(new Dimension(scrollx, scrolly));
	    jScrollPane.setMinimumSize(new Dimension(scrollx, scrolly));

	    attributeInfos.add(jScrollPane, BorderLayout.CENTER);

	    return attributeInfos;
	  }
	  
	  /**
	   * Build the a Panel which holds very basic but frequently used Graph operations
	   * @return a Panel which holds the requested Information
	   */
	  private JPanel addBasicOperationPanel() {
	    GridBagConstraints c = new GridBagConstraints();
	    int ypos = -1;

	    JPanel basicOperations = new JPanel();
	    basicOperations.setLayout(new GridBagLayout());
	    basicOperations.setBackground(bgColor);

	    JLabel pickMode = new JLabel("Pick Mode: ");
	    pickMode.setFont( new Font("Serif", Font.ITALIC, 12));

	    edgeSelection = new JRadioButton("Edge");
	    edgeSelection.setBackground(bgColor);
	    edgeSelection.addActionListener(new ActionListener() {

	      @Override
	      public void actionPerformed(ActionEvent e) {
	        JRadioButton parent = (JRadioButton) e.getSource();
	        if (parent.isSelected()) {
	          addEdgeChangeListener();

	          informationPanel.remove(nodeAttributePanel);
	          informationPanel.remove(nodeInformationPanel);
	          informationPanel.add(edgeAttributePanel, BorderLayout.CENTER);
	          informationPanel.add(edgeInformationPanel, BorderLayout.WEST);

	          informationPanel.validate();
	          informationPanel.revalidate();
	          informationPanel.repaint();

	          parent.validate();
	          parent.revalidate();
	          parent.repaint();
	        }
	      }
	    });
	    nodeSelection = new JRadioButton("Node");
	    nodeSelection.setBackground(bgColor);
	    nodeSelection.addActionListener(new ActionListener() {

	      @Override
	      public void actionPerformed(ActionEvent e) {

	        JRadioButton parent = (JRadioButton) e.getSource();
	        if (parent.isSelected()) {
	          addNodeChangeListener();

	          informationPanel.remove(edgeAttributePanel);
	          informationPanel.remove(edgeInformationPanel);
	          informationPanel.add(nodeAttributePanel, BorderLayout.CENTER);
	          informationPanel.add(nodeInformationPanel, BorderLayout.WEST);

	          informationPanel.validate();
	          informationPanel.revalidate();
	          informationPanel.repaint();

	          parent.validate();
	          parent.revalidate();
	          parent.repaint();
	        }
	      }
	    });

	    group = new ButtonGroup();
	    group.add(edgeSelection);
	    group.add(nodeSelection);

	    
	    //adding minimize button
	    JButton hideButton = new JButton("Hide Information Panel");
	    hideButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				informationPanel.setVisible(false);
				showPanel.setVisible(true);
			}
		});
	    
	 
	    
	    
	    ypos++;
	    c.gridy = ypos;
	    c.gridx = 1;
	    basicOperations.add(Box.createVerticalStrut(betweenComps), c);
	    basicOperations.add(hideButton, c);
	    
	    ypos++;
	    c.gridy = ypos;
	    c.gridx = 1;
	    JPanel pickPanel = new JPanel(new BorderLayout());
	    pickPanel.setBackground(bgColor);
	    pickPanel.add(pickMode, BorderLayout.LINE_START);
	    pickPanel.add(edgeSelection, BorderLayout.CENTER);
	    pickPanel.add(nodeSelection, BorderLayout.LINE_END);

	    basicOperations.add(pickPanel, c);

	    ypos++;
	    c.gridy = ypos;

	    ypos++;
	    c.gridy = ypos;
	    nodeDetails = new JCheckBox("Node Details");
	    nodeDetails.setBackground(bgColor);
	    ypos++;
	    c.gridy = ypos;
	    basicOperations.add(nodeDetails, c);
	    nodeDetails.setSelected(false);
	    graph.setNodeVisualisation(false);
	    nodeDetails.addItemListener(new ItemListener() {
	      @Override
	      public void itemStateChanged(ItemEvent item) {
	        JCheckBox parent = (JCheckBox) item.getSource();
	        if (parent.isSelected()) {
	          // checkbox selected
	          graph.setNodeVisualisation(true);
	        }
	        else {
	          // checkbox not selected
	          graph.setNodeVisualisation(false);
	        }
	      }
	    });
	    ypos++;
	    c.gridy = ypos;
	    

	    
	    JPanel spinPanel = new JPanel();
	    JLabel lblDepthSpinner = new JLabel("Search Depth");
	    spinPanel.add(lblDepthSpinner, BorderLayout.WEST);
	    int currentDepth = 1;
	    SpinnerModel depthModel = new SpinnerNumberModel(currentDepth, //initial value
	        1, //min
	        currentDepth + 100, //max
	        1);
	    depthSpinner = new JSpinner(depthModel);

	    depthSpinner.addChangeListener(new ChangeListener() {
	      @Override
	      public void stateChanged(ChangeEvent e) {

	        Object value = ((JSpinner) e.getSource()).getValue();
	        if (value instanceof Integer) {
	          int depth = (Integer) value;
	          graph.setFollowEdgeTypes(depth);
	        }

	      }
	    });
	    
	    ypos++;
	    c.gridy = ypos;
	    spinPanel.add(depthSpinner, BorderLayout.EAST);
	    basicOperations.add(spinPanel, c);
	    
	    ypos++;
	    c.gridy = ypos;

	    collapseExpand = new JButton("Collapse/Expand Node");
	    collapseExpand.setEnabled(false);
	    collapseExpand.addActionListener(new ActionListener() {
	      @Override
	      public void actionPerformed(ActionEvent e) {
	        if (graph.isExpandable()) {
	          graph.expandNode(nodeDetails.isSelected());
	          collapseExpand.setText("Collapse Node");
	        }

	        if (graph.isCollapsable()) {
	          graph.collapseNode();
	          collapseExpand.setText("Expand Node");
	        }
	      }
	    });
	    ypos++;
	    c.gridy = ypos;
	    basicOperations.add(collapseExpand, c);
	    basicOperations.add(Box.createVerticalStrut(betweenComps), c);
	    
	    c.gridx = 0;
	    c.gridheight = ypos;
	    c.gridwidth = 1;
	    basicOperations.add(Box.createHorizontalStrut(10), c);

	    return basicOperations;
	  }

	  /**
	   * Build a Table which displays the Node Attributes
	   * @return  the Table which displays the Node Attributes
	   */
	  @SuppressWarnings("serial")
	private JTable createNodeAttributTable() {
	    nodeAttributesModel = new DefaultTableModel() {

	      @Override
	      public boolean isCellEditable(int rowIndex, int columnIndex) {
	        if (columnIndex == 0 || columnIndex == 2 || columnIndex == 3) {
	          return false;
	        }
	        else {
	          return true;
	        }
	      }
	    };
	    nodeAttributesModel.addColumn("Attribute");
	    nodeAttributesModel.addColumn("Value");
	    nodeAttributesModel.addColumn("Unit");
	    nodeAttributesModel.addColumn("Type");

	    tableNodeAttributes = new JTable(nodeAttributesModel);
	    tableNodeAttributes.getColumnModel().getColumn(0).setMaxWidth(100);
	    tableNodeAttributes.getColumnModel().getColumn(2).setMaxWidth(60);
	    tableNodeAttributes.getColumnModel().getColumn(3).setMaxWidth(100);
	    
	    tableNodeAttributes.getModel().addTableModelListener(new TableModelListener() {

	      @Override
	      public void tableChanged(TableModelEvent e) {
	        if (e.getType() == TableModelEvent.UPDATE) {
	          int row = e.getFirstRow();
	          int column = e.getColumn();
	          TableModel model = (TableModel) e.getSource();

	          Object data = model.getValueAt(row, column);
	          String attributeName = (String) model.getValueAt(row, 0);

	          // get the data to test if there was some data entered
	          String testdata = (String) data;
	          // do not trigger the event again if we previously wrote null back
	          if (data != null && testdata.length() > 0) {
	            Set<IMyNode> selectedNodes = graph.getVisualisationViewer().getPickedVertexState().getPicked();
	            if (!selectedNodes.isEmpty() && selectedNodes.size() == 1) {
	            	IMyNode selectedNode = selectedNodes.iterator().next();
	            	
	            	if (selectedNode instanceof MyNode)
	            	{
		            	MyNode node = (MyNode)selectedNode;
	            		boolean res = node.updateAttribute(attributeName, data);
						
						// If node should be saved to DB
						PssifToDBMapperImpl.changedNodes.add(node);
						
	            		if (!res){ 
	            			model.setValueAt(null, row, column);
			                JPanel errorPanel = new JPanel();
			
			                errorPanel.add(new JLabel("The value does not match the attribute data type"));
			
			                JOptionPane.showMessageDialog(null, errorPanel, "Ups something went wrong", JOptionPane.ERROR_MESSAGE);
	            		} //else 
	            			//graph.updateGraph();
	            	}
	            }
	          }
	        }
	      }
	    });

	    return tableNodeAttributes;
	  }

	  /**
	   * Build a Table which displays the Edge Attributes
	   * @return  the Table which displays the Edge Attributes
	   */
	  @SuppressWarnings("serial")
	private JTable createEdgeAttributTable() {
	    edgeAttributesModel = new DefaultTableModel() {

	      @Override
	      public boolean isCellEditable(int rowIndex, int columnIndex) {
	        if (columnIndex == 0 || columnIndex == 2 || columnIndex == 3) {
	          return false;
	        }
	        else {
	          return true;
	        }
	      }
	    };
	    edgeAttributesModel.addColumn("Attribute");
	    edgeAttributesModel.addColumn("Value");
	    edgeAttributesModel.addColumn("Unit");
	    edgeAttributesModel.addColumn("Type");

	    tableEdgeAttributes = new JTable(edgeAttributesModel);
	    tableEdgeAttributes.getColumnModel().getColumn(0).setMaxWidth(100);
	    tableEdgeAttributes.getColumnModel().getColumn(2).setMaxWidth(60);
	    tableEdgeAttributes.getColumnModel().getColumn(3).setMaxWidth(100);
	    tableEdgeAttributes.getModel().addTableModelListener(new TableModelListener() {

	      @Override
	      public void tableChanged(TableModelEvent e) {
	        if (e.getType() == TableModelEvent.UPDATE) {
	          int row = e.getFirstRow();
	          int column = e.getColumn();
	          TableModel model = (TableModel) e.getSource();

	          Object data = model.getValueAt(row, column);
	          String attributeName = (String) model.getValueAt(row, 0);

	          // get the data to test if there was some data entered
	          String testdata = (String) data;
	          // do not trigger the event again if we previously wrote null back
	          if (data != null && testdata.length() > 0) {
	            Set<MyEdge> selectedEdges = graph.getVisualisationViewer().getPickedEdgeState().getPicked();
	            if (!selectedEdges.isEmpty() && selectedEdges.size() == 1) {
	              MyEdge selectedEdge = selectedEdges.iterator().next();

	              boolean res = selectedEdge.updateAttribute(attributeName, data);
				  
				// If edge should be saved to DB
				PssifToDBMapperImpl.changedEdges.add(selectedEdge);
				  
	              //directed  attr changed
	              if (attributeName.equals(PSSIFConstants.BUILTIN_ATTRIBUTE_DIRECTED))
	              {
	            	  graph.updateGraph();
	              }
	              if (!res) {
	                model.setValueAt(null, row, column);
	                JPanel errorPanel = new JPanel();

	                errorPanel.add(new JLabel("The value does not match the attribute data type"));

	                JOptionPane.showMessageDialog(null, errorPanel, "Ups something went wrong", JOptionPane.ERROR_MESSAGE);
	              }
	            }
	          }
	        }
	      }
	    });

	    return tableEdgeAttributes;
	  }
	  
	  /**
	   * Get the Graph
	   * @return the Jung2 Graph, but with own features
	   */
	  public GraphVisualization getGraph() {
	    return graph;
	  }
	  
	  /**
	   * Display an empty Graph
	   */
	  public void resetGraph() {
	    ModelBuilder.resetModel();
	    this.getGraph().updateGraph();
	  }
	  
	  /**
	   * Update the the Information panel after a Node change
	   * @param nodeName the name of the currently selected Node
	   * @param nodeType the type of the currently selected Node
	   * @param attributes the currently selected Nodes attributes
	   */
	  private void updateNodeSidebar(String nodeName, MyNodeType nodeType, LinkedList<LinkedList<String>> attributes) {

	    if (nodeName == null) {
	      this.nodename.setText("");
	    }
	    else {
	      this.nodename.setText(nodeName);
	    }
	    if (nodeType == null) {
	      this.nodetype.setText("");
	    }
	    else {
	      this.nodetype.setText(nodeType.getName());
	    }
	    if (attributes == null) {
	      for (int i = 0; i < nodeAttributesModel.getRowCount(); i++) {
	        nodeAttributesModel.removeRow(i);
	      }
	      nodeAttributesModel.setRowCount(0);
	    }
	    else {
	      for (int i = 0; i < nodeAttributesModel.getRowCount(); i++) {
	        nodeAttributesModel.removeRow(i);
	      }
	      nodeAttributesModel.setRowCount(0);

	      for (LinkedList<String> currentAttr : attributes) {
	        String name = currentAttr.get(0);
	        String value = currentAttr.get(1);
	        String unit = currentAttr.get(2);
	        String type = currentAttr.get(3);
	        nodeAttributesModel.addRow(new String[] { name, value, unit, type });
	      }
	    }

	    if (graph.isCollapsable()) {
	      collapseExpand.setText("Collapse Node");
	    }

	    if (graph.isExpandable()) {
	      collapseExpand.setText("Expand Node");
	    }

	    if (!graph.isCollapsable() && !graph.isExpandable()) {
	      collapseExpand.setText("Collapse/Expand Node");
	      collapseExpand.setEnabled(false);
	    }
	    else {
	      collapseExpand.setEnabled(true);
	    }

	  }
	  
	  /**
	   * Update the the Information panel after a Edge change
	   * @param edge the currently selected Edge
	   * @param edgeType the type of the currently selected Edge
	   * @param attributes the currently selected Edges attributes
	   */
	  private void updateEdgeSidebar(MyEdge edge, MyEdgeType edgeType, LinkedList<LinkedList<String>> attributes) {
	    if (edge == null) {
	      this.edgeSource.setText("");
	      this.edgeDestination.setText("");
	    }
	    else {
	      this.edgeSource.setText(edge.getSourceNode().getRealName());
	      this.edgeDestination.setText(edge.getDestinationNode().getRealName());
	    }

	    if (edgeType == null) {
	      this.edgetype.setText("");
	    }
	    else {
	      this.edgetype.setText(edgeType.getName());
	    }
	    if (attributes == null) {
	      for (int i = 0; i < edgeAttributesModel.getRowCount(); i++) {
	        edgeAttributesModel.removeRow(i);
	      }
	      edgeAttributesModel.setRowCount(0);
	    }
	    else {
	      for (int i = 0; i < edgeAttributesModel.getRowCount(); i++) {
	        edgeAttributesModel.removeRow(i);
	      }
	      edgeAttributesModel.setRowCount(0);

	      for (LinkedList<String> currentAttr : attributes) {
	        String name = currentAttr.get(0);
	        String value = currentAttr.get(1);
	        String unit = currentAttr.get(2);
	        String type = currentAttr.get(3);
	        edgeAttributesModel.addRow(new String[] { name, value, unit, type });
	      }
	    }

	    if (graph.isCollapsable()) {
	      collapseExpand.setText("Collapse Node");
	    }

	    if (graph.isExpandable()) {
	      collapseExpand.setText("Expand Node");
	    }

	    if (!graph.isCollapsable() && !graph.isExpandable()) {
	      collapseExpand.setText("Collapse/Expand Node");
	      collapseExpand.setEnabled(false);
	    }
	    else {
	      collapseExpand.setEnabled(true);
	    }

	  }
	  
	  /**
	   * Add Listener to the Graph, which allows to trace every Node Selection change. Triggers the update on the Information Panel 
	   */
	  private void addNodeChangeListener() {
	    PickedState<IMyNode> pickedState = graph.getVisualisationViewer().getPickedVertexState();

	    if (edgeListener != null) {
	      pickedState.removeItemListener(edgeListener);
	    }

	    nodeListener = new ItemListener() {

	      @Override
	      public void itemStateChanged(ItemEvent e) {
	        @SuppressWarnings("unchecked")
	        PickedState<MyNode> pi = (PickedState<MyNode>) e.getSource();
	        Object subject = e.getItem();

	        if (subject instanceof MyNode) {
	          MyNode vertex = (MyNode) subject;
	          if (pi.isPicked(vertex)) {
	            updateNodeSidebar(vertex.getName(), vertex.getNodeType(), vertex.getAttributes());
	          }
	          else {
	            updateNodeSidebar(null, null, null);
	          }
	        }
	        else {
	          updateNodeSidebar(null, null, null);
	        }

	      }
	    };

	    pickedState.addItemListener(nodeListener);

	    Set<IMyNode> s = pickedState.getPicked();
	    if (s == null) {
	      updateNodeSidebar(null, null, null);
	    }
	  }
	  
	  /**
	   * Add Listener to the Graph, which allows to trace every Edge Selection change. Triggers the update on the Information Panel 
	   */
	  private void addEdgeChangeListener() {
	    PickedState<MyEdge> pickedState = graph.getVisualisationViewer().getPickedEdgeState();
	    if (nodeListener != null) {
	      pickedState.removeItemListener(nodeListener);
	    }

	    edgeListener = new ItemListener() {

	      @Override
	      public void itemStateChanged(ItemEvent e) {
	        @SuppressWarnings("unchecked")
	        PickedState<MyEdge> pi = (PickedState<MyEdge>) e.getSource();
	        Object subject = e.getItem();

	        if (subject instanceof MyEdge) {
	          MyEdge edge = (MyEdge) subject;
	          if (pi.isPicked(edge)) {
	            updateEdgeSidebar(edge, edge.getEdgeType(), edge.getAttributes());
	          }
	          else {
	            updateEdgeSidebar(null, null, null);
	          }
	        }
	        else {
	          updateEdgeSidebar(null, null, null);
	        }

	      }
	    };
	    pickedState.addItemListener(edgeListener);
	    Set<MyEdge> s = pickedState.getPicked();
	    if (s == null) {
	      updateEdgeSidebar(null, null, null);
	    }
	  }
	  
		/**
		 * Check if currently the GraphView is displayed to the user
		 * @return true if it is displayed, else false
		 */
	  public boolean isActive() {
	    return active;
	  }

		/**
		 * Change the active status of  the GraphView
		 * @param active is the GraphView currently active or not
		 */
	  public void setActive(boolean active) {
	    this.active = active;
	  }
	  
	  /**
	   * Set the value of the Search Depth Spinner on the Information Panel
	   * @param value the depth value to which the spinner should be set
	   */
	  public void setDepthSpinnerValue(int value) {
	    if (value > 0 && value < 101 && depthSpinner != null) {
	      depthSpinner.getModel().setValue(value);
	    }
	  }
	  
	  /**
	   * Get the value of the Search Depth Spinner on the Information Panel
	   * @return 1 if the Spinner is not initialized, otherwise the value of the Spinner
	   */
	  public Integer getDepthSpinnerValue() {
		if (depthSpinner!=null)
		{
			return (Integer) depthSpinner.getModel().getValue();
		}
		else
			return 1;
	  }
	  
	  public MasterFilter getMasterFilter()
	  {
		  return this.masterFilter;
	  }

	}