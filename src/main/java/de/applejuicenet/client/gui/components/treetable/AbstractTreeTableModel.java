/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.components.treetable;

import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreePath;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/components/treetable/AbstractTreeTableModel.java,v 1.2 2009/01/12 07:45:46 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 */
public abstract class AbstractTreeTableModel implements TreeTableModel
{
   protected Object            root;
   protected EventListenerList listenerList = new EventListenerList();

   public AbstractTreeTableModel(Object root)
   {
      this.root = root;
   }

   public Object getRoot()
   {
      return root;
   }

   public boolean isLeaf(Object node)
   {
      return getChildCount(node) == 0;
   }

   public void valueForPathChanged(TreePath path, Object newValue)
   {
   }

   public int getIndexOfChild(Object parent, Object child)
   {
      for(int i = 0; i < getChildCount(parent); i++)
      {
         if(getChild(parent, i).equals(child))
         {
            return i;
         }
      }

      return -1;
   }

   public void addTreeModelListener(TreeModelListener l)
   {
      listenerList.add(TreeModelListener.class, l);
   }

   public void removeTreeModelListener(TreeModelListener l)
   {
      listenerList.remove(TreeModelListener.class, l);
   }

   protected void fireTreeNodesChanged(Object source, Object[] path, int[] childIndices, Object[] children)
   {
      Object[]       listeners = listenerList.getListenerList();
      TreeModelEvent e = null;

      for(int i = listeners.length - 2; i >= 0; i -= 2)
      {
         if(listeners[i] == TreeModelListener.class)
         {
            if(e == null)
            {
               e = new TreeModelEvent(source, path, childIndices, children);
            }

            ((TreeModelListener) listeners[i + 1]).treeNodesChanged(e);
         }
      }
   }

   protected void fireTreeNodesInserted(Object source, Object[] path, int[] childIndices, Object[] children)
   {
      Object[]       listeners = listenerList.getListenerList();
      TreeModelEvent e = null;

      for(int i = listeners.length - 2; i >= 0; i -= 2)
      {
         if(listeners[i] == TreeModelListener.class)
         {
            if(e == null)
            {
               e = new TreeModelEvent(source, path, childIndices, children);
            }

            ((TreeModelListener) listeners[i + 1]).treeNodesInserted(e);
         }
      }
   }

   protected void fireTreeNodesRemoved(Object source, Object[] path, int[] childIndices, Object[] children)
   {
      Object[]       listeners = listenerList.getListenerList();
      TreeModelEvent e = null;

      for(int i = listeners.length - 2; i >= 0; i -= 2)
      {
         if(listeners[i] == TreeModelListener.class)
         {
            if(e == null)
            {
               e = new TreeModelEvent(source, path, childIndices, children);
            }

            ((TreeModelListener) listeners[i + 1]).treeNodesRemoved(e);
         }
      }
   }

   protected void fireTreeStructureChanged(Object source, Object[] path, int[] childIndices, Object[] children)
   {
      Object[]       listeners = listenerList.getListenerList();
      TreeModelEvent e = null;

      for(int i = listeners.length - 2; i >= 0; i -= 2)
      {
         if(listeners[i] == TreeModelListener.class)
         {
            if(e == null)
            {
               e = new TreeModelEvent(source, path, childIndices, children);
            }

            ((TreeModelListener) listeners[i + 1]).treeStructureChanged(e);
         }
      }
   }

   @SuppressWarnings("unchecked")
   public Class getColumnClass(int column)
   {
      return Object.class;
   }

   public boolean isCellEditable(Object node, int column)
   {
      return getColumnClass(column) == TreeTableModel.class;
   }

   public void setValueAt(Object aValue, Object node, int column)
   {
   }
}
