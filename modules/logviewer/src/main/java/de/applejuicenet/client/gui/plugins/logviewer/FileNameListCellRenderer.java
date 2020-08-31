/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.plugins.logviewer;

import java.awt.Component;

import java.io.File;

import javax.swing.JList;
import javax.swing.ListCellRenderer;

import de.tklsoft.gui.controls.TKLLabel;

public class FileNameListCellRenderer extends TKLLabel implements ListCellRenderer
{
   public FileNameListCellRenderer()
   {
      setOpaque(true);
   }

   public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
   {
      setText(" " + ((File) value).getName() + " ");
      setToolTipText(((File) value).getAbsolutePath());
      if(isSelected)
      {
         setBackground(list.getSelectionBackground());
         setForeground(list.getSelectionForeground());
      }
      else
      {
         setBackground(list.getBackground());
         setForeground(list.getForeground());
      }

      return this;
   }
}
