/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2009, OpenRemote Inc.
*
* See the contributors.txt file in the distribution for a
* full listing of individual contributors.
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as
* published by the Free Software Foundation, either version 3 of the
* License, or (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Affero General Public License for more details.
*
* You should have received a copy of the GNU Affero General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*/

package org.openremote.modeler.utils;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;
import org.openremote.modeler.exception.XmlParserException;
import org.xml.sax.InputSource;

/**
 * The iphone.xml parser .
 * 
 * @author Tomsky, Handy
 */
public class IphoneXmlParser {
   
   /** The Constant LOGGER. */
   private static final Logger LOGGER = Logger.getLogger(IphoneXmlParser.class);
   
   /** The Constant SCHEMA_LANGUAGE. */
   public static final String SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
   
   /** The Constant XML_SCHEMA. */
   public static final String XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
   
   /** The Constant SCHEMA_SOURCE. */
   public static final String SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";
   
   /**
    * Instantiates a new iphone xml parser.
    */
   private IphoneXmlParser() {      
   }
   
   /**
    * Modify xmlString and download icons from beehive.
    * 
    * @param xmlString the xml string
    * @param folder the folder
    * @param xsdfile the xsdfile
    * 
    * @return modified iphoneXML
    */
   @SuppressWarnings("unchecked")
   public static String parserXML(File xsdfile, String xmlString, File folder) {
      SAXBuilder sb = new SAXBuilder(false);
      sb.setValidation(false);

//      sb.setProperty(SCHEMA_LANGUAGE, XML_SCHEMA);
//      sb.setProperty(SCHEMA_SOURCE, xsdfile);
      String iphoneXml = "";
      try {         
          Document doc = sb.build(new InputSource(new StringReader(xmlString)));
          xpathParse(folder, doc, "//or:image[@src]");          
         Format format = Format.getPrettyFormat();
         format.setIndent("  ");
         format.setEncoding("UTF-8");
         XMLOutputter outp = new XMLOutputter(format);
         iphoneXml = outp.outputString(doc);
      } catch (JDOMException e) {
          LOGGER.error("Parser XML occur JDOMException", e);
          throw new XmlParserException("Parser XML occur JDOMException", e);
      } catch (IOException e) {
         LOGGER.error("Parser XML occur IOException", e);
         throw new XmlParserException("Parser XML occur IOException", e);
      }
      return iphoneXml;
   }

   @SuppressWarnings("unchecked")
   private static void xpathParse(File folder, Document doc, String xpathExpression) throws JDOMException, IOException {
      XPath xpath = XPath.newInstance(xpathExpression);
       xpath.addNamespace("or", "http://www.openremote.org");
       List<Element> elements = xpath.selectNodes(doc);
       for (Element element : elements) {
          String iconVal = element.getAttributeValue("src");
          String iconName = iconVal.substring(iconVal.lastIndexOf("/") + 1);
          element.setAttribute("src", iconName);
          File iphoneIconFile = new File(folder, iconName);
          if (iconVal.startsWith("http")) {
             downloadFile(iconVal, iphoneIconFile);
          }
      }
   }
   
   /**
    * Download file.
    * 
    * @param srcUrl the src url
    * @param destFile the dest file
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    */
   private static void downloadFile(String srcUrl, File destFile) throws IOException {
      HttpClient client = new HttpClient();
      GetMethod get = new GetMethod(srcUrl);
      client.executeMethod(get);
      FileOutputStream output = new FileOutputStream(destFile);

      output.write(get.getResponseBody());
      output.close();
   }
   
   /**
    * Check xml schema.
    * 
    * @param xsdPath the xsd path
    * @param xmlString the xml string
    * 
    * @return true, if successful
    */
   public static boolean checkXmlSchema(String xsdPath, String xmlString) {
      SAXBuilder sb = new SAXBuilder(true);
      sb.setValidation(true);
      
      File xsdfile = new File(xsdPath);

      sb.setProperty(SCHEMA_LANGUAGE, XML_SCHEMA);
      sb.setProperty(SCHEMA_SOURCE, xsdfile);
      try {
         sb.build(new InputSource(new StringReader(xmlString)));
      } catch (JDOMException e) {
         LOGGER.error("Check the schema " + xsdfile.getName() + " occur JDOMException", e);
         return false;
      } catch (IOException e) {
         LOGGER.error("Check the schema " + xsdfile.getName() + " occur IOException", e);
         return false;
      }
      return true;
   }
}
