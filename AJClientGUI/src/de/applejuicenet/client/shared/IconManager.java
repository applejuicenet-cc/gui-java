package de.applejuicenet.client.shared;

import java.net.URL;
import java.util.HashMap;

import java.awt.Image;
import java.awt.Toolkit;
import javax.swing.ImageIcon;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import de.applejuicenet.client.shared.icons.DummyClass;
import java.util.Map;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/shared/IconManager.java,v 1.11 2004/03/03 15:33:31 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI f�r den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: IconManager.java,v $
 * Revision 1.11  2004/03/03 15:33:31  maj0r
 * PMD-Optimierung
 *
 * Revision 1.10  2004/02/05 23:11:27  maj0r
 * Formatierung angepasst.
 *
 * Revision 1.9  2004/01/30 16:32:47  maj0r
 * MapSetStringKey ausgebaut.
 *
 * Revision 1.8  2003/12/29 16:04:17  maj0r
 * Header korrigiert.
 *
 * Revision 1.7  2003/11/03 15:04:27  maj0r
 * Nix wesentliches.
 *
 * Revision 1.6  2003/09/03 12:31:07  maj0r
 * Logger eingebaut.
 *
 * Revision 1.5  2003/07/01 14:50:37  maj0r
 * Inner-Class Key ausgelagert und umbenannt.
 *
 * Revision 1.4  2003/06/30 20:35:50  maj0r
 * Code optimiert.
 *
 * Revision 1.3  2003/06/10 12:31:03  maj0r
 * Historie eingef�gt.
 *
 *
 */

public class IconManager {
    private static IconManager instance = null;
    private Map icons;
    private static Logger logger;

    private IconManager() {
        icons = new HashMap();
    }

    public static IconManager getInstance() {
        if (instance == null) {
            instance = new IconManager();
            logger = Logger.getLogger(instance.getClass());
        }
        return instance;
    }

    public ImageIcon getIcon(String key) {
        ImageIcon result = null;
        try {
            String hashtableKey = key;
            if (icons.containsKey(hashtableKey)) {
                result = (ImageIcon) icons.get(hashtableKey);
            }
            else {
                URL url = new DummyClass().getClass().getResource(key + ".gif");
                Image img = Toolkit.getDefaultToolkit().getImage(url);
                result = new ImageIcon(img);
                icons.put(hashtableKey, result);
            }
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.INFO)) {
                logger.info("Icon " + key + ".gif nicht gefunden", e);
            }
        }
        return result;
    }
}