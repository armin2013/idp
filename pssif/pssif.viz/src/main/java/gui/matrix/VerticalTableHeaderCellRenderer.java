package gui.matrix;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.Icon;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.UIManager;

public class VerticalTableHeaderCellRenderer
  extends DefaultTableHeaderCellRenderer
{
  public VerticalTableHeaderCellRenderer()
  {
    setHorizontalAlignment(2);
    setHorizontalTextPosition(0);
    setVerticalAlignment(0);
    setVerticalTextPosition(1);
    setUI(new VerticalLabelUI());
  }
  
  protected Icon getIcon(JTable table, int column)
  {
    RowSorter.SortKey sortKey = getSortKey(table, column);
    if ((sortKey != null) && (table.convertColumnIndexToView(sortKey.getColumn()) == column))
    {
      SortOrder sortOrder = sortKey.getSortOrder();
      switch (sortOrder)
      {
      case ASCENDING: 
        return VerticalSortIcon.ASCENDING;
      case DESCENDING: 
        return VerticalSortIcon.DESCENDING;
      }
    }
    return null;
  }
  
  private static enum VerticalSortIcon
    implements Icon
  {
    ASCENDING(UIManager.getIcon("Table.ascendingSortIcon")),  DESCENDING(UIManager.getIcon("Table.descendingSortIcon"));
    
    private final Icon icon;
    
    private VerticalSortIcon(Icon icon)
    {
      this.icon = icon;
    }
    
    public void paintIcon(Component c, Graphics g, int x, int y)
    {
      int maxSide = Math.max(getIconWidth(), getIconHeight());
      Graphics2D g2 = (Graphics2D)g.create(x, y, maxSide, maxSide);
      g2.rotate(1.570796326794897D);
      g2.translate(0, -maxSide);
      this.icon.paintIcon(c, g2, 0, 0);
      g2.dispose();
    }
    
    public int getIconWidth()
    {
      return this.icon.getIconHeight();
    }
    
    public int getIconHeight()
    {
      return this.icon.getIconWidth();
    }
  }
}