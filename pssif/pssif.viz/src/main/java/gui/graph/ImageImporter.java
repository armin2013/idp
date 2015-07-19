package gui.graph;

 
import graph.model.MyNodeType;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
 

@SuppressWarnings("serial")
public class ImageImporter extends JPanel
                             implements ActionListener {
	
	public static final int IMG_WIDTH = 128;
	public static final int IMG_HEIGHT = 128;
	
    private JButton openButton;
    private JFileChooser fc;
    private File file ;
    private JLabel picLabel = new JLabel();
    private NodeIconPopup nsp;
    private MyNodeType currentNode;
    private JPanel buttonPanel = new JPanel();
    
    public ImageImporter(NodeIconPopup nsp) {
         
        this.nsp = nsp;
        //Create a file chooser
        fc = new JFileChooser();
 
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
 
        
        setLayout(new BorderLayout());
        openButton = new JButton("Select an Image File...");
        openButton.addActionListener(this);
        
        JButton remButton = new JButton("Remove Icon");
        remButton.addActionListener(this);
 
        //For layout purposes, put the buttons in a separate panel
         //use FlowLayout
        buttonPanel.add(openButton);
        buttonPanel.add(remButton);
       
        //Add the buttons and the log to this panel.
        add(buttonPanel, BorderLayout.PAGE_START);
   //     add(logScrollPane, BorderLayout.CENTER);

    }
 
    public void actionPerformed(ActionEvent e) {
    	  //Handle open button action.
        if (e.getSource() == openButton) {
        	handleShapeMapping();
        } 
        else 
        {
        	nsp.getIconMapper().remove(currentNode);
        	this.wipeImage();
        }

    }
    
    public void setCurrentNode(MyNodeType node){
    	this.currentNode = node;
    }
 
    public void handleShapeMapping(){ 
    	
       	// Get array of available formats
        	String[] suffices = ImageIO.getReaderFileSuffixes();

        	// Add a file filter for each one
        	for (int i = 0; i < suffices.length; i++) {
        		FileNameExtensionFilter filter = new FileNameExtensionFilter(suffices[i] + " Files", suffices[i]);
        	    fc.addChoosableFileFilter(filter);
        	}

            int returnVal = fc.showOpenDialog(this);
 
            if (returnVal == JFileChooser.APPROVE_OPTION) {
           		file = fc.getSelectedFile();
           		if (picLabel != null)
           			this.remove(picLabel);
    			ImageIcon icon = loadImageBySize(file, IMG_WIDTH, IMG_HEIGHT);
    			icon.setDescription(file.getPath());
               	nsp.getIconMapper().put(currentNode, icon);
               	this.showImage(icon);
            } 
  
        
    }
    
    public static ImageIcon loadImageBySize(File imgFile, int width, int height){
    	ImageIcon icon = null;
    	try{
    		BufferedImage originalImage = ImageIO.read(imgFile);
    		int type = originalImage.getType() == 0? BufferedImage.TYPE_INT_ARGB : originalImage.getType();     
		
    		BufferedImage resizedImage = new BufferedImage(width, height, type);
    		Graphics2D g = resizedImage.createGraphics();
    		g.drawImage(originalImage, 0, 0, width, height, null);
    		g.dispose();
	 
    		icon = new ImageIcon(resizedImage);
    	}
        catch(Exception e)
        {
        	System.out.println("Cannot read the image from file.");
        }
		
    	return icon;
	}
    
    public void showImage(Icon icon){
    	if (icon == null)
    	{
    		this.wipeImage();
    		return;
    	}
    	if (picLabel != null)
    		this.remove(picLabel);
    	
    	//icon.paintIcon(this, getGraphics(), (this.getWidth()-IMG_WIDTH)/2, 30);
    	picLabel = new JLabel(icon);
    	picLabel.setSize(this.getSize());
    	this.add(picLabel, BorderLayout.CENTER);
        this.repaint();
    }
    public void wipeImage(){
    	if (picLabel != null)
    		this.remove(picLabel);
        this.repaint();
    }
    
    /** Returns an ImageIcon, or null if the path was invalid. */
    protected ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = ImageImporter.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }
}