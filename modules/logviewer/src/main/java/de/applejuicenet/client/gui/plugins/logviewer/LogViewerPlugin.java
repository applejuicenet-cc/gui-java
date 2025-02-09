/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.plugins.logviewer;

import de.applejuicenet.client.AppleJuiceClient;
import de.applejuicenet.client.fassade.ApplejuiceFassade;
import de.applejuicenet.client.gui.plugins.PluginConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Map;
import java.util.Properties;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/plugin_src/logviewer/src/de/applejuicenet/client/gui/plugins/logviewer/LogViewerPlugin.java,v 1.4 2009/01/23 09:58:24 maj0r Exp $
 *
 * <p>Titel: AppleJuice Core-GUI</p>
 * <p>Beschreibung: GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: GPL</p>
 *
 * @author loevenwong <timo@loevenwong.de>
 */
public class LogViewerPlugin extends PluginConnector {
    private static Logger logger;
    private static final String path = AppleJuiceClient.getPath() + File.separator + "logs";
    private final JTextPane logPane = new JTextPane();
    private final SortedStringListModel listModel = new SortedStringListModel();
    private final JList list = new JList(listModel);

    public LogViewerPlugin(Properties pluginsProperties, Map<String, Properties> languageFiles, ImageIcon icon,
                           Map<String, ImageIcon> availableIcons) {
        super(pluginsProperties, languageFiles, icon, availableIcons);
        logger = LoggerFactory.getLogger(getClass());
        try {
            setLayout(new BorderLayout());
            logPane.setBackground(getBackground());
            logPane.setContentType("text/html");
            logPane.setEditable(false);
            logPane.setBorder(null);
            list.setCellRenderer(new FileNameListCellRenderer());
            JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(list), new JScrollPane(logPane));
            add(splitPane, BorderLayout.CENTER);
            readLogDir();
            list.addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) {
                    doDisplayLogfile();
                }
            });
        } catch (Exception e) {
            logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
        }
    }

    private void doDisplayLogfile() {
        try {
            File selectedLog = (File) list.getSelectedValue();

            logPane.setPage("file://localhost/" + selectedLog.getAbsolutePath());
        } catch (Exception e) {
            logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
        }
    }

    private void readLogDir() {
        File logPath = new File(path);

        if (!logPath.isDirectory()) {
            return;
        }

        File[] htmlFiles = logPath.listFiles((dir, name) -> name.endsWith(".html"));

        listModel.setData(htmlFiles);
    }

    public void fireLanguageChanged() {
    }

    public void registerSelected() {
    }

    public void fireContentChanged(de.applejuicenet.client.fassade.listener.DataUpdateListener.DATALISTENER_TYPE arg0, Object arg1) {
    }
}
