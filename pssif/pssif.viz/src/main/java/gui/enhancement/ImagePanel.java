package gui.enhancement;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class ImagePanel extends JPanel {

	private Image img = null;
	
	public Image getImg() {
		return img;
	}

	public void reloadImage(Icon icon)
	{
		this.img.flush();
		this.img = null;
		this.img = ((ImageIcon) icon).getImage();
		this.repaint();
		
	}
	
	
	public void setImg(Image img) {
		this.img = img;
	}

	
	public ImagePanel(String img) {
		  this(new ImageIcon(img).getImage());
	  }
	
	  public ImagePanel(URL img) {
		  this(new ImageIcon(img).getImage());
	  }

	  public ImagePanel(Icon icon)
	  {
		  this(((ImageIcon) icon).getImage());
	  }
	  
	  public ImagePanel(Image img) {
	    this.img = img;
	    Dimension size = new Dimension(img.getWidth(null), img.getHeight(null));
	    setPreferredSize(size);
	    setMinimumSize(size);
	    setMaximumSize(size);
	    setSize(size);
	    
	    
	    setLayout(null);
	  } 
	  
	  
	  
	  public ImagePanel(String img, int width, int height)
		{
			try{
				BufferedImage originalImage = ImageIO.read(new File(img));
				int type = originalImage.getType() == 0? BufferedImage.TYPE_INT_ARGB : originalImage.getType();     
				BufferedImage resizeImageJpg = this.resizeImage(originalImage, type, width, height);
				this.img = resizeImageJpg;
			} catch(Exception e)
			{
				System.out.println(e);
				this.img = null;
			}
		}
		
		private BufferedImage resizeImage(BufferedImage originalImage, int type, int width, int height){
			BufferedImage resizedImage = new BufferedImage(width, height, type);
			Graphics2D g = resizedImage.createGraphics();
			g.drawImage(originalImage, 0, 0, width, height, null);
			g.dispose();
		 
			return resizedImage;
		}
	  
	  public void paintComponent(Graphics g) {
	    	if (img != null)
	    	{
	    		g.drawImage(img, 10, 40, null);
	    	}
	  }
}