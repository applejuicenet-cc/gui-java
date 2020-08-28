package de.applejuicenet.client.gui.controller;

import de.applejuicenet.client.fassade.shared.ProxySettings;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/controller/ProxyManagerImpl.java,v 1.3 2005/01/18 17:35:27 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 */

public class ProxyManagerImpl implements ProxyManager{
    private static ProxyManager instance = null;
    private PropertiesManager propertiesManager;

    private ProxyManagerImpl(){
        propertiesManager = PropertiesManager.getInstance();
    }

    public static synchronized ProxyManager getInstance(){
        if (instance==null){
            instance = new ProxyManagerImpl();
        }
        return instance;
    }

    public ProxySettings getProxySettings(){
        return propertiesManager.getProxySettings();
    }

    public void saveProxySettings(ProxySettings proxySettings){
        propertiesManager.saveProxySettings(proxySettings);
    }
}
