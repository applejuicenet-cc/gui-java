/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.options;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

import org.apache.log4j.Logger;

import de.applejuicenet.client.fassade.ApplejuiceFassade;
import de.applejuicenet.client.gui.connect.QuickConnectionSettingsDialog;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.shared.ConnectionSettings;
import de.applejuicenet.client.shared.IconManager;
import de.applejuicenet.client.shared.NumberInputVerifier;
import de.tklsoft.gui.controls.TKLTextField;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/options/ODConnectionPanel.java,v 1.10 2009/01/12 09:19:20 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 */
public class ODConnectionPanel extends JPanel implements OptionsRegister
{
   private boolean                       dirty                         = false;
   private JLabel                        label1;
   private JLabel                        label3;
   private JLabel                        label4;
   private TKLTextField                  host                          = new TKLTextField();
   private TKLTextField                  port                          = new TKLTextField();
   private JPasswordField                passwortNeu                   = new JPasswordField();
   private ConnectionSettings            remote;
   private Logger                        logger;
   private boolean                       showPort                      = false;
   private QuickConnectionSettingsDialog quickConnectionSettingsDialog;
   private Icon                          menuIcon;
   private String                        menuText;

   public ODConnectionPanel(ConnectionSettings remote, QuickConnectionSettingsDialog quickConnectionSettingsDialog, boolean showPort)
   {
      logger = Logger.getLogger(getClass());
      try
      {
         this.showPort                      = showPort;
         this.quickConnectionSettingsDialog = quickConnectionSettingsDialog;
         this.remote                        = remote;
         init();
      }
      catch(Exception e)
      {
         logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
      }
   }

   public ODConnectionPanel(ConnectionSettings remote, QuickConnectionSettingsDialog quickConnectionSettingsDialog)
   {
      this(remote, quickConnectionSettingsDialog, false);
   }

   private void init() throws Exception
   {
      setLayout(new BorderLayout());
      IconManager im = IconManager.getInstance();

      menuIcon = im.getIcon("opt_passwort");
      JPanel     panel1 = new JPanel(new GridBagLayout());
      FlowLayout flowL = new FlowLayout();

      flowL.setAlignment(FlowLayout.RIGHT);
      JPanel           panel2 = new JPanel(flowL);

      LanguageSelector languageSelector = LanguageSelector.getInstance();

      label1 = new JLabel(languageSelector.getFirstAttrbuteByTagName("javagui.options.remote.host"));
      label3 = new JLabel(languageSelector.getFirstAttrbuteByTagName("javagui.options.remote.passwortNeu"));
      menuText = languageSelector.getFirstAttrbuteByTagName("einstform.pwsheet.caption");
      label4   = new JLabel("Port");

      host.setText(remote.getHost());
      host.addFocusListener(new HostFocusListener());
      passwortNeu.addFocusListener(new PasswortNeuFocusListener());

      KeyAdapter keyAdapter = new ShortcutKeyListener();

      if(quickConnectionSettingsDialog != null)
      {
         host.addKeyListener(keyAdapter);
         port.addKeyListener(keyAdapter);
         passwortNeu.addKeyListener(keyAdapter);
      }

      port.setDocument(new NumberInputVerifier());
      port.setText(Integer.toString(remote.getXmlPort()));
      port.addFocusListener(new PortFocusListener());

      enableControls(true);

      GridBagConstraints constraints = new GridBagConstraints();

      constraints.anchor      = GridBagConstraints.NORTH;
      constraints.fill        = GridBagConstraints.BOTH;
      constraints.gridx       = 0;
      constraints.gridy       = 0;
      constraints.insets.top  = 5;
      constraints.insets.left = 5;

      panel1.add(label1, constraints);

      int gridy = 1;

      if(showPort)
      {
         constraints.gridy = gridy;
         gridy++;
         panel1.add(label4, constraints);
      }

      constraints.gridy = gridy;
      gridy++;
      panel1.add(label3, constraints);

      constraints.insets.right = 5;
      constraints.gridy        = 0;
      constraints.gridx        = 1;
      constraints.weightx      = 1;
      panel1.add(host, constraints);

      gridy = 1;
      if(showPort)
      {
         constraints.gridy = gridy;
         gridy++;
         panel1.add(port, constraints);
      }

      constraints.gridy = gridy;
      gridy++;
      panel1.add(passwortNeu, constraints);

      constraints.gridy = gridy;
      gridy++;
      constraints.gridx     = 0;
      constraints.gridwidth = 2;
      panel1.add(panel2, constraints);

      add(panel1, BorderLayout.NORTH);
      if(quickConnectionSettingsDialog != null)
      {
         label3.setText(languageSelector.getFirstAttrbuteByTagName("einstform.pwsheet.caption"));
      }

      host.confirmNewValue();
      port.confirmNewValue();
   }

   public void setFocusOnPassword()
   {
      this.passwortNeu.requestFocus();
   }

   public boolean isDirty()
   {
      return dirty;
   }

   public void enableControls(boolean enable)
   {
      host.setEnabled(enable);
      passwortNeu.setEnabled(enable);
      label1.setEnabled(enable);
      label3.setEnabled(enable);
   }

   public Icon getIcon()
   {
      return menuIcon;
   }

   public String getMenuText()
   {
      return menuText;
   }

   public void setHost(String host)
   {
      this.host.setText(host);
      this.remote.setHost(host);
   }

   public void setXMLPort(String port)
   {
      this.port.setText(port);
      this.remote.setXmlPort(Integer.parseInt(port));
   }

   public void reloadSettings()
   {

      // nothing to do...
   }

   public String getPassword()
   {
      return new String(passwortNeu.getPassword());
   }

   public String getHost()
   {
      return host.getText();
   }

   public Integer getPort()
   {
      return new Integer(port.getText());
   }

   class PortFocusListener extends FocusAdapter
   {
      public void focusLost(FocusEvent e)
      {
         dirty = true;
         remote.setXmlPort(Integer.parseInt(port.getText()));
      }
   }


   class ShortcutKeyListener extends KeyAdapter
   {
      public void keyPressed(KeyEvent ke)
      {
         if(ke.getKeyCode() == KeyEvent.VK_ENTER)
         {
            dirty = true;
            remote.setHost(host.getText());
            remote.setXmlPort(Integer.parseInt(port.getText()));
            remote.setNewPassword(new String(passwortNeu.getPassword()));
            quickConnectionSettingsDialog.pressOK();
         }
         else if(ke.getKeyCode() == KeyEvent.VK_ESCAPE)
         {
            quickConnectionSettingsDialog.pressAbbrechen();
         }
         else
         {
            super.keyPressed(ke);
         }
      }
   }


   class PasswortNeuFocusListener extends FocusAdapter
   {
      public void focusLost(FocusEvent e)
      {
         dirty = true;
         remote.setNewPassword(new String(passwortNeu.getPassword()));
      }
   }


   class HostFocusListener extends FocusAdapter
   {
      public void focusLost(FocusEvent e)
      {
         if(remote.getHost().compareTo(host.getText()) != 0)
         {
            dirty = true;
            remote.setHost(host.getText());
         }
      }
   }
}
