package de.applejuicenet.client.gui.tables;

import java.util.EventObject;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import javax.swing.Icon;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import de.applejuicenet.client.gui.controller.PropertiesManager;
import de.applejuicenet.client.gui.listener.DataUpdateListener;
import de.applejuicenet.client.gui.tables.download.DownloadDirectoryNode;
import de.applejuicenet.client.gui.tables.download.DownloadMainNode;
import de.applejuicenet.client.gui.tables.download.IconGetter;
import de.applejuicenet.client.shared.Search.SearchEntry;
import de.applejuicenet.client.shared.Settings;
import de.applejuicenet.client.shared.dac.DownloadDO;
import de.applejuicenet.client.shared.dac.DownloadSourceDO;
import de.applejuicenet.client.shared.Search.SearchEntry.FileName;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/tables/Attic/JTreeTable.java,v 1.21 2004/02/05 23:11:27 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI f�r den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: JTreeTable.java,v $
 * Revision 1.21  2004/02/05 23:11:27  maj0r
 * Formatierung angepasst.
 *
 * Revision 1.20  2003/12/29 16:04:17  maj0r
 * Header korrigiert.
 *
 * Revision 1.19  2003/11/03 15:45:26  maj0r
 * Optimierungen.
 *
 * Revision 1.18  2003/10/21 14:08:45  maj0r
 * Mittels PMD Code verschoenert, optimiert.
 *
 * Revision 1.17  2003/10/02 11:14:39  maj0r
 * Kleinen Anzeigefehler korrigiert und unnoetige Casts entfernt.
 *
 * Revision 1.16  2003/10/01 07:25:44  maj0r
 * Suche weiter gefuehrt.
 *
 * Revision 1.15  2003/09/09 12:28:15  maj0r
 * Wizard fertiggestellt.
 *
 * Revision 1.14  2003/09/05 09:47:09  maj0r
 * Eine Klasse vertauscht.
 *
 * Revision 1.13  2003/09/04 17:55:58  maj0r
 * Ausgabe f�r DownloadSourceDO korrigiert.
 *
 * Revision 1.12  2003/09/02 16:06:54  maj0r
 * Downloadbaum komplett umgebaut.
 *
 * Revision 1.11  2003/08/30 19:45:11  maj0r
 * Sicherheitshalber Ueberpruefung eingebaut.
 *
 * Revision 1.10  2003/08/22 12:39:54  maj0r
 * Bug ID 798
 *
 * Revision 1.9  2003/08/16 17:50:31  maj0r
 * Diverse Farben k�nnen nun manuell eingestellt bzw. deaktiviert werden.
 * DownloaduebersichtTabelle kann deaktiviert werden.
 *
 * Revision 1.8  2003/08/12 06:12:05  maj0r
 * Fertig-Status-Farbe korrigiert.
 *
 * Revision 1.7  2003/08/09 10:56:54  maj0r
 * Nur ein todo eingef�gt.
 *
 * Revision 1.6  2003/08/02 12:03:38  maj0r
 * An neue Schnittstelle angepasst.
 *
 * Revision 1.5  2003/07/07 15:57:59  maj0r
 * Fehler korrigiert.
 *
 * Revision 1.4  2003/07/06 20:00:19  maj0r
 * DownloadTable bearbeitet.
 *
 * Revision 1.3  2003/07/02 13:54:34  maj0r
 * JTreeTable komplett �berarbeitet.
 *
 *
 */

public class JTreeTable
    extends JTable {
    protected TreeTableCellRenderer tree;

    protected JTable thisTable;

    public JTreeTable(TreeTableModel treeTableModel) {
        super();
        thisTable = this;
        tree = new TreeTableCellRenderer(treeTableModel);

        super.setModel(new TreeTableModelAdapter(treeTableModel, tree));

        ListToTreeSelectionModelWrapper selectionWrapper = new
            ListToTreeSelectionModelWrapper();
        tree.setSelectionModel(selectionWrapper);
        setSelectionModel(selectionWrapper.getListSelectionModel());

        setDefaultRenderer(TreeTableModel.class, tree);
        setDefaultEditor(TreeTableModel.class, new TreeTableCellEditor());

        setShowGrid(false);

        setIntercellSpacing(new Dimension(0, 0));

        if (tree.getRowHeight() < 1) {
            // Metal looks better like this.
            setRowHeight(18);
        }
    }

    public void updateUI() {
        super.updateUI();
        if (tree != null) {
            tree.updateUI();
        }
        LookAndFeel.installColorsAndFont(this, "Tree.background",
                                         "Tree.foreground", "Tree.font");
    }

    public int getEditingRow() {
        return (getColumnClass(editingColumn) == TreeTableModel.class) ? -1 :
            editingRow;
    }

    public void setRowHeight(int rowHeight) {
        super.setRowHeight(rowHeight);
        if (tree != null && tree.getRowHeight() != rowHeight) {
            tree.setRowHeight(getRowHeight());
        }
    }

    public JTree getTree() {
        return tree;
    }

    public class TreeTableCellRenderer
        extends JTree
        implements
        TableCellRenderer, DataUpdateListener {

        protected int visibleRow;
        private Settings settings;

        public TreeTableCellRenderer(TreeModel model) {
            super(model);
            settings = Settings.getSettings();
            this.setCellRenderer(new IconNodeRenderer());
            PropertiesManager.getOptionsManager().addSettingsListener(this);
        }

        public void updateUI() {
            super.updateUI();
            TreeCellRenderer tcr = getCellRenderer();
            if (tcr instanceof DefaultTreeCellRenderer) {
                DefaultTreeCellRenderer dtcr = ( (DefaultTreeCellRenderer) tcr);
                dtcr.setTextSelectionColor(UIManager.getColor
                                           ("Table.selectionForeground"));
                dtcr.setBackgroundSelectionColor(UIManager.getColor
                                                 ("Table.selectionBackground"));
            }
        }

        public void setRowHeight(int rowHeight) {
            if (rowHeight > 0) {
                super.setRowHeight(rowHeight);
                if (JTreeTable.this != null &&
                    JTreeTable.this.getRowHeight() != rowHeight) {
                    JTreeTable.this.setRowHeight(getRowHeight());
                }
            }
        }

        public void setBounds(int x, int y, int w, int h) {
            super.setBounds(x, 0, w, JTreeTable.this.getHeight());
        }

        public void paint(Graphics g) {
            g.translate(0, -visibleRow * getRowHeight());
            super.paint(g);
        }

        public Component getTableCellRendererComponent(JTable table,
            Object value,
            boolean isSelected,
            boolean hasFocus,
            int row, int column) {
            Object node = ( (TreeTableModelAdapter) table.getModel()).
                nodeForRow(row);
            if (isSelected) {
                setBackground(table.getSelectionBackground());
            }
            else {
                if (settings.isFarbenAktiv()) {
                    if (node.getClass() == DownloadSourceDO.class) {
                        setBackground(settings.getQuelleHintergrundColor());
                    }
                    else if (node.getClass() == DownloadMainNode.class &&
                             ( (DownloadMainNode) node).getType() ==
                             DownloadMainNode.ROOT_NODE &&
                             ( (DownloadMainNode) node).getDownloadDO().
                             getStatus() == DownloadDO.FERTIG) {
                        setBackground(settings.
                                      getDownloadFertigHintergrundColor());
                    }
                    else {
                        setBackground(table.getBackground());
                    }
                }
                else {
                    setBackground(table.getBackground());
                }
            }

            visibleRow = row;
            return this;
        }

        public void fireContentChanged(int type, Object content) {
            if (type == DataUpdateListener.SETTINGS_CHANGED) {
                settings = (Settings) content;
            }
        }
    }

    public class TreeTableCellEditor
        extends AbstractCellEditor
        implements
        TableCellEditor {
        public Component getTableCellEditorComponent(JTable table,
            Object value,
            boolean isSelected,
            int r, int c) {
            return tree;
        }

        public boolean isCellEditable(EventObject e) {
            if (e instanceof MouseEvent) {
                for (int counter = getColumnCount() - 1; counter >= 0;
                     counter--) {
                    if (getColumnClass(counter) == TreeTableModel.class) {
                        MouseEvent me = (MouseEvent) e;
                        MouseEvent newME = new MouseEvent(tree, me.getID(),
                            me.getWhen(), me.getModifiers(),
                            me.getX() - getCellRect(0, counter, true).x,
                            me.getY(), me.getClickCount(),
                            me.isPopupTrigger());
                        tree.dispatchEvent(newME);
                        break;
                    }
                }
            }
            return false;
        }
    }

    class ListToTreeSelectionModelWrapper
        extends DefaultTreeSelectionModel {

        protected boolean updatingListSelectionModel;

        public ListToTreeSelectionModelWrapper() {
            super();
            getListSelectionModel().addListSelectionListener
                (createListSelectionListener());
        }

        ListSelectionModel getListSelectionModel() {
            return listSelectionModel;
        }

        public void resetRowSelection() {
            if (!updatingListSelectionModel) {
                updatingListSelectionModel = true;
                try {
                    super.resetRowSelection();
                }
                finally {
                    updatingListSelectionModel = false;
                }
            }
        }

        protected ListSelectionListener createListSelectionListener() {
            return new ListSelectionHandler();
        }

        protected void updateSelectedPathsFromSelectedRows() {
            if (!updatingListSelectionModel) {
                updatingListSelectionModel = true;
                try {
                    int min = listSelectionModel.getMinSelectionIndex();
                    int max = listSelectionModel.getMaxSelectionIndex();

                    clearSelection();
                    if (min != -1 && max != -1) {
                        for (int counter = min; counter <= max; counter++) {
                            if (listSelectionModel.isSelectedIndex(counter)) {
                                TreePath selPath = tree.getPathForRow
                                    (counter);

                                if (selPath != null) {
                                    addSelectionPath(selPath);
                                }
                            }
                        }
                    }
                }
                finally {
                    updatingListSelectionModel = false;
                }
            }
        }

        class ListSelectionHandler
            implements ListSelectionListener {
            public void valueChanged(ListSelectionEvent e) {
                updateSelectedPathsFromSelectedRows();
            }
        }
    }

    public class IconNodeRenderer
        extends DefaultTreeCellRenderer
        implements DataUpdateListener {

        private Settings settings;

        public IconNodeRenderer() {
            super();
            settings = Settings.getSettings();
            PropertiesManager.getOptionsManager().addSettingsListener(this);
        }

        public Component getTreeCellRendererComponent(JTree tree, Object value,
            boolean sel, boolean expanded,
            boolean leaf,
            int row, boolean hasFocus) {

            Component c = null;
            if (value.getClass() == DownloadSourceDO.class) {
                c = super.getTreeCellRendererComponent(tree,
                    ( (DownloadSourceDO) value).getNickname()
                    + " (" + ( (DownloadSourceDO) value).getFilename() + ")",
                    sel, expanded, leaf, row, hasFocus);
            }
            else if (value.getClass() == DownloadMainNode.class) {
                c = super.getTreeCellRendererComponent(tree, value.toString(),
                    sel, expanded, leaf, row, hasFocus);
            }
            else if (value.getClass() == DownloadDirectoryNode.class) {
                c = super.getTreeCellRendererComponent(tree,
                    ( (DownloadDirectoryNode) value).getVerzeichnis(),
                    sel, expanded, leaf, row, hasFocus);
            }
            else if (value.getClass() == FileName.class) {
                c = super.getTreeCellRendererComponent(tree,
                    ( (FileName) value).getDateiName(),
                    sel, expanded, leaf, row, hasFocus);
            }
            else {
                c = super.getTreeCellRendererComponent(tree, value,
                    sel, expanded, leaf, row, hasFocus);
            }
            if (c.getClass() == IconNodeRenderer.class) {
                ( (IconNodeRenderer) c).setOpaque(true);
                if (sel) {
                    c.setBackground(thisTable.getSelectionBackground());
                    c.setForeground(thisTable.getSelectionForeground());
                }
                else {
                    if (settings.isFarbenAktiv()) {
                        if (value.getClass() == DownloadSourceDO.class) {
                            c.setBackground(settings.getQuelleHintergrundColor());
                        }
                        else if (value.getClass() == DownloadMainNode.class &&
                                 ( (DownloadMainNode) value).getType() ==
                                 DownloadMainNode.ROOT_NODE &&
                                 ( (DownloadMainNode) value).getDownloadDO().
                                 getStatus() == DownloadDO.FERTIG) {
                            c.setBackground(settings.
                                            getDownloadFertigHintergrundColor());
                        }
                        else {
                            c.setBackground(tree.getBackground());
                        }
                    }
                }
            }
            if (value instanceof Node) {
                Icon icon = ( (Node) value).getConvenientIcon();
                if (icon != null) {
                    setIcon(icon);
                }
            }
            else {
                Icon icon = IconGetter.getConvenientIcon(value);
                if (icon != null) {
                    setIcon(icon);
                }
            }
            return this;
        }

        public void fireContentChanged(int type, Object content) {
            if (type == DataUpdateListener.SETTINGS_CHANGED) {
                settings = (Settings) content;
            }
        }
    }
}
