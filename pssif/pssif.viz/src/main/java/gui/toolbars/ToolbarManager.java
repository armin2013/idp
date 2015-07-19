package gui.toolbars;

import edu.uci.ics.jung.visualization.control.ModalGraphMouse.Mode;
import gui.FileCommands;
import gui.MainFrame;
import gui.enhancement.DraggableButton;
import gui.enhancement.EnhancedVisualizationViewer;
import gui.graph.GraphVisualization;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseWheelEvent;

import javax.swing.ImageIcon;

public class ToolbarManager {
	
	public EnhancedToolBar createMouseToolbar(final GraphVisualization graph)
	{
		
		EnhancedToolBar ebt = new EnhancedToolBar(1);
		ebt.addButton(MainFrame.INSTALL_FOLDER + "//images//buttons//finger3.png", new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			graph.setAbstractModalGraphMode(Mode.PICKING);
		}}, "Picking Mode", true);
		ebt.addButton(MainFrame.INSTALL_FOLDER+ "//images//buttons//pick2.png", new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			graph.setAbstractModalGraphMode(Mode.TRANSFORMING);
			graph.clearPickSupport();
		}}, "Moving Mode", true);
		

		//zoom in 
		ebt.addButton(MainFrame.INSTALL_FOLDER + "//images//buttons//zoom_in3.png", new ActionListener(){ @Override
			public void actionPerformed(ActionEvent e) {
			EnhancedVisualizationViewer ecv = (EnhancedVisualizationViewer) graph.getVisualisationViewer();
			int x = (int) ecv.getCenter().getX();
			int y = (int) ecv.getCenter().getY();
			ecv.getGraphMouse().mouseWheelMoved(new MouseWheelEvent(ecv, 0, 0, 0, 0, 0, x, y, MouseWheelEvent.WHEEL_UNIT_SCROLL
			, true, 1, 1, 1));
			ecv.mouseWheelMoved(1);
		} }, "Zoom In", true);
		
		//zoom out 
		ebt.addButton(MainFrame.INSTALL_FOLDER + "//images//buttons//zoom_out3.png", new ActionListener(){ @Override
			public void actionPerformed(ActionEvent e) {
			EnhancedVisualizationViewer ecv = (EnhancedVisualizationViewer) graph.getVisualisationViewer();
			int x = (int) ecv.getCenter().getX();
			int y = (int) ecv.getCenter().getY();
			ecv.getGraphMouse().mouseWheelMoved(new MouseWheelEvent(ecv, 0, 0, 0, 0, 0, x, y, MouseWheelEvent.WHEEL_UNIT_SCROLL
			, true, 1, -1, -1));
			ecv.mouseWheelMoved(-1);
		} }, "Zoom Out", true);
		
		//Hide/Show Node Details 
		ebt.addButton(MainFrame.INSTALL_FOLDER + "//images//buttons//details.png", new ActionListener(){ @Override
			public void actionPerformed(ActionEvent e) {
			EnhancedVisualizationViewer ecv = (EnhancedVisualizationViewer) graph.getVisualisationViewer();
			graph.flipVertexLabelVisibility();
			graph.updateGraph();
			ecv.repaint();
					} }, "Hide/Show Node Details", true);
		
		ImageIcon img = new ImageIcon(MainFrame.INSTALL_FOLDER + "//images//buttons//node.png");
		img.setImage(img.getImage().getScaledInstance(32, 32, Image.SCALE_DEFAULT));
		DraggableButton newNode = new DraggableButton(img);
		newNode.setToolTipText("Inserting New Node");
		newNode.setBorder(null);
		ebt.add(newNode);
		
		
		ebt.addButton(MainFrame.INSTALL_FOLDER + "//images//buttons//draw.png", new ActionListener(){ @Override
			public void actionPerformed(ActionEvent e) {
			graph.setAbstractModalGraphMode(Mode.EDITING);
					} }, "Edge Drawing Mode", true);
	
		
		return ebt;
	}
	
	/**
	 * Create ToolBar With only new, import from file, and import from DB buttons
	 * 
	 * @return ToolBar
	 */
	public EnhancedToolBar createStandardToolBar(final FileCommands fcommands)
	{
		EnhancedToolBar ebt = new EnhancedToolBar(0);
		//new button
		ebt.addButton(MainFrame.INSTALL_FOLDER + "//images//buttons//newfile.png", new ActionListener(){ @Override
			public void actionPerformed(ActionEvent e) {
			fcommands.createNewFile();
		} }, "New File", true);
		//import from file button
		ebt.addButton(MainFrame.INSTALL_FOLDER + "//images//buttons//importfile.png", new ActionListener(){ @Override
			public void actionPerformed(ActionEvent e) {
			fcommands.importFromFile();
		} }, "Import from File", true);
		//import from DB
		ebt.addButton(MainFrame.INSTALL_FOLDER + "//images//buttons//importdb.png", new ActionListener(){ @Override
			public void actionPerformed(ActionEvent e) {
			fcommands.importFromDB();
		} }, "Import from DB", true);
	
		
		return ebt;
	}

}
