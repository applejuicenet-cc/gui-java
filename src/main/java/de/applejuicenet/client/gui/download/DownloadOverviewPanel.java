/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */
package de.applejuicenet.client.gui.download;


/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/download/DownloadOverviewPanel.java,v 1.8 2009/02/01 14:49:17 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.text.DecimalFormat;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import de.applejuicenet.client.AppleJuiceClient;
import de.applejuicenet.client.fassade.ApplejuiceFassade;
import de.applejuicenet.client.fassade.entity.Download;
import de.applejuicenet.client.fassade.entity.DownloadSource;
import de.applejuicenet.client.fassade.entity.PartList;
import de.applejuicenet.client.fassade.exception.WebSiteNotFoundException;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.listener.LanguageListener;
import de.applejuicenet.client.shared.Settings;

public class DownloadOverviewPanel extends JPanel implements LanguageListener
{
   private DownloadPartListPanel actualDlOverviewTable;
   private JLabel                actualDLDateiName    = new JLabel();
   private JLabel                label5               = new JLabel();
   private JLabel                label4               = new JLabel();
   private JLabel                label3               = new JLabel();
   private JLabel                label2               = new JLabel();
   private JLabel                label1               = new JLabel();
   private Logger                logger;
   private JCheckBox             holeListe            = new JCheckBox();
   private PartListWorkerThread  partListWorkerThread = null;
   private DownloadPanel         downloadPanel;
   private String                verfuegbar;
   private DecimalFormat         decimalFormat        = new DecimalFormat("#.##");
   private Object                lastPartlistObject   = null;

   public DownloadOverviewPanel(DownloadPanel parent)
   {
      logger = Logger.getLogger(getClass());
      try
      {
         downloadPanel         = parent;
         actualDlOverviewTable = DownloadPartListPanel.getInstance();
         init();
         LanguageSelector.getInstance().addLanguageListener(this);
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
      holeListe.setSelected(Settings.getSettings().isDownloadUebersicht());
      holeListe.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent e)
            {
               if(!holeListe.isSelected() && null != partListWorkerThread)
               {
                  partListWorkerThread.interrupt();
                  partListWorkerThread = null;
                  actualDLDateiName.setText(null);
                  actualDlOverviewTable.setPartList(null, null);
               }
               else if(holeListe.isSelected() && null != lastPartlistObject)
               {
                  if(lastPartlistObject instanceof Download)
                  {
                     setDownload((Download) lastPartlistObject);
                  }
                  else
                  {
                     setDownloadSource((DownloadSource) lastPartlistObject);
                  }
               }

               Settings settings = Settings.getSettings();

               settings.setDownloadUebersicht(holeListe.isSelected());
               settings.save();
            }
         });
      setLayout(new BorderLayout());
      JPanel tempPanel1 = new JPanel();

      tempPanel1.setLayout(new FlowLayout());

      JLabel gelb = new JLabel("     ");

      gelb.setOpaque(true);
      gelb.setBackground(Color.YELLOW);
      tempPanel1.add(gelb);
      tempPanel1.add(label5);

      JLabel blau = new JLabel("     ");

      blau.setOpaque(true);
      blau.setBackground(Color.BLUE);
      tempPanel1.add(blau);
      tempPanel1.add(label4);

      JLabel red = new JLabel("     ");

      red.setOpaque(true);
      red.setBackground(Color.RED);
      tempPanel1.add(red);
      tempPanel1.add(label3);

      JLabel black = new JLabel("     ");

      black.setOpaque(true);
      black.setBackground(Color.BLACK);
      tempPanel1.add(black);
      tempPanel1.add(label2);

      JLabel green = new JLabel("     ");

      green.setOpaque(true);
      green.setBackground(Color.GREEN);
      tempPanel1.add(green);
      tempPanel1.add(label1);

      JPanel panel3 = new JPanel(new BorderLayout());

      panel3.add(holeListe, BorderLayout.WEST);
      panel3.add(tempPanel1, BorderLayout.CENTER);

      add(panel3, BorderLayout.NORTH);
      actualDLDateiName.setPreferredSize(new Dimension(actualDLDateiName.getPreferredSize().width, 17));
      JPanel panel1 = new JPanel(new BorderLayout());

      panel1.add(actualDLDateiName, BorderLayout.NORTH);
      panel1.add(actualDlOverviewTable, BorderLayout.CENTER);
      add(panel1, BorderLayout.CENTER);
   }

   public void setDownload(Download download)
   {
      lastPartlistObject = download;
      if(!holeListe.isSelected())
      {
         return;
      }

      try
      {
         if(partListWorkerThread != null)
         {
            if(partListWorkerThread.isInterrupted())
            {
               partListWorkerThread = null;
            }
            else
            {
               if(partListWorkerThread.getObjectDO() == download)
               {
                  return;
               }
               else
               {
                  partListWorkerThread.cancel();
                  partListWorkerThread = null;
               }
            }
         }

         partListWorkerThread = new PartListWorkerThread();
         partListWorkerThread.setDownload(download);
         partListWorkerThread.start();
      }
      catch(Exception e)
      {
         if(partListWorkerThread != null)
         {
            partListWorkerThread.cancel();
            partListWorkerThread = null;
         }

         if(logger.isEnabledFor(Level.ERROR))
         {
            logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
         }
      }
   }

   public void setDownloadSource(DownloadSource downloadSource)
   {
      lastPartlistObject = downloadSource;
      if(!holeListe.isSelected())
      {
         return;
      }

      try
      {
         if(partListWorkerThread != null)
         {
            if(partListWorkerThread.isInterrupted())
            {
               partListWorkerThread = null;
            }
            else
            {
               partListWorkerThread.cancel();
               partListWorkerThread = null;
            }
         }

         partListWorkerThread = new PartListWorkerThread();
         partListWorkerThread.setDownloadSourceDO(downloadSource);
         partListWorkerThread.start();
      }
      catch(Exception e)
      {
         if(partListWorkerThread != null)
         {
            partListWorkerThread.cancel();
            partListWorkerThread = null;
         }

         if(logger.isEnabledFor(Level.ERROR))
         {
            logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
         }
      }
   }

   public void fireLanguageChanged()
   {
      try
      {
         LanguageSelector languageSelector = LanguageSelector.getInstance();

         label5.setText(languageSelector.getFirstAttrbuteByTagName("javagui.downloadform.aktiveuebertragung"));
         label4.setText(languageSelector.getFirstAttrbuteByTagName("mainform.Label4.caption"));
         label3.setText(languageSelector.getFirstAttrbuteByTagName("mainform.Label3.caption"));
         label2.setText(languageSelector.getFirstAttrbuteByTagName("mainform.Label2.caption"));
         label1.setText(languageSelector.getFirstAttrbuteByTagName("mainform.Label1.caption"));
         holeListe.setText(languageSelector.getFirstAttrbuteByTagName("javagui.downloadform.partlisteanzeigen"));
         verfuegbar = languageSelector.getFirstAttrbuteByTagName("javagui.downloadform.verfuegbar");
      }
      catch(Exception e)
      {
         if(logger.isEnabledFor(Level.ERROR))
         {
            logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
         }
      }
   }

   private class PartListWorkerThread extends Thread
   {
      private Object  objectDO = null;
      private boolean firstRun = true;

      public void run()
      {
         while(!interrupted())
         {
            if(objectDO == null)
            {
               break;
            }

            boolean shortPause = false;

            if(objectDO instanceof Download)
            {
               if(((Download) objectDO).getStatus() == Download.PAUSIERT ||
                     ((Download) objectDO).getStatus() == Download.SUCHEN_LADEN)
               {
                  shortPause = workDownloadDO((Download) objectDO);
               }
               else
               {
                  objectDO = null;
                  break;
               }
            }
            else
            {
               shortPause = workDownloadSourceDO((DownloadSource) objectDO);
            }

            if(shortPause)
            {
               try
               {
                  sleep(5000);
                  firstRun = false;
                  continue;
               }
               catch(InterruptedException iE)
               {
                  interrupt();
               }
            }
            else
            {
               break;
            }
         }
      }

      public Object getObjectDO()
      {
         return objectDO;
      }

      private boolean workDownloadDO(Download download)
      {
         if(download.getStatus() != Download.FERTIGSTELLEN && download.getStatus() != Download.FERTIG)
         {
            String dateiNameText = " " + download.getFilename() + " (" + download.getTemporaryFileNumber() + ".data) ";

            if(firstRun)
            {
               actualDLDateiName.setText(dateiNameText);
            }

            PartList partList = null;

            try
            {
               partList = AppleJuiceClient.getAjFassade().getPartList(download);
            }
            catch(WebSiteNotFoundException wsnfE)
            {
               // Core ist wahrscheinlich zurzeit ueberlastet
               partList = null;
            }

            if(partList != null && !isInterrupted())
            {
               String tmp = verfuegbar.replaceFirst("%s", decimalFormat.format(partList.getProzentVerfuegbar()));

               actualDLDateiName.setText(dateiNameText + " - " + tmp);
               actualDlOverviewTable.setPartList(partList, new Integer(download.getId()));
            }

            return true;
         }
         else
         {
            return false;
         }
      }

      private boolean workDownloadSourceDO(DownloadSource downloadSoure)
      {
         PartList partList;
         String   tmp = downloadSoure.getFilename() + " (" + downloadSoure.getNickname() + ")";

         actualDLDateiName.setText(tmp);
         try
         {
            partList = AppleJuiceClient.getAjFassade().getPartList(downloadSoure);
         }
         catch(WebSiteNotFoundException ex)
         {
            // Core ist wahrscheinlich zurzeit ueberlastet
            partList = null;
         }

         if(partList != null && !isInterrupted())
         {
            actualDLDateiName.setText(tmp + " - " +
                                      verfuegbar.replaceFirst("%s", decimalFormat.format(partList.getProzentVerfuegbar())));
            actualDlOverviewTable.setPartList(partList, new Integer(downloadSoure.getId()));
         }

         return false;
      }

      public void setDownload(Download download)
      {
         if(download == null)
         {
            objectDO = null;
            clear();
         }
         else if(objectDO != download)
         {
            objectDO = download;
            clear();
         }
      }

      public void setDownloadSourceDO(DownloadSource downloadSoure)
      {
         if(downloadSoure == null)
         {
            objectDO = null;
            clear();
         }
         else if(objectDO != downloadSoure)
         {
            objectDO = downloadSoure;
            clear();
         }
      }

      private void clear()
      {
         actualDLDateiName.setText("");
         actualDlOverviewTable.setPartList(null, null);
      }

      public void cancel()
      {
         interrupt();
      }
   }
}
