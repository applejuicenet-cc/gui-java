/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.start;

import java.awt.Component;
import java.awt.Cursor;
import java.net.URI;

import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import de.applejuicenet.client.fassade.ApplejuiceFassade;
import de.applejuicenet.client.shared.DesktopTools;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/start/HyperlinkAdapter.java,v 1.9 2009/02/12 10:27:34 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 */
public class HyperlinkAdapter implements HyperlinkListener
{
   private Logger    logger;
   private Component parent;

   public HyperlinkAdapter(Component parent)
   {
      logger      = Logger.getLogger(getClass());
      this.parent = parent;
   }

   public void hyperlinkUpdate(HyperlinkEvent e)
   {
      HyperlinkEvent.EventType type = e.getEventType();

      if(type == HyperlinkEvent.EventType.ENTERED)
      {
         parent.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

      }
      else if(type == HyperlinkEvent.EventType.EXITED)
      {
         parent.setCursor(Cursor.getDefaultCursor());
      }
      else if(e.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
      {
         if(e.getURL() != null)
         {
            String url = e.getURL().toString();

            if(url.length() != 0)
            {
               executeLink(url);
            }
         }
      }
   }

   private void executeLink(String link)
   {
      try
      {
         DesktopTools.browse(new URI(link));
      }
      catch(Exception e)
      {
         if(logger.isEnabledFor(Level.ERROR))
         {
            logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
         }
      }
   }
}
