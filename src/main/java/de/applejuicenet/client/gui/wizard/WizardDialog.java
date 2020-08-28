/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.wizard;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import de.applejuicenet.client.AppleJuiceClient;
import de.applejuicenet.client.fassade.ApplejuiceFassade;
import de.applejuicenet.client.fassade.shared.AJSettings;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.controller.OptionsManagerImpl;
import de.applejuicenet.client.gui.listener.LanguageListener;
import de.applejuicenet.client.shared.IconManager;
import de.tklsoft.gui.controls.TKLButton;
import de.tklsoft.gui.controls.TKLPanel;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/wizard/WizardDialog.java,v 1.10 2009/01/12 09:19:20 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 */
public class WizardDialog extends JDialog implements LanguageListener
{
   private Logger      logger;
   private WizardPanel aktuellesPanel;
   private WizardPanel schritt1      = new Schritt1Panel();
   private WizardPanel schritt2      = new Schritt2Panel();
   private WizardPanel schritt3;
   private WizardPanel schritt4      = new Schritt4Panel();
   private WizardPanel schritt5      = new Schritt5Panel();
   private TKLPanel    buttons       = new TKLPanel(new FlowLayout(FlowLayout.RIGHT));
   private TKLButton   zurueck       = new TKLButton();
   private TKLButton   weiter        = new TKLButton();
   private TKLButton   ende          = new TKLButton();
   private AJSettings  ajSettings;
   private boolean     regularClosed = false;

   public WizardDialog(Frame parent, boolean modal)
   {
      super(parent, modal);
      logger = Logger.getLogger(getClass());
      try
      {
         init();
         LanguageSelector ls = LanguageSelector.getInstance();

         ls.addLanguageListener(this);
      }
      catch(Exception e)
      {
         if(logger.isEnabledFor(Level.ERROR))
         {
            logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
         }
      }
   }

   public WizardDialog(JDialog parent, boolean modal, AJSettings ajSettings)
   {
      super(parent, modal);
      logger = Logger.getLogger(getClass());
      try
      {
         this.ajSettings = ajSettings;
         init();
         LanguageSelector ls = LanguageSelector.getInstance();

         ls.addLanguageListener(this);
      }
      catch(Exception e)
      {
         if(logger.isEnabledFor(Level.ERROR))
         {
            logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
         }
      }
   }

   private void init()
   {
      if(ajSettings == null)
      {
         ajSettings = AppleJuiceClient.getAjFassade().getAJSettings();
      }

      schritt3 = new Schritt3Panel(this, ajSettings);
      getContentPane().setLayout(new BorderLayout());
      ImageIcon icon1  = IconManager.getInstance().getIcon("wizardbanner");
      JLabel    label1 = new JLabel(icon1);

      schritt1.setVorherigesPanel(null);
      schritt1.setNaechstesPanel(schritt2);
      schritt2.setVorherigesPanel(schritt1);
      schritt2.setNaechstesPanel(schritt3);
      schritt3.setVorherigesPanel(schritt2);
      schritt3.setNaechstesPanel(schritt4);
      schritt4.setVorherigesPanel(schritt3);
      schritt4.setNaechstesPanel(schritt5);
      schritt5.setVorherigesPanel(schritt4);
      schritt5.setNaechstesPanel(null);

      addWindowListener(new WindowAdapter()
         {
            public void windowClosing(WindowEvent evt)
            {
               LanguageSelector.getInstance().removeLanguageListener(WizardDialog.this);
               OptionsManagerImpl.getInstance().setErsterStart(false);
               dispose();
            }
         });
      zurueck.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent ae)
            {
               if(aktuellesPanel.getVorherigesPanel() != null)
               {
                  getContentPane().remove(aktuellesPanel);
                  aktuellesPanel = aktuellesPanel.getVorherigesPanel();
                  getContentPane().add(aktuellesPanel, BorderLayout.CENTER);
                  weiter.setEnabled(true);
                  if(aktuellesPanel.getVorherigesPanel() == null)
                  {
                     zurueck.setEnabled(false);
                  }
                  else
                  {
                     zurueck.setEnabled(true);
                  }

                  if(aktuellesPanel == schritt3)
                  {
                     if(((Schritt3Panel) schritt3).isValidNickname())
                     {
                        setWeiterEnabled(true);
                     }
                     else
                     {
                        setWeiterEnabled(false);
                     }
                  }
                  else
                  {
                     setWeiterEnabled(true);
                  }

                  WizardDialog.this.validate();
                  WizardDialog.this.repaint();
               }
            }
         });
      weiter.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent ae)
            {
               if(aktuellesPanel.getNaechstesPanel() != null)
               {
                  getContentPane().remove(aktuellesPanel);
                  aktuellesPanel = aktuellesPanel.getNaechstesPanel();
                  getContentPane().add(aktuellesPanel, BorderLayout.CENTER);
                  zurueck.setEnabled(true);
                  if(aktuellesPanel.getNaechstesPanel() == null)
                  {
                     weiter.setEnabled(false);
                  }
                  else
                  {
                     if(aktuellesPanel == schritt3)
                     {
                        if(((Schritt3Panel) schritt3).isValidNickname())
                        {
                           setWeiterEnabled(true);
                        }
                        else
                        {
                           setWeiterEnabled(false);
                        }
                     }
                     else
                     {
                        setWeiterEnabled(true);
                     }
                  }

                  WizardDialog.this.validate();
                  WizardDialog.this.repaint();
               }
            }
         });
      ende.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent ae)
            {
               regularClosed = true;
               close();
            }
         });
      buttons.add(zurueck);
      buttons.add(weiter);
      buttons.add(ende);
      buttons.setBackground(Color.WHITE);
      zurueck.setEnabled(false);

      getContentPane().add(label1, BorderLayout.NORTH);
      getContentPane().add(schritt1, BorderLayout.CENTER);
      aktuellesPanel = schritt1;
      getContentPane().add(buttons, BorderLayout.SOUTH);
      setSize(icon1.getIconWidth(), icon1.getIconHeight() + 180 + buttons.getPreferredSize().height);
      setResizable(false);
      fireLanguageChanged();
   }

   public boolean isRegularClosed()
   {
      return regularClosed;
   }

   private void close()
   {
      LanguageSelector.getInstance().removeLanguageListener(this);
      int result = JOptionPane.showConfirmDialog(this,
                                                 LanguageSelector.getInstance().getFirstAttrbuteByTagName("connect.remember.caption") +
                                                 " ?", "appleJuice Client", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

      if(result == JOptionPane.YES_OPTION)
      {
         if(((Schritt3Panel) schritt3).isValidNickname())
         {
            ConnectionKind connection = ((Schritt4Panel) schritt4).getVerbindungsart();

            ajSettings.setNick(((Schritt3Panel) schritt3).getNickname());
            ajSettings.setMaxUpload(connection.getMaxUpload() * 1024);
            ajSettings.setMaxDownload(connection.getMaxDownload() * 1024);
            ajSettings.setMaxNewConnectionsPerTurn(connection.getMaxNewConnectionsPro10Sek());
            AppleJuiceClient.getAjFassade().saveAJSettings(ajSettings);
         }
      }
      else
      {
         regularClosed = false;
      }

      OptionsManagerImpl.getInstance().setErsterStart(false);
      dispose();
   }

   public void setWeiterEnabled(boolean enabled)
   {
      weiter.setEnabled(enabled);
   }

   public void fireLanguageChanged()
   {
      LanguageSelector languageSelector = LanguageSelector.getInstance();

      setTitle(languageSelector.getFirstAttrbuteByTagName("javagui.wizard.titel"));
      zurueck.setText(languageSelector.getFirstAttrbuteByTagName("javagui.wizard.zurueck"));
      weiter.setText(languageSelector.getFirstAttrbuteByTagName("javagui.wizard.weiter"));
      ende.setText(languageSelector.getFirstAttrbuteByTagName("javagui.wizard.ende"));
      schritt1.fireLanguageChanged();
      schritt2.fireLanguageChanged();
      schritt3.fireLanguageChanged();
      schritt4.fireLanguageChanged();
      schritt5.fireLanguageChanged();
      repaint();
   }
}
