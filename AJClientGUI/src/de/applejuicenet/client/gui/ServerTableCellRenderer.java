package de.applejuicenet.client.gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;
import de.applejuicenet.client.shared.dac.ServerDO;
import java.util.Date;
import de.applejuicenet.client.shared.IconManager;

/**
 * <p>Title: AppleJuice Client-GUI</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Maj0r
 * @version 1.0
 */

public class ServerTableCellRenderer
    implements TableCellRenderer {
  public Component getTableCellRendererComponent(JTable table,
                                                 Object value,
                                                 boolean isSelected,
                                                 boolean hasFocus,
                                                 int row,
                                                 int column) {
    ServerDO server = (ServerDO)((ServerTableModel)table.getModel()).getRow(row);
    JPanel returnPanel = new JPanel(new BorderLayout());
    JLabel image = null;
    long aktuelleZeit = System.currentTimeMillis();
    long tag = 24 * 60 * 60 * 1000;

    if (server.getTimeLastSeen()==0 || server.getTimeLastSeen()<aktuelleZeit-tag)
      image = new JLabel(IconManager.getInstance().getIcon("aelter24h"));
    else
      image = new JLabel(IconManager.getInstance().getIcon("juenger24h"));
    //mehr Icons kommen, wenn der Core mehr kann

    JLabel serverName = new JLabel("  " + (String)value);
    serverName.setFont(table.getFont());
    returnPanel.add(image, BorderLayout.WEST);
    returnPanel.add(serverName, BorderLayout.CENTER);
    if (isSelected){
      returnPanel.setBackground(table.getSelectionBackground());
      returnPanel.setForeground(table.getSelectionForeground());
      image.setBackground(table.getSelectionBackground());
      serverName.setBackground(table.getSelectionBackground());
      image.setForeground(table.getSelectionForeground());
      serverName.setBackground(table.getSelectionForeground());
    }
    else{
      returnPanel.setBackground(table.getBackground());
      returnPanel.setForeground(table.getForeground());
      image.setBackground(table.getBackground());
      serverName.setBackground(table.getBackground());
      image.setForeground(table.getForeground());
      serverName.setBackground(table.getForeground());
    }
    return returnPanel;
  }
}