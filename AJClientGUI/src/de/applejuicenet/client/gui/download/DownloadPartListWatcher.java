package de.applejuicenet.client.gui.download;

import de.applejuicenet.client.fassade.controller.dac.DownloadSourceDO;
import de.applejuicenet.client.gui.download.table.DownloadMainNode;

public class DownloadPartListWatcher {
	private Object nodeObject = null;
	private DownloadController downloadController;
	
	public DownloadPartListWatcher(DownloadController downloadController){
		this.downloadController = downloadController;
	}

	public void setDownloadNode(Object node) {
		nodeObject = node;
		if (nodeObject == null) {
			((DownloadPanel)downloadController.getComponent())
				.getDownloadDOOverviewPanel()
					.setDownloadDO(null);
			return;
		}
		if (nodeObject.getClass() == DownloadMainNode.class
				&& ((DownloadMainNode) nodeObject)
						.getType() == DownloadMainNode.ROOT_NODE) {
			((DownloadPanel)downloadController.getComponent())
				.getDownloadDOOverviewPanel()
					.setDownloadDO(((DownloadMainNode) nodeObject)
							.getDownloadDO());
		} else if (nodeObject.getClass() == DownloadSourceDO.class) {
			if (((DownloadSourceDO) nodeObject).getStatus() == DownloadSourceDO.IN_WARTESCHLANGE
					&& ((DownloadSourceDO) nodeObject)
							.getQueuePosition() > 20) {
				((DownloadPanel)downloadController.getComponent())
				.getDownloadDOOverviewPanel()
					.setDownloadDO(null);
			}
			else{
				((DownloadPanel)downloadController.getComponent())
					.getDownloadDOOverviewPanel()
						.setDownloadSourceDO((DownloadSourceDO) nodeObject);
			}
		}
	}
}
