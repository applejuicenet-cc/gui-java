/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.shared;

import de.applejuicenet.client.gui.controller.OptionsManagerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author Maj0r [aj@tkl-soft.de]
 */
public class IconManager {
    private static IconManager instance = null;
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Map<String, ImageIcon> icons;

    private IconManager() {
        icons = new HashMap<>();
    }

    public static IconManager getInstance() {
        if (instance == null) {
            instance = new IconManager();
        }

        return instance;
    }

    public ImageIcon getIcon(String key) {
        ImageIcon result = null;

        try {

            if (icons.containsKey(key)) {
                result = icons.get(key);
            } else {
                String iconSet = OptionsManagerImpl.getInstance().getIconSetName();

                String path = System.getProperty("user.dir") + File.separator + "icons" + File.separator + iconSet + File.separator + key + ".png";

                File file = new File(path);

                if (!file.exists()) {
                    throw new FileNotFoundException("No Icon for " + key + " found");
                }

                Image img = Toolkit.getDefaultToolkit().getImage(path);

                result = new ImageIcon(img);
                icons.put(key, result);
            }
        } catch (Exception e) {
            logger.info("Icon " + key + ".png nicht gefunden", e);
        }

        return result;
    }

    public Properties getIconProperties(String identifier) {
        String iconSet = OptionsManagerImpl.getInstance().getIconSetName();
        String path = System.getProperty("user.dir") + File.separator + "icons" + File.separator + iconSet + File.separator + identifier + ".properties";
        File aFile = new File(path);

        if (aFile.isFile()) {
            Properties props = new Properties();

            try {
                props.load(new FileInputStream(aFile));
            } catch (Exception e) {
                return null;
            }

            return props;
        } else {
            return null;
        }
    }
}
