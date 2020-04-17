/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

import java.net.Socket;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.StringTokenizer;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.HTMLLayout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import de.applejuicenet.client.fassade.ApplejuiceFassade;
import de.applejuicenet.client.fassade.controller.CoreConnectionSettingsHolder;
import de.applejuicenet.client.fassade.exception.IllegalArgumentException;
import de.applejuicenet.client.fassade.shared.AJSettings;
import de.applejuicenet.client.fassade.shared.ProxySettings;
import de.applejuicenet.client.fassade.shared.WebsiteContentLoader;
import de.applejuicenet.client.gui.AppleJuiceDialog;
import de.applejuicenet.client.gui.UpdateInformationDialog;
import de.applejuicenet.client.gui.components.listener.KeyStates;
import de.applejuicenet.client.gui.connect.ConnectFrame;
import de.applejuicenet.client.gui.connect.QuickConnectionSettingsDialog;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.controller.LinkListener;
import de.applejuicenet.client.gui.controller.OptionsManagerImpl;
import de.applejuicenet.client.gui.controller.PositionManager;
import de.applejuicenet.client.gui.controller.PositionManagerImpl;
import de.applejuicenet.client.gui.controller.ProxyManagerImpl;
import de.applejuicenet.client.gui.wizard.WizardDialog;
import de.applejuicenet.client.shared.ConnectionSettings;
import de.applejuicenet.client.shared.IconManager;
import de.applejuicenet.client.shared.SoundPlayer;
import de.applejuicenet.client.shared.Splash;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/AppleJuiceClient.java,v 1.109 2009/02/12 13:11:40 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r [aj@tkl-soft.de]
 *
 */
public class AppleJuiceClient
{
   public static Splash                        splash           = null;
   private static Logger                       logger           = Logger.getLogger(AppleJuiceClient.class);
   private static String                       fileAppenderPath;
   private static HTMLLayout                   layout;
   private static ApplejuiceFassade            ajFassade        = null;
   private static CoreConnectionSettingsHolder conn             = null;
   private static String                       rootDirectory    = null;

   /**
    * @return Returns the path of the Client-GUI.
    */
   public static String getPath()
   {
      return getRootDirectory();
   }

   public static synchronized ApplejuiceFassade getAjFassade()
   {
      if(ajFassade == null)
      {
         ConnectionSettings rm = OptionsManagerImpl.getInstance().getRemoteSettings();

         try
         {
            conn      = new CoreConnectionSettingsHolder(rm.getHost(), new Integer(rm.getXmlPort()), rm.getOldPassword(), false);
            ajFassade = new ApplejuiceFassade(conn);
         }
         catch(IllegalArgumentException e)
         {
            logger.error(e);
         }
      }

      return ajFassade;
   }

   public static CoreConnectionSettingsHolder getCoreConnectionSettingsHolder()
   {
      return conn;
   }

   public static HTMLLayout getLoggerHtmlLayout()
   {
      return layout;
   }

   public static String getLoggerFileAppenderPath()
   {
      return fileAppenderPath;
   }

   public static void main(String[] args)
   {
      AppleJuiceClientTG tg       = new AppleJuiceClientTG();
      final String[]     myargs   = args;
      Runnable           runnable = new Runnable()
      {
         public void run()
         {
            AppleJuiceClient.runmain(myargs);
         }
      };

      Thread t = new Thread(tg, runnable, "appleJuiceCoreGUI");

      t.start();
   }

   public static void runmain(String[] args)
   {
      boolean      processLink    = false;
      String       link           = "";
      boolean      doubleInstance = false;
      LinkListener linkListener   = null;

      if(args != null && args.length > 0)
      {
         try
         {
            for(String curArg : args)
            {
               if(curArg.indexOf("-path=") != -1)
               {
                  System.setProperty("user.dir", curArg.substring(6));
                  break;
               }
            }

            boolean hilfeAusgegeben = false;

            for(String curArg : args)
            {
               if(curArg.compareTo("-help") == 0)
               {
                  if(hilfeAusgegeben)
                  {
                     continue;
                  }

                  System.out.println();
                  System.out.println(" -help                       Diese Uebersicht.");
                  System.out.println(" -path=<pfad>                Ausfuehrpfad setzen. Alles im GUI ist relativ zu diesem.");
                  System.out.println(" -link=<md5Passwort|link>    ajfsp-Link ans GUI uebergeben. " +
                                     " Das GUI wird ggf gestartet.");
                  System.out.println();
                  hilfeAusgegeben = true;
               }
               else if(curArg.indexOf("-command=") != -1)
               {
                  if(linkListener == null)
                  {
                     try
                     {
                        linkListener = new LinkListener();
                     }
                     catch(IOException ex)
                     {
                        //bereits ein GUI vorhanden, also GUI schliessen
                        doubleInstance = true;
                     }
                  }

                  if(doubleInstance)
                  {
                     int             PORT     = OptionsManagerImpl.getInstance().getLinkListenerPort();
                     String          passwort = OptionsManagerImpl.getInstance().getRemoteSettings().getOldPassword();
                     Socket          socket   = new Socket("localhost", PORT);
                     PrintStream     out      = new PrintStream(socket.getOutputStream());
                     DataInputStream in       = new DataInputStream(socket.getInputStream());

                     out.println(passwort + "|" + curArg);
                     BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                     String         line = reader.readLine();

                     System.out.println(line);
                     socket.close();
                     System.exit(1);
                  }
                  else
                  {
                     System.out.println("appleJuice-JavaGUI nicht gestartet");
                     System.exit(1);
                  }
               }
               else if(curArg.indexOf("-link=") != -1 && curArg.length() > "-link=".length() + 1)
               {
                  link = curArg.substring(curArg.indexOf("-link=") + "-link=".length());
                  if(linkListener == null)
                  {
                     try
                     {
                        linkListener = new LinkListener();
                     }
                     catch(IOException ex)
                     {
                        //bereits ein GUI vorhanden, also GUI schliessen
                        doubleInstance = true;
                     }
                  }

                  if(doubleInstance)
                  {
                     int         PORT     = OptionsManagerImpl.getInstance().getLinkListenerPort();
                     String      passwort = OptionsManagerImpl.getInstance().getRemoteSettings().getOldPassword();
                     Socket      socket   = new Socket("localhost", PORT);
                     PrintStream out      = new PrintStream(socket.getOutputStream());

                     out.println(passwort + "|" + curArg);
                     socket.close();
                     //war nur Linkprocessing, also GUI schliessen
                     System.exit(1);
                  }
                  else
                  {
                     linkListener.processLink(link, "");
                  }
               }
            }
         }
         catch(IOException ioE)
         {
            //Keine bisherige GUI-Instanz vorhanden, also GUI oeffnen
            processLink = true;
         }
         catch(Exception e)
         {
            System.exit(1);
         }
      }

      if(linkListener == null)
      {
         try
         {
            linkListener = new LinkListener();
         }
         catch(IOException ex)
         {
            //bereits ein GUI vorhanden, also GUI schliessen
            doubleInstance = true;
         }
      }

      if(doubleInstance)
      {
         //bereits ein GUI vorhanden, also GUI schliessen
         JOptionPane.showMessageDialog(new Frame(), "Eine Instanz des GUIs ist bereits in Verwendung.", "appleJuice Client",
                                       JOptionPane.ERROR_MESSAGE);
         System.exit(1);
      }

      if(processLink)
      {
         linkListener.processLink(link, "");
      }

      boolean isDebug = System.getProperty("Debug") != null;

      if(isDebug)
      {
         ConsoleAppender consoleAppender = new ConsoleAppender(new PatternLayout("%d [%t] %-5p %c - %m%n"));

         Logger.getRootLogger().addAppender(consoleAppender);
         Logger.getRootLogger().setLevel(Level.DEBUG);
      }
      else
      {
         Logger rootLogger = Logger.getRootLogger();

         String datum     = new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date(System.currentTimeMillis()));
         String dateiName;

         dateiName = datum + ".html";
         layout    = new HTMLLayout();
         layout.setTitle("appleJuice-GUI-Log " + datum);
         layout.setLocationInfo(true);
         Level logLevel = OptionsManagerImpl.getInstance().getLogLevel();

         try
         {
            String path  = getRootDirectory() + File.separator + "logs";
            File   aFile = new File(path);

            if(!aFile.exists())
            {
               aFile.mkdir();
            }

            fileAppenderPath = path + File.separator + dateiName;
            if(logLevel != Level.OFF)
            {
               FileAppender fileAppender = new FileAppender(layout, fileAppenderPath);

               rootLogger.removeAllAppenders();
               rootLogger.addAppender(fileAppender);
            }
         }
         catch(IOException ioe)
         {
            ioe.printStackTrace();
         }

         rootLogger.setLevel(logLevel);
      }

      try
      {
         String       nachricht = "appleJuice-GUI " + AppleJuiceDialog.GUI_VERSION + "/" + ApplejuiceFassade.FASSADE_VERSION + " wird gestartet...";
         ConnectFrame connectFrame = new ConnectFrame();

         splash = new Splash(connectFrame, ((ImageIcon) IconManager.getInstance().getIcon("splashscreen")).getImage(), 0, 100);
         KeyStates ks = new KeyStates();

         splash.addKeyListener(ks);
         splash.setVisible(true);
         try
         {
            if(OptionsManagerImpl.getInstance().isThemesSupported())
            {
               java.lang.reflect.Method method = JFrame.class.getMethod("setDefaultLookAndFeelDecorated",
                                                                        new Class[] {boolean.class});

               method.invoke(null, new Object[] {Boolean.TRUE});

               method = JDialog.class.getMethod("setDefaultLookAndFeelDecorated", new Class[] {boolean.class});
               method.invoke(null, new Object[] {Boolean.TRUE});
            }
         }
         catch(Exception e)
         {
            if(logger.isEnabledFor(Level.FATAL))
            {
               logger.fatal("Programmabbruch", e);
            }
         }

         if(logger.isEnabledFor(Level.INFO))
         {
            logger.info(nachricht);
         }

         System.out.println(nachricht);
         if(logger.isEnabledFor(Level.INFO))
         {
            nachricht = "erkanntes GUI-OS: " + System.getProperty("os.name");
            logger.info(nachricht);
            nachricht = "erkannte Java-Version: " + System.getProperty("java.version");
            logger.info(nachricht);
         }

         String                        titel            = null;
         LanguageSelector              languageSelector = LanguageSelector.getInstance();
         QuickConnectionSettingsDialog remoteDialog     = null;

         splash.setProgress(5, "Lade Themes...");
         AppleJuiceDialog.initThemes();
         splash.setProgress(10, "Teste Verbindung...");
         boolean showDialog = OptionsManagerImpl.getInstance().shouldShowConnectionDialogOnStartup();
         boolean keyDown = ks.isKeyDown(KeyEvent.VK_SHIFT);

         if(!showDialog)
         {
            showDialog = keyDown;
         }

         boolean firstTry       = keyDown ? false : true;
         int     erreichbarkeit = 2;

         if(ajFassade == null)
         {
            getAjFassade();
         }
         while(showDialog || (erreichbarkeit = ajFassade.isCoreAvailable()) != 0)
         {
            splash.setVisible(false);
            if(!showDialog)
            {
               if(erreichbarkeit == 2)
               {
                  titel     = languageSelector.getFirstAttrbuteByTagName("mainform.caption");
                  nachricht = languageSelector.getFirstAttrbuteByTagName("javagui.startup.fehlversuch");
                  SoundPlayer.getInstance().playSound(SoundPlayer.VERWEIGERT);
                  JOptionPane.showMessageDialog(connectFrame, nachricht, titel, JOptionPane.ERROR_MESSAGE);
               }
               else
               {
                  titel     = languageSelector.getFirstAttrbuteByTagName("mainform.caption");
                  nachricht = languageSelector.getFirstAttrbuteByTagName("mainform.msgdlgtext3");
                  SoundPlayer.getInstance().playSound(SoundPlayer.VERWEIGERT);
                  JOptionPane.showMessageDialog(connectFrame, nachricht, titel, JOptionPane.ERROR_MESSAGE);
               }
            }

            showDialog   = false;
            remoteDialog = new QuickConnectionSettingsDialog(connectFrame);
            if(firstTry && OptionsManagerImpl.getInstance().isErsterStart())
            {
               firstTry = false;
               remoteDialog.setNieWiederAnzeigen();
               remoteDialog.pressOK();
            }
            else
            {
               remoteDialog.setVisible(true);
               if(remoteDialog.getResult() == QuickConnectionSettingsDialog.ABGEBROCHEN)
               {
                  nachricht = languageSelector.getFirstAttrbuteByTagName("javagui.startup.verbindungsfehler");
                  nachricht = nachricht.replaceFirst("%s", OptionsManagerImpl.getInstance().getRemoteSettings().getHost());
                  JOptionPane.showMessageDialog(connectFrame, nachricht, titel, JOptionPane.OK_OPTION);
                  logger.fatal(nachricht);
                  System.out.println("Fehler: " + nachricht);
                  System.exit(-1);
               }
            }

            splash.setVisible(true);
         }

         SoundPlayer.getInstance().playSound(SoundPlayer.ZUGANG_GEWAEHRT);

         splash.setProgress(20, "Lade Hauptdialog...");
         SwingUtilities.invokeLater(new Runnable()
            {
               public void run()
               {
                  final AppleJuiceDialog theApp = new AppleJuiceDialog();

                  splash.setProgress(100, "GUI geladen...");
                  PositionManager lm = PositionManagerImpl.getInstance();

                  if(lm.isLegal())
                  {
                     theApp.setLocation(lm.getMainXY());
                     theApp.setSize(lm.getMainDimension());
                  }
                  else
                  {
                     Toolkit   tk            = Toolkit.getDefaultToolkit();
                     Dimension screenSize    = tk.getScreenSize();
                     Dimension appScreenSize = new Dimension(screenSize.width, screenSize.height);
                     Insets    insets        = tk.getScreenInsets(theApp.getGraphicsConfiguration());

                     appScreenSize.width -= (insets.left + insets.right);
                     appScreenSize.width = appScreenSize.width / 5 * 4;
                     appScreenSize.height -= (insets.top + insets.bottom);
                     appScreenSize.height = appScreenSize.height / 5 * 4;
                     Point location = new Point((screenSize.width - appScreenSize.width) / 2,
                                                (screenSize.height - appScreenSize.height) / 2);

                     lm.setMainXY(location);
                     lm.setMainDimension(appScreenSize);
                     theApp.setSize(appScreenSize);
                     theApp.setLocation(location);
                  }

                  theApp.setVisible(true);
                  String nachricht = "appleJuice-GUI gestartet...";

                  if(logger.isEnabledFor(Level.INFO))
                  {
                     logger.info(nachricht);
                  }

                  System.out.println(nachricht);
                  splash.dispose();
                  if(OptionsManagerImpl.getInstance().isErsterStart())
                  {
                     showConnectionWizard(theApp);
                  }

                  Thread versionWorker = new Thread()
                  {
                     public void run()
                     {
                        if(logger.isEnabledFor(Level.DEBUG))
                        {
                           logger.debug("VersionWorkerThread gestartet. " + this);
                        }

                        try
                        {
                           ProxySettings proxy        = ProxyManagerImpl.getInstance().getProxySettings();
                           String downloadData = WebsiteContentLoader.getWebsiteContent(proxy, "https://api.github.com", 443, "/repos/appleJuiceNET/gui-java/releases/latest");

                           if(downloadData.length() > 0)
                           {
                              JsonObject jsonObject             = new JsonParser().parse(downloadData).getAsJsonObject();
                              String aktuellsteVersion          = jsonObject.get("tag_name").getAsString();
                              StringTokenizer token1            = new StringTokenizer(aktuellsteVersion, ".");
                              String          guiVersion        = AppleJuiceDialog.GUI_VERSION;

                              StringTokenizer token2 = new StringTokenizer(guiVersion, ".");

                              if(token1.countTokens() != 3 || token2.countTokens() != 3)
                              {
                                 return;
                              }

                              String[] versionInternet = new String[3];
                              String[] aktuelleVersion = new String[3];

                              for(int i = 0; i < 3; i++)
                              {
                                 versionInternet[i] = token1.nextToken();
                                 aktuelleVersion[i] = token2.nextToken();
                              }

                              int     versionsInfoModus = OptionsManagerImpl.getInstance().getVersionsinfoModus();
                              boolean showInfo        = false;
                              boolean versionUpdate   = false;
                              boolean importantUpdate = false;
                              boolean cosmeticUpdate  = false;

                              if(Integer.parseInt(versionInternet[0]) > Integer.parseInt(aktuelleVersion[0]))
                              {
                                 versionUpdate = true;
                              }
                              else if(Integer.parseInt(versionInternet[1]) > Integer.parseInt(aktuelleVersion[1]))
                              {
                                 importantUpdate = true;
                              }
                              else if(Integer.parseInt(versionInternet[2]) > Integer.parseInt(aktuelleVersion[2]))
                              {
                                 cosmeticUpdate = true;
                              }

                              if(versionsInfoModus == 2 && (cosmeticUpdate || importantUpdate || versionUpdate))
                              {
                                 showInfo = true;
                              }
                              else if(versionsInfoModus == 1 && (importantUpdate || versionUpdate))
                              {
                                 showInfo = true;
                              }
                              else if(versionsInfoModus == 0 && versionUpdate)
                              {
                                 showInfo = true;
                              }

                              showInfo = true;

                              if(showInfo)
                              {
                                 String downloadLink = "";
                                 JsonArray arr = jsonObject.getAsJsonArray("assets");
                                 for (int i = 0; i < arr.size(); i++) {
                                    String name = arr.get(i).getAsJsonObject().get("name").getAsString();
                                    if(name.equals("AJCoreGUI.zip")) {
                                       downloadLink = arr.get(i).getAsJsonObject().get("browser_download_url").getAsString();
                                    }
                                 }

                                 String finaldownloadLink = downloadLink;
                                 SwingUtilities.invokeLater(() -> {
                                    UpdateInformationDialog updateInformationDialog = new UpdateInformationDialog(theApp, aktuellsteVersion, finaldownloadLink, finaldownloadLink);
                                    updateInformationDialog.setVisible(true);
                                 });
                              }
                           }
                        }
                        catch(Exception e)
                        {
                           if(logger.isEnabledFor(Level.INFO))
                           {
                              logger.info("Aktualisierungsinformationen konnten nicht geladen werden. Server down?");
                           }
                        }

                        if(logger.isEnabledFor(Level.DEBUG))
                        {
                           logger.debug("VersionWorkerThread beendet. " + this);
                        }
                     }
                  };

                  versionWorker.start();
               }
            });

         /**
          * erstmal raus mit dem Mobile-Client
          */

         //         MobileProxy.getInstance();
      }
      catch(Exception e)
      {
         if(logger.isEnabledFor(Level.FATAL))
         {
            logger.fatal("Programmabbruch", e);
         }

         System.exit(-1);
      }
   }

   private static String getRootDirectory()
   {
      if(rootDirectory == null)
      {
         if(System.getProperty("os.name").toLowerCase().indexOf("windows") == -1)
         {
            rootDirectory = System.getProperty("user.home") + File.separator + "appleJuice" + File.separator + "gui";
         }
         else
         {
            rootDirectory = System.getProperty("user.dir");
         }
      }

      return rootDirectory;
   }

   public static void showConnectionWizard(JFrame frame)
                                    throws HeadlessException
   {
      WizardDialog wizardDialog = new WizardDialog(frame, true);
      Dimension    appDimension = wizardDialog.getSize();
      Dimension    screenSize   = Toolkit.getDefaultToolkit().getScreenSize();

      wizardDialog.setLocation((screenSize.width - appDimension.width) / 2, (screenSize.height - appDimension.height) / 2);
      wizardDialog.setVisible(true);
   }

   public static boolean showConnectionWizard(JDialog dialog, AJSettings ajSettings)
                                       throws HeadlessException
   {
      WizardDialog wizardDialog = new WizardDialog(dialog, true, ajSettings);
      Dimension    appDimension = wizardDialog.getSize();
      Dimension    screenSize   = Toolkit.getDefaultToolkit().getScreenSize();

      wizardDialog.setLocation((screenSize.width - appDimension.width) / 2, (screenSize.height - appDimension.height) / 2);
      wizardDialog.setVisible(true);
      return wizardDialog.isRegularClosed();
   }

   public static String getPropertiesPath()
   {
      if(System.getProperty("os.name").toLowerCase().indexOf("windows") == -1)
      {
         String dir       = System.getProperty("user.home") + File.separator + "appleJuice";
         File   directory = new File(dir);

         if(!directory.isDirectory())
         {
            directory.mkdir();
         }

         dir += File.separator + "gui";
         directory = new File(dir);
         if(!directory.isDirectory())
         {
            directory.mkdir();
         }

         dir += File.separator + "ajgui.properties";
         return dir;
      }
      else
      {
         return System.getProperty("user.dir") + File.separator + "ajgui.properties";
      }
   }
}
