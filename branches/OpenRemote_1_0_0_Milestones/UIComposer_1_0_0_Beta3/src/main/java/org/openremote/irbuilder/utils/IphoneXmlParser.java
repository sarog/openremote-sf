/**
 * 
 */
package org.openremote.irbuilder.utils;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;
import org.openremote.irbuilder.exception.XmlParserException;
import org.xml.sax.InputSource;

/**
 * @author Tomsky
 *
 */
public class IphoneXmlParser {
   private static final Logger logger = Logger.getLogger(IphoneXmlParser.class);
   
   private IphoneXmlParser(){      
   }
   
   /**
    * Modify xmlString and download icons from beehive
    * 
    * @param xmlString
    * @param folder
    * @return modified iphoneXML
    */
   public static String parserXML(String xmlString, File folder){
      SAXBuilder sb = new SAXBuilder(false);
      sb.setValidation(false);
      String iphoneXml = "";
      try {         
          Document doc = sb.build(new InputSource(new StringReader(xmlString)));
          XPath xpath = XPath.newInstance("//button[@icon]");
          List<Element> elements = xpath.selectNodes(doc);
          for (Element element : elements) {
             String iconVal = element.getAttributeValue("icon");
             String iconName = iconVal.substring(iconVal.lastIndexOf("/")+1);;
             element.setAttribute("icon", iconName);
             File iphoneIconFile = new File(folder,iconName);
             if(iconVal.startsWith("http")){
                downloadFile(iconVal,iphoneIconFile);
             }
         }          
         Format format = Format.getPrettyFormat();
         format.setIndent("  ");
         format.setEncoding("UTF-8");
         XMLOutputter outp = new XMLOutputter(format);
         iphoneXml = outp.outputString(doc);
      } catch (JDOMException e) {
          logger.error("Parser XML occur JDOMException", e);
          throw new XmlParserException("Parser XML occur JDOMException",e);
      } catch (IOException e) {
         logger.error("Parser XML occur IOException", e);
         throw new XmlParserException("Parser XML occur IOException",e);
      }
      return iphoneXml;
   }
   
   private static void downloadFile(String srcUrl, File destFile) throws HttpException, IOException{
      HttpClient client = new HttpClient();  
      GetMethod get = new GetMethod(srcUrl);  
       client.executeMethod(get);
       FileOutputStream output = new FileOutputStream(destFile);  

       output.write(get.getResponseBody());  
       output.close();  
   }
}
