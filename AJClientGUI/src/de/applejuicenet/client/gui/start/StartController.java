package de.applejuicenet.client.gui.start;

import java.util.Map;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import org.apache.log4j.Level;

import de.applejuicenet.client.AppleJuiceClient;
import de.applejuicenet.client.fassade.ApplejuiceFassade;
import de.applejuicenet.client.fassade.controller.dac.ServerDO;
import de.applejuicenet.client.fassade.listener.DataUpdateListener;
import de.applejuicenet.client.fassade.shared.Information;
import de.applejuicenet.client.fassade.shared.NetworkInfo;
import de.applejuicenet.client.fassade.shared.ZeichenErsetzer;
import de.applejuicenet.client.gui.components.GuiController;
import de.applejuicenet.client.gui.components.util.Value;
import de.applejuicenet.client.gui.controller.LanguageSelector;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/start/StartController.java,v 1.7 2005/01/18 17:35:29 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 */

public class StartController extends GuiController {

	private static StartController instance = null;
	
	private final StartPanel startPanel;
	private boolean firstChange = true;
	private boolean firewalled = false;
	private String status2Text;
	private String verbindungNachrichtText;
	private NetworkInfo netInfo = null;
	private Information information = null;
	private String firewallWarningText;
	private String keinServer = "";
	private String verbindungenText;

	private StartController() {
		super();
		startPanel = new StartPanel(this);
		try{
			init();
		} catch (Exception e) {
			if (logger.isEnabledFor(Level.ERROR)) {
				logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
			}
		}
	}

	public static synchronized StartController getInstance() {
		if (null == instance) {
			instance = new StartController();
		}
		return instance;
	}

	private void init() {
		HyperlinkAdapter hyperlinkAdapter = new HyperlinkAdapter(startPanel.getServerMessagePane());
		startPanel.getServerMessagePane()
				.addHyperlinkListener(hyperlinkAdapter);
		startPanel.getNachrichtenPane().addHyperlinkListener(hyperlinkAdapter);
		startPanel.getFaqPane().addHyperlinkListener(hyperlinkAdapter);

		AppleJuiceClient.getAjFassade().addDataUpdateListener(this,
				DataUpdateListener.NETINFO_CHANGED);
		AppleJuiceClient.getAjFassade().addDataUpdateListener(this,
				DataUpdateListener.INFORMATION_CHANGED);
		LanguageSelector.getInstance().addLanguageListener(this);
	}

	public Value[] getCustomizedValues(){
		return null;
	}

	public void fireAction(int actionId, Object source) {
		// nix
	}

	public JComponent getComponent() {
		return startPanel;
	}

	public void componentSelected() {
		// nix zu tun
	}

	public void componentLostSelection() {
		// nix zu tun
	}

	protected void languageChanged() {
		LanguageSelector languageSelector = LanguageSelector.getInstance();
		keinServer = languageSelector
			.getFirstAttrbuteByTagName(".root.javagui.mainform.keinserver");
		status2Text = ZeichenErsetzer.korrigiereUmlaute(languageSelector
				.getFirstAttrbuteByTagName(".root.mainform.status.status2"));
		StringBuffer temp = new StringBuffer(status2Text);
		int pos;
		if (netInfo != null) {
			pos = temp.indexOf("%d");
			if (pos != -1) {
				temp.replace(pos, pos + 2, netInfo
						.getAJUserGesamtAsStringWithPoints());
			}
			pos = temp.indexOf("%d");
			if (pos != -1) {
				temp.replace(pos, pos + 2, netInfo
						.getAJAnzahlDateienAsStringWithPoints());
			}
			pos = temp.indexOf("%s");
			if (pos != -1) {
				temp.replace(pos, pos + 2, netInfo
						.getAJGesamtShareWithPoints(0));
			}
		} else {
			pos = temp.indexOf("%d");
			if (pos != -1) {
				temp.replace(pos, pos + 2, "0");
			}
			pos = temp.indexOf("%d");
			if (pos != -1) {
				temp.replace(pos, pos + 2, "0");
			}
			pos = temp.indexOf("%s");
			if (pos != -1) {
				temp.replace(pos, pos + 2, "0 MB");
			}

		}
		startPanel.getLblStatus().setText(temp.toString());
		firewallWarningText = ZeichenErsetzer
			.korrigiereUmlaute(languageSelector
				.getFirstAttrbuteByTagName(".root.mainform.firewallwarning.caption"));
		if (firewalled) {
			startPanel.getLblWarnung().setVisible(true);
			startPanel.getLblWarnungIcon().setVisible(true);
			startPanel.getLblFirewallWarning().setText(firewallWarningText);
		} else {
			startPanel.getLblWarnung().setVisible(false);
			startPanel.getLblWarnungIcon().setVisible(false);
			startPanel.getLblFirewallWarning().setText("");
		}
		verbindungNachrichtText = ZeichenErsetzer.korrigiereUmlaute(languageSelector
				.getFirstAttrbuteByTagName(".root.mainform.html10"));
		temp = new StringBuffer(verbindungNachrichtText);
		pos = temp.indexOf("%s");
		if (pos != -1) {
			temp.replace(pos, pos + 2, keinServer);
		}
		pos = temp.indexOf("%d");
		if (pos != -1) {
			Map servers = AppleJuiceClient.getAjFassade().getAllServer();
			int count;
			if (servers != null) {
				count = servers.size();
			} else {
				count = 0;
			}
			temp.replace(pos, pos + 2, Integer.toString(count));
		}
		startPanel.getLblVerbindungsnachricht().setText(temp.toString());
		verbindungenText = ZeichenErsetzer
			.korrigiereUmlaute(languageSelector
				.getFirstAttrbuteByTagName(".root.mainform.status.status0"));
		temp = new StringBuffer(verbindungenText);
		pos = temp.indexOf("%d");
		if (pos != -1) {
			if (information != null) {
				temp.replace(pos, pos + 2, Long.toString(information
						.getOpenConnections()));
			} else {
				temp.replace(pos, pos + 2, "0");
			}
		}
		startPanel.getLblVerbindungen().setText(temp.toString());
		startPanel.getLblNetzwerk().setText("<html><font><h2>"
				+ ZeichenErsetzer.korrigiereUmlaute(languageSelector
						.getFirstAttrbuteByTagName(".root.mainform.html7"))
				+ "</h2></font></html>");
		startPanel.getLblNeuigkeiten().setText("<html><font><h2>"
						+ ZeichenErsetzer
								.korrigiereUmlaute(languageSelector
										.getFirstAttrbuteByTagName(".root.mainform.html13"))
						+ "</h2></font></html>");
		startPanel.getLblDeinClient().setText("<html><font><h2>"
				+ ZeichenErsetzer.korrigiereUmlaute(languageSelector
						.getFirstAttrbuteByTagName(".root.mainform.html1"))
				+ "</h2></font></html>");
		startPanel.getLblWarnung()
				.setText("<html><font><h2>"
						+ ZeichenErsetzer
								.korrigiereUmlaute(languageSelector
										.getFirstAttrbuteByTagName(".root.mainform.html15"))
						+ "</h2></font></html>");
	}

	protected void contentChanged(int type, final Object content) {
		if (type == DataUpdateListener.NETINFO_CHANGED) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					try {
						if (firstChange) {
							firstChange = false;
							new NachrichtenWorker(StartController.this,
									startPanel.getLblVersion(), startPanel
											.getNachrichtenPane()).start();
						}
						netInfo = (NetworkInfo) content;
						StringBuffer temp = new StringBuffer(status2Text);
						int pos = temp.indexOf("%d");
						if (pos != -1) {
							temp.replace(pos, pos + 2, netInfo
									.getAJUserGesamtAsStringWithPoints());
						}
						pos = temp.indexOf("%d");
						if (pos != -1) {
							temp.replace(pos, pos + 2, netInfo
									.getAJAnzahlDateienAsStringWithPoints());
						}
						pos = temp.indexOf("%s");
						if (pos != -1) {
							temp.replace(pos, pos + 2, netInfo
									.getAJGesamtShareWithPoints(0));
						}
						startPanel.getLblStatus().setText(temp.toString());
						if (netInfo.isFirewalled() != firewalled) {
							firewalled = !firewalled;
							startPanel.getLblWarnung().setVisible(firewalled);
							startPanel.getLblWarnungIcon().setVisible(
									firewalled);
							if (firewalled) {
								startPanel.getLblFirewallWarning().setText(firewallWarningText);
							} else {
								startPanel.getLblFirewallWarning().setText("");
							}
							startPanel.repaint();
						}
						String tmp = netInfo.getWelcomeMessage();
						if (tmp.compareTo(startPanel.getServerMessagePane()
								.getText()) != 0) {
							startPanel.getServerMessagePane().setText(tmp);
							startPanel.getServerMessagePane().setCaretPosition(
									0);
						}
					} catch (Exception e) {
						if (logger.isEnabledFor(Level.ERROR)) {
							logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
						}
					}
				}
			});
		} else if (type == DataUpdateListener.INFORMATION_CHANGED) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					try {
						information = (Information) content;
						StringBuffer temp = new StringBuffer(verbindungNachrichtText);
						int pos = temp.indexOf("%s");
						if (pos != -1) {
							if (information.getVerbindungsStatus() == Information.VERBUNDEN) {
								if (information.getServerName() == null
										|| information.getServerName().length() == 0) {
									ServerDO serverDO = information
											.getServerDO();
									if (serverDO != null) {
										String tmp = serverDO.getHost() + ":"
												+ serverDO.getPort();
										temp.replace(pos, pos + 2, tmp);
									} else {
										temp.replace(pos, pos + 2, "?");
									}
								} else {
									temp.replace(pos, pos + 2, information
											.getServerName());
								}
							} else {
								temp.replace(pos, pos + 2, keinServer);
							}
							pos = temp.indexOf("%d");
							if (pos != -1) {
								temp.replace(pos, pos + 2, Integer
										.toString(AppleJuiceClient.getAjFassade()
												.getAllServer().size()));
							}
						}
						startPanel.getLblVerbindungsnachricht().setText(temp.toString());
						temp = new StringBuffer(verbindungenText);
						pos = temp.indexOf("%d");
						if (pos != -1) {
							temp.replace(pos, pos + 2, Long.toString(
									information.getOpenConnections()));
						}
						startPanel.getLblVerbindungen().setText(temp.toString());
					} catch (Exception e) {
						if (logger.isEnabledFor(Level.ERROR)) {
							logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
						}
					}
				}
			});
		}
	}
}
