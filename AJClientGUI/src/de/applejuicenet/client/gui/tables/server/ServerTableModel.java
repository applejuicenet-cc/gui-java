package de.applejuicenet.client.gui.tables.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.table.AbstractTableModel;

import de.applejuicenet.client.gui.shared.SortableTableModel;
import de.applejuicenet.client.gui.shared.TableSorter;
import de.applejuicenet.client.shared.dac.ServerDO;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/tables/server/Attic/ServerTableModel.java,v 1.9 2004/02/05 23:11:28 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI f�r den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: ServerTableModel.java,v $
 * Revision 1.9  2004/02/05 23:11:28  maj0r
 * Formatierung angepasst.
 *
 * Revision 1.8  2004/01/30 16:32:47  maj0r
 * MapSetStringKey ausgebaut.
 *
 * Revision 1.7  2004/01/24 08:10:24  maj0r
 * Anzahl der Verbindungsversuche eingebaut.
 *
 * Revision 1.6  2003/12/29 16:04:17  maj0r
 * Header korrigiert.
 *
 * Revision 1.5  2003/10/21 14:08:45  maj0r
 * Mittels PMD Code verschoenert, optimiert.
 *
 * Revision 1.4  2003/08/19 16:02:16  maj0r
 * Optimierungen.
 *
 * Revision 1.3  2003/08/17 20:55:34  maj0r
 * Neusortierung der Tabelle nach �nderung entfernt.
 *
 * Revision 1.2  2003/08/02 12:03:38  maj0r
 * An neue Schnittstelle angepasst.
 *
 * Revision 1.1  2003/07/01 18:41:39  maj0r
 * Struktur ver�ndert.
 *
 * Revision 1.6  2003/07/01 14:54:27  maj0r
 * Weggefallene Server werden erkannt und entfernt.
 *
 * Revision 1.5  2003/06/24 14:32:27  maj0r
 * Klassen zum Sortieren von Tabellen eingef�gt.
 * Servertabelle kann nun spaltenweise sortiert werden.
 *
 * Revision 1.4  2003/06/10 12:31:03  maj0r
 * Historie eingef�gt.
 *
 *
 */

public class ServerTableModel
    extends AbstractTableModel
    implements SortableTableModel {
    final static String[] COL_NAMES = {
        "Name", "DynIP", "Port", "Verbindungsversuche", "Letztes mal online"};

    private TableSorter sorter;
    private ArrayList servers = new ArrayList();

    public ArrayList getContent() {
        return servers;
    }

    public Object getRow(int row) {
        if ( (servers != null) && (row < servers.size())) {
            return servers.get(row);
        }
        return null;
    }

    public void sortByColumn(int column, boolean isAscent) {
        if (sorter == null) {
            sorter = new TableSorter(this);
        }
        sorter.sort(column, isAscent);
        fireTableDataChanged();
    }

    public Object getValueAt(int row, int column) {
        if ( (servers == null) || (row >= servers.size())) {
            return "";
        }

        ServerDO server = (ServerDO) servers.get(row);
        if (server == null) {
            return "";
        }

        switch (column) {
            case 0:
                return server.getName();
            case 1:
                return server.getHost();
            case 2:
                return server.getPort();
            case 3:
                return new Integer(server.getVersuche());
            case 4:
                return server.getTimeLastSeenAsString();
            default:
                return "";
        }
    }

    public int getColumnCount() {
        return COL_NAMES.length;
    }

    public String getColumnName(int index) {
        return COL_NAMES[index];
    }

    public int getRowCount() {
        if (servers == null) {
            return 0;
        }
        return servers.size();
    }

    public Class getClass(int index) {
        if (index == 3) {
            return Number.class;
        }
        else {
            return String.class;
        }
    }

    public void setTable(HashMap changedContent) {
        //alte Server entfernen
        String suchKey = null;
        ArrayList toRemove = new ArrayList();
        for (int i = 0; i < servers.size(); i++) {
            suchKey = Integer.toString( ( (ServerDO) servers.get(i)).getID());
            if (!changedContent.containsKey(suchKey)) {
                toRemove.add(servers.get(i));
            }
        }
        for (int x = 0; x < toRemove.size(); x++) {
            servers.remove(toRemove.get(x));
        }
        Iterator it = changedContent.values().iterator();
        while (it.hasNext()) {
            ServerDO server = (ServerDO) it.next();
            int index = servers.indexOf(server);
            if (index == -1) { // Der Server ist neu
                servers.add(server);
            }
            else { // Der Server hat sich ver�ndert
                ServerDO oldServer = (ServerDO) servers.get(index);
                oldServer.setHost(server.getHost());
                oldServer.setName(server.getName());
                oldServer.setPort(server.getPort());
                oldServer.setVersuche(server.getVersuche());
                oldServer.setTimeLastSeen(server.getTimeLastSeen());
                oldServer.setConnected(server.isConnected());
                oldServer.setTryConnect(server.isTryConnect());
            }
        }
        this.fireTableDataChanged();
    }
}