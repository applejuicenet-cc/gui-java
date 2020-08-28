/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.fassade.controller.xml;

import java.io.CharArrayWriter;
import java.io.StringReader;

import org.apache.xerces.parsers.SAXParser;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import de.applejuicenet.client.fassade.ApplejuiceFassade;
import de.applejuicenet.client.fassade.controller.CoreConnectionSettingsHolder;
import de.applejuicenet.client.fassade.shared.HtmlLoader;
import de.applejuicenet.client.fassade.shared.StringConstants;

/**
 * $Header:
 * /cvsroot/applejuicejava/ajcorefassade/src/de/applejuicenet/client/fassade/controller/xmlholder/InformationXMLHolder.java,v
 * 1.1 2004/12/03 07:57:12 maj0r Exp $
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
 * @author: Maj0r <aj@tkl-soft.de>
 *
 */
public class InformationXMLHolder extends DefaultHandler
{
   private static InformationXMLHolder  instance    = null;
   private VersionDO                    version     = null;
   private XMLReader                    xr          = null;
   private String                       xmlCommand  = StringConstants.XML_INFORMATION_XML_PASSWORD;
   private CharArrayWriter              contents    = new CharArrayWriter();
   private String                       coreVersion;
   private CoreConnectionSettingsHolder coreHolder;

   @SuppressWarnings("unchecked")
   public InformationXMLHolder(CoreConnectionSettingsHolder coreHolder)
   {
      this.coreHolder = coreHolder;
      try
      {
         xmlCommand = StringConstants.XML_INFORMATION_XML_PASSWORD;
         Class parser = SAXParser.class;

         xr = XMLReaderFactory.createXMLReader(parser.getName());
         xr.setContentHandler(this);
      }
      catch(Exception ex)
      {
         throw new RuntimeException(ex);
      }
   }

   public VersionDO getCoreVersion()
   {
      if(version == null)
      {
         try
         {
            String xmlString = getXMLString();

            xr.parse(new InputSource(new StringReader(xmlString)));
            version = new VersionDO(coreVersion, VersionDO.getOSTypByOSName((String) System.getProperties().get("os.name")));
         }
         catch(Exception ex)
         {
            throw new RuntimeException(ex);
         }
      }

      return version;
   }

   public void startElement(String namespaceURI, String localName, String qName, Attributes attr)
                     throws SAXException
   {
      contents.reset();
      if(localName.equals(StringConstants.FILESYSTEM))
      {
         int length = attr.getLength();

         for(int i = 0; i < length; i++)
         {
            if(attr.getLocalName(i).equals(StringConstants.SEPERATOR))
            {
               ApplejuiceFassade.separator = attr.getValue(i);
               break;
            }
         }
      }
   }

   public void characters(char[] ch, int start, int length)
                   throws SAXException
   {
      contents.write(ch, start, length);
   }

   public void endElement(String namespaceURI, String localName, String qName)
                   throws SAXException
   {
      if(localName.equals(StringConstants.VERSION))
      {
         coreVersion = contents.toString();
      }
   }

   private String getXMLString() throws Exception
   {
      String xmlData = null;

      xmlData = HtmlLoader.getHtmlXMLContent(coreHolder.getCoreHost(), coreHolder.getCorePort(), HtmlLoader.GET,
                                             xmlCommand + coreHolder.getCorePassword());
      if(xmlData.length() == 0)
      {
         throw new IllegalArgumentException();
      }

      return xmlData;
   }
}
