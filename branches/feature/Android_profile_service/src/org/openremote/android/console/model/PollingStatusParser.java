package org.openremote.android.console.model;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class PollingStatusParser {
   public static final HashMap<String, String> statusMap = new HashMap<String, String>();
   
   public static void parse(InputStream inputStream) {
      try {
         DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
         DocumentBuilder builder = factory.newDocumentBuilder();
         Document dom = builder.parse(inputStream);
         Element root = dom.getDocumentElement();
         
         NodeList nodeList = root.getElementsByTagName("status");
         int nodeNums = nodeList.getLength();
         for (int i = 0; i < nodeNums; i++) {
            String lastId = nodeList.item(i).getAttributes().getNamedItem("id").getNodeValue();
            statusMap.put(lastId, nodeList.item(i).getFirstChild().getNodeValue());
            ORListenerManager.getInstance().notifyOREventListener(ListenerConstant.ListenerPollingStatusIdFormat + lastId, null);
         }
      } catch (ParserConfigurationException e) {
         e.printStackTrace();
      } catch (SAXException e) {
         e.printStackTrace();
      } catch (IOException e) {
         e.printStackTrace();
      }
   }
}
