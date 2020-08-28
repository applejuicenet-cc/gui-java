package de.applejuicenet.client.gui.share;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import de.applejuicenet.client.gui.share.table.ShareNode;
import de.applejuicenet.client.gui.share.table.ShareTable;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/share/ShareTableMouseAdapter.java,v 1.3 2004/10/15 13:39:47 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 */

public class ShareTableMouseAdapter extends MouseAdapter {
	
	private ShareTable shareTable;
	private JPopupMenu popup;
	
	public ShareTableMouseAdapter(ShareTable shareTable, JPopupMenu popup){
		this.shareTable = shareTable;
		this.popup = popup;
	}
	
    public void mousePressed(MouseEvent me) {
        if (SwingUtilities.isRightMouseButton(me)) {
            Point p = me.getPoint();
            int iRow = shareTable.rowAtPoint(p);
            int iCol = shareTable.columnAtPoint(p);
            if (iRow == -1 || iCol == -1) {
                return;
            }
            shareTable.setRowSelectionInterval(iRow, iRow);
            shareTable.setColumnSelectionInterval(iCol, iCol);
        }
        maybeShowPopup(me);
    }

    public void mouseReleased(MouseEvent e) {
        super.mouseReleased(e);
        maybeShowPopup(e);
    }

    private void maybeShowPopup(MouseEvent e) {
        if (e.isPopupTrigger() && shareTable.getSelectedRowCount() == 1) {
            Object[] obj = shareTable.getSelectedItems();
            if ( ( (ShareNode) obj[0]).isLeaf()) {
                popup.show(shareTable, e.getX(), e.getY());
            }
        }
    }
}