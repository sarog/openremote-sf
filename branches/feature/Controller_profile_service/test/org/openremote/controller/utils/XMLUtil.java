package org.openremote.controller.utils;

import java.io.IOException;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;
import org.openremote.controller.Constants;
import org.openremote.controller.exception.NoSuchComponentException;

public class XMLUtil {
   public static Document getDocument(String xmlPath){
      SAXBuilder builder = new SAXBuilder();
      Document doc = null;
      try {
         doc = builder.build(xmlPath);
      } catch (JDOMException e) {
         e.printStackTrace();
      } catch (IOException e) {
         e.printStackTrace();
      }
      
      return doc;
   }
   
   @SuppressWarnings("unchecked")
   public static Element getElementByID(Document doc,String id){
      String xpath = "//" + Constants.OPENREMOTE_NAMESPACE + ":*[@id='" + id + "']";
      
      try {
         XPath xPath = XPath.newInstance(xpath);
         xPath.addNamespace(Constants.OPENREMOTE_NAMESPACE, Constants.OPENREMOTE_WEBSITE);
         List<Element> elements = xPath.selectNodes(doc);
         if (elements.size() > 1) {
            throw new RuntimeException("duplicated id :" + id);
         } else if (elements.size() == 0) {
            throw new NoSuchComponentException();
         }
         return elements.get(0);
      } catch (JDOMException e) {
         throw new RuntimeException(e);
      }
   }
   
   
}
