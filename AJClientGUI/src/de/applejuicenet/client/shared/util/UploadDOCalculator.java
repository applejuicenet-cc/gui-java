package de.applejuicenet.client.shared.util;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JTable;

import de.applejuicenet.client.fassade.controller.dac.UploadDO;
import de.applejuicenet.client.fassade.shared.Version;
import de.applejuicenet.client.shared.IconManager;

public abstract class UploadDOCalculator {
	
	private static JProgressBar progress = new JProgressBar(JProgressBar.HORIZONTAL, 0, 100);
	private static JProgressBar wholeLoadedProgress = new JProgressBar(JProgressBar.HORIZONTAL, 0, 100);
	private static JLabel progressbarLabel = new JLabel();
	private static JLabel versionLabel = new JLabel();
	private static JLabel wholeLoadedProgressbarLabel = new JLabel();
	static {
        progress.setStringPainted(true);
        progress.setOpaque(false);
        progressbarLabel = new JLabel();
        progressbarLabel.setOpaque(true);
        versionLabel.setOpaque(true);
        wholeLoadedProgress.setStringPainted(true);
        wholeLoadedProgress.setOpaque(false);
        wholeLoadedProgressbarLabel.setOpaque(true);
	}

	 public static Component getProgressbarComponent(UploadDO uploadDO) {
        if (uploadDO.getStatus() == UploadDO.AKTIVE_UEBERTRAGUNG) {
            String prozent = uploadDO.getDownloadPercentAsString();
            progress.setString(prozent + " %");
            int pos = prozent.indexOf('.');
            if (pos != -1) {
                prozent = prozent.substring(0, pos);
            }
            progress.setValue(Integer.parseInt(prozent));
            return progress;
        }
        else {
            return progressbarLabel;
        }
    }

    public static Component getVersionComponent(UploadDO uploadDO, JTable table) {
        versionLabel.setFont(table.getFont());
        if (uploadDO.getVersion() == null) {
            versionLabel.setIcon(null);
            versionLabel.setText("");
        }
        else {
            versionLabel.setIcon(getVersionIcon(uploadDO));
            versionLabel.setText(uploadDO.getVersion().getVersion());
        }
        return versionLabel;
    }

    public static Component getWholeLoadedProgressbarComponent(UploadDO uploadDO) {
        if (uploadDO.getLoaded() != -1) {
            wholeLoadedProgress.setString(uploadDO.getLoaded() + " %");
            wholeLoadedProgress.setValue(uploadDO.getLoaded());
            return wholeLoadedProgress;
        }
        else {
            return wholeLoadedProgressbarLabel;
        }
    }

	public static ImageIcon getVersionIcon(UploadDO uploadDO) {
        switch (uploadDO.getVersion().getBetriebsSystem()) {
	        case Version.WIN32: {
	            return IconManager.getInstance().getIcon("winsymbol");
	        }
	        case Version.LINUX: {
	            return IconManager.getInstance().getIcon("linuxsymbol");
	        }
	        case Version.FREEBSD: {
	            return IconManager.getInstance().getIcon("freebsdsymbol");
	        }
	        case Version.MACINTOSH: {
	            return IconManager.getInstance().getIcon("macsymbol");
	        }
	        case Version.SOLARIS: {
	            return IconManager.getInstance().getIcon("sunossymbol");
	        }
	        case Version.NETWARE: {
	            return IconManager.getInstance().getIcon("netwaresymbol");
	        }
	        case Version.OS2: {
	            return IconManager.getInstance().getIcon("os2symbol");
	        }
	        default: {
	            return IconManager.getInstance().getIcon("unbekanntsymbol");
	        }
        }
    }
}
