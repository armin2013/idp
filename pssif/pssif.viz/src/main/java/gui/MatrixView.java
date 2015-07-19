package gui;

import graph.model.MyEdge;
import graph.model.MyNode;
import gui.matrix.MatrixChooseNodeAndEgeTypePopup;
import gui.matrix.RowLegendTable;
import gui.matrix.TableColumnAdjuster;
import gui.matrix.VerticalTableHeaderCellRenderer;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Enumeration;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import matrix.model.ExcelExport;
import matrix.model.MatrixBuilder;

/**
 * Provides the Matrix View of a PSS-IF Model
 * @author Luc
 *
 */
public class MatrixView {

	private JPanel matrixPanel;
	private MatrixBuilder mbuilder;
	private String[][] content;
	private LinkedList<MyNode> nodes;
	private LinkedList<MyEdge> edges;
	private ExcelExport xml_exporter;
	private boolean active;

	/**
	 * creates a new Instance of the MatrixView
	 */
	public MatrixView() {
		this.matrixPanel = new JPanel();
		this.mbuilder = new MatrixBuilder();
		this.xml_exporter = new ExcelExport();
		active = false;	
	}
	
	/**
	 * Builds the Matrix from scratch
	 */
	private void drawMatrix()
	{
		edges = mbuilder.findRelevantEdges();
		nodes=  mbuilder.findRelevantNodes();
		matrixPanel = drawPanels(nodes,edges);
	}
	
	/**
	 * Creates the Matrix Visualization
	 * @param nodes the Nodes which should be contained in the matrix (the labels)
	 * @param edges the Edges which should be contained in the matrix (the values of the matrix)
	 * @return a JPanel which contains the Matrix Visualization
	 */
	private JPanel drawPanels(LinkedList<MyNode> nodes, LinkedList<MyEdge> edges)
	{
		JPanel Content = new JPanel(new BorderLayout());
		createMatrixContent(Content, nodes, edges);
		
		return Content;
	}
	
	/**
	 * Add a Matrix to a given JPanel. The labels are the Nodes and the Edges are the content 
	 * @param p the JPanel with the labels(Nodes)
	 * @param nodes the labels of the Matrix
	 * @param edges the content of the Matrix
	 */
	private void createMatrixContent (JPanel p, LinkedList<MyNode> nodes, LinkedList<MyEdge> edges)
	{
		if (nodes.size()>0 && edges.size()>0)
		{
			String[] legend = new String[nodes.size()];
			
			int counter =0;
			// create Legend
			for (MyNode n : nodes)
			{
				legend[counter] = n.getName();
				counter++;				
			}
			
			content = mbuilder.getEdgeConnections(nodes, edges);


			JTable mainTable = new JTable(content,legend);
			JScrollPane scrollPane = new JScrollPane(mainTable);
			
			JTable rowTable = new RowLegendTable(mainTable);
			
			scrollPane.setRowHeaderView(rowTable);
			
			mainTable.setEnabled(false);


			TableCellRenderer headerRenderer = new VerticalTableHeaderCellRenderer();
		    Enumeration<TableColumn> columns = mainTable.getColumnModel().getColumns();
		      while (columns.hasMoreElements())
		      {
		        TableColumn tc = (TableColumn)columns.nextElement();
		        tc.setHeaderRenderer(headerRenderer);
		      }
			
			mainTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			TableColumnAdjuster tca = new TableColumnAdjuster(mainTable);
			tca.adjustColumns();
				
			p.add(scrollPane, BorderLayout.CENTER);
			
		}
	}
	
	/**
	 * Get a Visualization of the current PSS-IF Model, in a Matrix form
	 * @return a Panel with the Matrix and a Excel Export Button
	 */
	public JPanel getVisualization()
	{
		boolean display = true;
		
		// check if the user did already select correctly some Edge and NodeTypes which should be displayed in the Matrix
		if (mbuilder.getRelevantNodeTypes()==null || mbuilder.getRelevantEdgesTypes() ==null ||
				(mbuilder.getRelevantNodeTypes()!=null && mbuilder.getRelevantNodeTypes().size()==0) ||
				(mbuilder.getRelevantEdgesTypes() !=null && mbuilder.getRelevantEdgesTypes().size()==0))
			display =chooseEdgesAndNodes();
		
		if (display)
		{
			drawMatrix();
			exportButton(this.matrixPanel);
			return matrixPanel;
		}
		else
		{
			// create an information Panel
			JPanel allPanel = new JPanel(new GridBagLayout());
			allPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
			JLabel label = new JLabel("Please select a least one EdgeType and one NodeType");
			label.setFont(new Font("Arial", Font.ITALIC, 25));
			allPanel.add(label);
			return allPanel;
		}	
	}
	
	/**
	 * Let the user choose which instance of Edge and NodeTypes should be displayed in the Matrix
	 * @return true if the user did select a valid selection, false if not (e.g. no NodeType or EdgeType selected)
	 */
	public boolean chooseEdgesAndNodes()
	{
		MatrixChooseNodeAndEgeTypePopup popup = new MatrixChooseNodeAndEgeTypePopup(mbuilder);
		
		return popup.showPopup();
	}
	
	/**
	 * Create the Excel export Button
	 * @param panel the Panel to which the Button should be added
	 */
	private void exportButton(JPanel panel)
	  {
	    JPanel optionPanel = new JPanel();
		JButton button = new JButton("Export Matrix");
	    
	    button.addActionListener(new ActionListener()
	    {
	      public void actionPerformed(ActionEvent e)
	      {
	        JFileChooser saveFile = new JFileChooser();
	        saveFile.setSelectedFile(new File(System.getProperty("user.home") + "\\matrix_export.xls"));
	        saveFile.setDialogTitle("Select the file location");
	        
	        int returnVal = saveFile.showSaveDialog(null);
	        if (returnVal == JFileChooser.APPROVE_OPTION)
	        {
	          File file = saveFile.getSelectedFile();
	          MatrixView.this.xml_exporter.createXMLExport(content, nodes, file);
	        }
	      }
	    });
	    optionPanel.add(button);
	    
	    panel.add(optionPanel, BorderLayout.EAST);
	  }
	
	/**
	 * Check if currently the MatrixView is displayed to the user
	 * @return true if it is displayed, else false
	 */
	public boolean isActive() {
		return active;
	}
	
	/**
	 * Change the active status of  the MatrixView
	 * @param active is the MatrixView currently active or not
	 */
	public void setActive(boolean active) {
		this.active = active;
	}
	
}
