package de.applejuicenet.client.gui.share;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import de.applejuicenet.client.fassade.ApplejuiceFassade;
import de.applejuicenet.client.gui.share.tree.DirectoryNode;
import de.applejuicenet.client.gui.share.tree.DirectoryTree;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/share/ShareTreeMouseAdapter.java,v 1.4 2005/01/18 17:35:29 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 */

public class ShareTreeMouseAdapter extends MouseAdapter {
	
	private static Logger logger = Logger.getLogger(ShareTreeMouseAdapter.class);
	
	private DirectoryTree folderTree;
	private JPopupMenu popup;
	private JMenuItem sharedWithSub;
	private JMenuItem sharedWithoutSub;
	private JMenuItem notShared;
	
	public ShareTreeMouseAdapter(DirectoryTree folderTree, JPopupMenu popup,
			JMenuItem sharedWithSub, JMenuItem sharedWithoutSub, JMenuItem notShared){
		this.folderTree = folderTree;
		this.popup = popup;
		this.sharedWithSub = sharedWithSub;
		this.sharedWithoutSub = sharedWithoutSub;
		this.notShared = notShared;
	}
	
    public void mousePressed(MouseEvent me) {
        try {
            if (SwingUtilities.isRightMouseButton(me)) {
                Point p = me.getPoint();
                int iRow = folderTree.getRowForLocation(p.x, p.y);
                folderTree.setSelectionRow(iRow);
            }
            maybeShowPopup(me);
        }
        catch (Exception ex) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error(ApplejuiceFassade.ERROR_MESSAGE, ex);
            }
        }
    }

    public void mouseReleased(MouseEvent e) {
        try {
            super.mouseReleased(e);
            maybeShowPopup(e);
        }
        catch (Exception ex) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error(ApplejuiceFassade.ERROR_MESSAGE, ex);
            }
        }
    }

    private void maybeShowPopup(MouseEvent e) {
        try {
            if (e.isPopupTrigger()) {
                DirectoryNode node = (DirectoryNode) folderTree.
                    getLastSelectedPathComponent();
                if (node == null) {
                    return;
                }
                popup.removeAll();
                int nodeShareMode = node.getShareMode();
                if (nodeShareMode == DirectoryNode.NOT_SHARED
                    || nodeShareMode == DirectoryNode.SHARED_SOMETHING
                    || nodeShareMode == DirectoryNode.SHARED_SUB) {
                    popup.add(sharedWithSub);
                    popup.add(sharedWithoutSub);
                    popup.show(folderTree, e.getX(), e.getY());
                }
                else if (nodeShareMode == DirectoryNode.SHARED_WITH_SUB
                         ||
                         nodeShareMode == DirectoryNode.SHARED_WITHOUT_SUB) {
                    popup.add(notShared);
                    popup.show(folderTree, e.getX(), e.getY());
                }
            }
        }
        catch (Exception ex) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error(ApplejuiceFassade.ERROR_MESSAGE, ex);
            }
        }
    }
}