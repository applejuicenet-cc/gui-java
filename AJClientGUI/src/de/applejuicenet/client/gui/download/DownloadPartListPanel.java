package de.applejuicenet.client.gui.download;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import de.applejuicenet.client.fassade.ApplejuiceFassade;
import de.applejuicenet.client.fassade.controller.dac.DownloadDO;
import de.applejuicenet.client.fassade.controller.dac.DownloadSourceDO;
import de.applejuicenet.client.fassade.controller.dac.PartListDO;
import de.applejuicenet.client.fassade.controller.dac.PartListDO.Part;
import de.applejuicenet.client.fassade.shared.ZeichenErsetzer;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.listener.LanguageListener;

/**
 * $Header:
 * /cvsroot/applejuicejava/AJClientGUI/src/de/applejuicenet/client/gui/DownloadPartListPanel.java,v
 * 1.33 2004/07/09 12:42:01 loevenwong Exp $
 * 
 * <p>
 * Titel: AppleJuice Client-GUI
 * </p>
 * <p>
 * Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten
 * appleJuice-Core
 * </p>
 * <p>
 * Copyright: General Public License
 * </p>
 * 
 * @author: Maj0r [aj@tkl-soft.de]
 *  
 */

public class DownloadPartListPanel extends JPanel implements
		MouseMotionListener, LanguageListener {

	private static DownloadPartListPanel instance = null;
	
	private PartListDO partListDO;
	private Logger logger;
	private BufferedImage image = null;
	private int width;
	private int height;
	private long fertigSeit = -1;
	private boolean miniFile = false;
	private String ueberprueft;
	private String nichtVorhanden;
	private String vorhanden;
	private String quellen;
	private String uebertragen;
	private MouseEvent savedMouseEvent = null;
	private Integer id = null;
	private int zeilenHoehe;
	private int pixelSize;
	private BufferedImage lineImage;

	private DownloadPartListPanel() {
		super(new BorderLayout());
		logger = Logger.getLogger(getClass());
		addMouseMotionListener(this);
		LanguageSelector.getInstance().addLanguageListener(this);
	}

	public static synchronized DownloadPartListPanel getInstance() {
		if (instance == null) {
			instance = new DownloadPartListPanel();
		}
		return instance;
	}

	public void paintComponent(Graphics g) {
		if (partListDO != null && image != null) {
			if (height != (int) getSize().getHeight()
					|| width != (int) getSize().getWidth()) {
				setPartList(partListDO, id);
			}
			g.setColor(getBackground());
			g.fillRect(0, 0, width, height);
			if (image != null) {
				g.drawImage(image, 0, 0, null);
			}
		} else {
			super.paintComponent(g);
		}
	}
	
	private void updatePanel(){
		SwingUtilities.invokeLater(new Runnable(){
			public void run() {
				updateUI();
			}
		});				
	}
	
	public synchronized void setPartList(PartListDO newPartListDO, Integer newId) {
		try {
			if (newPartListDO == null || newId == null){
				partListDO = null;
				lineImage = null;
				image = null;
				savedMouseEvent = null;
				updatePanel();
				return;
			}
			boolean idChanged = false;
			if (id == null){
				idChanged = true;
				id = newId;
			}
			if (id.longValue() != newId.longValue()){
				idChanged = true;
			}
			if (idChanged){
				id = newId;
				if (partListDO != null){
					partListDO.removeAllParts();
				}
			}
			else if (partListDO != null && newPartListDO != null){
				Part[] parts = partListDO.getParts();
				Part[] newParts = newPartListDO.getParts();
				if (parts.length == newParts.length){
					boolean sameParts = true;
					for (int i=0; i<parts.length; i++){
						if (!parts[i].equals(newParts[i])){
							sameParts = false;
							break;
						}
					}
					if(sameParts){
						insertSources(partListDO, lineImage);
						updatePanel();
						return;
					}
				}
			}
			partListDO = newPartListDO;
			height = (int) getSize().getHeight();
			width = (int) getSize().getWidth();
			if (partListDO != null && partListDO.getParts().length > 0) {
				Part[] parts = partListDO.getParts();
				zeilenHoehe = 15;
				int zeilen = height / zeilenHoehe;
				miniFile = false;
				pixelSize = (int) (partListDO.getGroesse() / (zeilen * width));
				if (pixelSize == 0) {
					pixelSize = (int) ((zeilen * width) / partListDO
							.getGroesse());
					miniFile = true;
				}
				lineImage = new BufferedImage(width * zeilen,
						15, BufferedImage.TYPE_INT_ARGB);
				Graphics graphics = lineImage.getGraphics();
				int obenLinks = 0;
				int breite = 0;
				fertigSeit = -1;
				for (int i = 0; i < parts.length - 1; i++) {
					drawPart(
							false,
							(partListDO.getPartListType() == PartListDO.MAIN_PARTLIST),
							graphics, pixelSize, parts[i].getType(),
							zeilenHoehe, parts[i].getFromPosition(),
							parts[i + 1].getFromPosition());
				}
				drawPart(
						true,
						(partListDO.getPartListType() == PartListDO.MAIN_PARTLIST),
						graphics, pixelSize, parts[parts.length - 1]
								.getType(), zeilenHoehe,
						parts[parts.length - 1].getFromPosition(),
						partListDO.getGroesse());
				BufferedImage imgWithSources = insertSources(partListDO, lineImage);
				if (savedMouseEvent != null) {
					processMouseMotionEvent(savedMouseEvent);
				}
			} else {
				image = null;
				savedMouseEvent = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			partListDO = null;
			lineImage = null;
			image = null;
			savedMouseEvent = null;
			if (logger.isEnabledFor(Level.DEBUG)) {
				logger.debug(ApplejuiceFassade.ERROR_MESSAGE, e);
			}
		}
		updatePanel();
	}
	
	private BufferedImage insertSources(PartListDO partListDO, BufferedImage sourceImage){
		BufferedImage imageWithSources = new BufferedImage(sourceImage.getWidth(), 15, BufferedImage.TYPE_INT_ARGB);
		Graphics graphics = imageWithSources.getGraphics();
		graphics.drawImage(sourceImage.getSubimage(0, 0, sourceImage.getWidth(),
				zeilenHoehe), 0, 0, null);
		int obenLinks;
		int breite;
		if (partListDO.getPartListType() == PartListDO.MAIN_PARTLIST) {
			DownloadDO downloadDO = (DownloadDO) partListDO
					.getValueDO();
			if (downloadDO.getStatus() == DownloadDO.SUCHEN_LADEN) {
				DownloadSourceDO[] sources = downloadDO
						.getSources();
				for (int i = 0; i < sources.length; i++) {
					if (sources[i].getStatus() == DownloadSourceDO.UEBERTRAGUNG) {
						if (!miniFile) {
							obenLinks = sources[i]
									.getDownloadFrom()
									/ pixelSize;
							breite = (sources[i].getDownloadTo() / pixelSize)
									- obenLinks;
						} else {
							obenLinks = sources[i]
									.getDownloadFrom()
									* pixelSize;
							breite = (sources[i].getDownloadTo() * pixelSize)
									- obenLinks;
						}
						graphics
								.setColor(getColorByPercent(sources[i]
										.getReadyPercent()));
						graphics.fillRect(obenLinks, 0, breite,
								zeilenHoehe);
					}
				}
			}
		} else {
			DownloadSourceDO downloadSourceDO = (DownloadSourceDO) partListDO
					.getValueDO();
			if (downloadSourceDO.getStatus() == DownloadSourceDO.UEBERTRAGUNG) {
				if (!miniFile) {
					obenLinks = downloadSourceDO.getDownloadFrom()
							/ pixelSize;
					breite = (downloadSourceDO.getDownloadTo() / pixelSize)
							- obenLinks;
				} else {
					obenLinks = downloadSourceDO.getDownloadFrom()
							* pixelSize;
					breite = (downloadSourceDO.getDownloadTo() * pixelSize)
							- obenLinks;
				}
				graphics
						.setColor(getColorByPercent(downloadSourceDO
								.getReadyPercent()));
				graphics
						.fillRect(obenLinks, 0, breite, zeilenHoehe);
			}
		}
		image = new BufferedImage(width, height,
				BufferedImage.TYPE_4BYTE_ABGR);
		Graphics g = image.getGraphics();
		int x = 0;
		int zeilen = height / zeilenHoehe;
		for (int i = 0; i < zeilen; i++) {
			g.drawImage(imageWithSources.getSubimage(x, 0, width,
					zeilenHoehe), 0, i * zeilenHoehe, null);
			x += width;
		}
		return imageWithSources;
	}

	private void drawPart(boolean forceDraw, boolean isMainList,
			Graphics graphics, int pixelSize, int partType, int zeilenHoehe,
			long currentFrom, long nextFrom) {
		int obenLinks = 0;
		int breite = 0;
		if (isMainList) {
			if (partType == -1 && !forceDraw) {
				if (fertigSeit != -1) {
					return;
				} else {
					fertigSeit = currentFrom;
				}
			} else {
				if (fertigSeit != -1) {
					obenLinks = (int) (fertigSeit / pixelSize);
					int mbCount = (int) (currentFrom - fertigSeit) / 1048576;
					breite = mbCount * 1048576 / pixelSize;
					graphics.setColor(PartListDO.COLOR_TYPE_UEBERPRUEFT);
					graphics.fillRect(obenLinks, 0, breite, zeilenHoehe);
					obenLinks += breite;
					breite = (int) (currentFrom / pixelSize) - obenLinks;
					if (partType == -1 || forceDraw) {
						graphics.setColor(PartListDO.COLOR_TYPE_UEBERPRUEFT);
					} else {
						graphics.setColor(PartListDO.COLOR_TYPE_OK);
					}
					graphics.fillRect(obenLinks, 0, breite, zeilenHoehe);
					obenLinks = (int) currentFrom / pixelSize;
					breite = (int) (nextFrom / pixelSize) - obenLinks;
					graphics.setColor(getColorByType(partType));
					graphics.fillRect(obenLinks, 0, breite, zeilenHoehe);
					fertigSeit = -1;
				} else {
					if (!miniFile) {
						obenLinks = (int) (currentFrom / pixelSize);
						breite = (int) (nextFrom / pixelSize) - obenLinks;
					} else {
						obenLinks = (int) (currentFrom * pixelSize);
						breite = (int) (nextFrom * pixelSize) - obenLinks;
					}
					graphics.setColor(getColorByType(partType));
					graphics.fillRect(obenLinks, 0, breite, zeilenHoehe);
				}
			}
		} else {
			if (!miniFile) {
				obenLinks = (int) (currentFrom / pixelSize);
				breite = (int) (nextFrom / pixelSize) - obenLinks;
			} else {
				obenLinks = (int) (currentFrom * pixelSize);
				breite = (int) (nextFrom * pixelSize) - obenLinks;
			}
			graphics.setColor(getColorByType(partType));
			graphics.fillRect(obenLinks, 0, breite, zeilenHoehe);
		}
	}

	private Color getColorByType(int type) {
		switch (type) {
		case -1:
			return PartListDO.COLOR_TYPE_OK;
		case 0:
			return PartListDO.COLOR_TYPE_0;
		case 1:
			return PartListDO.COLOR_TYPE_1;
		case 2:
			return PartListDO.COLOR_TYPE_2;
		case 3:
			return PartListDO.COLOR_TYPE_3;
		case 4:
			return PartListDO.COLOR_TYPE_4;
		case 5:
			return PartListDO.COLOR_TYPE_5;
		case 6:
			return PartListDO.COLOR_TYPE_6;
		case 7:
			return PartListDO.COLOR_TYPE_7;
		case 8:
			return PartListDO.COLOR_TYPE_8;
		case 9:
			return PartListDO.COLOR_TYPE_9;
		case 10:
			return PartListDO.COLOR_TYPE_10;
		default:
			return PartListDO.COLOR_TYPE_10;
		}
	}

	private Color getColorByPercent(double percent) {
		if (percent < 10) {
			return PartListDO.COLOR_READY_10;
		} else if (percent < 30) {
			return PartListDO.COLOR_READY_30;
		} else if (percent < 50) {
			return PartListDO.COLOR_READY_50;
		} else if (percent < 70) {
			return PartListDO.COLOR_READY_70;
		} else if (percent < 90) {
			return PartListDO.COLOR_READY_90;
		} else {
			return PartListDO.COLOR_READY_100;
		}
	}

	public void mouseDragged(MouseEvent mouseEvent) {
	}

	public void mouseMoved(MouseEvent mouseEvent) {
		if (image != null) {
			savedMouseEvent = mouseEvent;
			Point p = mouseEvent.getPoint();
			try {
				int rgb = image.getRGB((int) p.getX(), (int) p.getY());
				if (rgb == PartListDO.COLOR_TYPE_UEBERPRUEFT.getRGB()) {
					setToolTipText(ueberprueft);
				} else if (rgb == PartListDO.COLOR_TYPE_0.getRGB()) {
					setToolTipText(nichtVorhanden);
				} else if (rgb == PartListDO.COLOR_TYPE_OK.getRGB()) {
					setToolTipText(vorhanden);
				} else if (rgb == PartListDO.COLOR_TYPE_1.getRGB()) {
					setToolTipText("1" + quellen);
				} else if (rgb == PartListDO.COLOR_TYPE_2.getRGB()) {
					setToolTipText("2" + quellen);
				} else if (rgb == PartListDO.COLOR_TYPE_3.getRGB()) {
					setToolTipText("3" + quellen);
				} else if (rgb == PartListDO.COLOR_TYPE_4.getRGB()) {
					setToolTipText("4" + quellen);
				} else if (rgb == PartListDO.COLOR_TYPE_5.getRGB()) {
					setToolTipText("5" + quellen);
				} else if (rgb == PartListDO.COLOR_TYPE_6.getRGB()) {
					setToolTipText("6" + quellen);
				} else if (rgb == PartListDO.COLOR_TYPE_7.getRGB()) {
					setToolTipText("7" + quellen);
				} else if (rgb == PartListDO.COLOR_TYPE_8.getRGB()) {
					setToolTipText("8" + quellen);
				} else if (rgb == PartListDO.COLOR_TYPE_9.getRGB()) {
					setToolTipText("9" + quellen);
				} else if (rgb == PartListDO.COLOR_TYPE_10.getRGB()) {
					setToolTipText("10+" + quellen);
				} else if (rgb == PartListDO.COLOR_READY_10.getRGB()) {
					setToolTipText("0-10" + uebertragen);
				} else if (rgb == PartListDO.COLOR_READY_30.getRGB()) {
					setToolTipText("10-30" + uebertragen);
				} else if (rgb == PartListDO.COLOR_READY_50.getRGB()) {
					setToolTipText("30-50" + uebertragen);
				} else if (rgb == PartListDO.COLOR_READY_70.getRGB()) {
					setToolTipText("50-70" + uebertragen);
				} else if (rgb == PartListDO.COLOR_READY_90.getRGB()) {
					setToolTipText("70-90" + uebertragen);
				} else if (rgb == PartListDO.COLOR_READY_100.getRGB()) {
					setToolTipText("90-100" + uebertragen);
				} else {
					setToolTipText(null);
				}
			} catch (ArrayIndexOutOfBoundsException aoobE) {
				setToolTipText(null);
			}
		} else {
			setToolTipText(null);
		}
	}

	public void fireLanguageChanged() {
		try {
			LanguageSelector languageSelector = LanguageSelector.getInstance();
			vorhanden = ZeichenErsetzer
					.korrigiereUmlaute(languageSelector
							.getFirstAttrbuteByTagName(".root.mainform.Label4.caption"));
			nichtVorhanden = ZeichenErsetzer
					.korrigiereUmlaute(languageSelector
							.getFirstAttrbuteByTagName(".root.mainform.Label3.caption"));
			ueberprueft = ZeichenErsetzer
					.korrigiereUmlaute(languageSelector
							.getFirstAttrbuteByTagName(".root.mainform.Label1.caption"));
			quellen = ZeichenErsetzer
					.korrigiereUmlaute(languageSelector
							.getFirstAttrbuteByTagName(".root.javagui.downloadform.quellen"));
			uebertragen = ZeichenErsetzer
					.korrigiereUmlaute(languageSelector
							.getFirstAttrbuteByTagName(".root.javagui.downloadform.uebertragen"));
		} catch (Exception e) {
			if (logger.isEnabledFor(Level.ERROR)) {
				logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
			}
		}
	}
}
