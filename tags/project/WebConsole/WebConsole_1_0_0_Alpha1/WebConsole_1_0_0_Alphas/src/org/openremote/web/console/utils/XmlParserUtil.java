/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2010, OpenRemote Inc.
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
package org.openremote.web.console.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.openremote.web.console.domain.Group;
import org.openremote.web.console.domain.Image;
import org.openremote.web.console.domain.Label;
import org.openremote.web.console.domain.PanelXmlEntity;
import org.openremote.web.console.domain.Screen;
import org.openremote.web.console.domain.TabBar;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Contains parse xml inputStream methods.
 */
public class XmlParserUtil {

   private static Logger log = Logger.getLogger(XmlParserUtil.class);
   
   private XmlParserUtil() {
   }
   
   /**
    * Parses the panel names from input stream.
    * the input stream is get from "{controller}/rest/panels".
    * 
    * @param inputStream the input stream
    * 
    * @return the list< string>
    */
   public static List<String> parsePanelNamesFromInputStream(InputStream inputStream) {
      List<String> panels = new ArrayList<String>();
      try {
         DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
         DocumentBuilder builder = factory.newDocumentBuilder();
         Document dom = builder.parse(inputStream);
         Element root = dom.getDocumentElement();

         NodeList nodeList = root.getElementsByTagName("panel");
         int nodeNums = nodeList.getLength();
         for (int i = 0; i < nodeNums; i++) {
            panels.add(nodeList.item(i).getAttributes().getNamedItem("name").getNodeValue());
         }
      } catch (ParserConfigurationException e) {
         log.error("Cant build new Document builder when parse panel list", e);
      } catch (SAXException e) {
         log.error("Parse panel list data error", e);
      } catch (IOException e) {
         log.error("The data from ORConnection is bad when parse panel list", e);
      } finally {
         try {
            inputStream.close();
         } catch (IOException e) {
            log.error("close inputstream error when parse panel list", e);
         }
      }
      return panels;
   }
   
   /**
    * Parses the panel xml from input stream.
    * the input stream is get from "{controller}/rest/panel/{panelName}".
    * 
    * @param inputStream the input stream
    * 
    * @return the panel xml entity
    */
   public static PanelXmlEntity parsePanelXmlFromInputStream(InputStream inputStream) {
      PanelXmlEntity panelXmlEntity = new PanelXmlEntity();
      try {
         DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
         DocumentBuilder builder = factory.newDocumentBuilder();
         Document dom = builder.parse(inputStream);
         Element root = dom.getDocumentElement();

         NodeList nodes = root.getChildNodes();
         int nodeLength = nodes.getLength();
         for (int i = 0; i < nodeLength; i++) {
            if (nodes.item(i).getNodeType() == Node.ELEMENT_NODE && "tabbar".equals(nodes.item(i).getNodeName())) {
               panelXmlEntity.setGlobalTabBar(new TabBar(nodes.item(i)));
            }
         }
         
         Map<Integer, Screen> screens = panelXmlEntity.getScreens();
         NodeList screenNodes = root.getElementsByTagName("screen");
         int screenNum = screenNodes.getLength();
         for (int i = 0; i < screenNum; i++) {
            Screen screen = new Screen(screenNodes.item(i), panelXmlEntity);
            screens.put(screen.getScreenId(), screen);
         }

         Map<Integer, Group> groups = panelXmlEntity.getGroups();
         NodeList groupNodes = root.getElementsByTagName("group");
         int groupNum = groupNodes.getLength();
         for (int i = 0; i < groupNum; i++) {
            Group group = new Group(groupNodes.item(i), screens);
            groups.put(group.getGroupId(), group);
         }
         
         // set linked label for images.
         Map<Integer, Label> tmpLabels = panelXmlEntity.getTmpLabels();
         for (Image tmpImage : panelXmlEntity.getTmpImages()) {
            if (tmpImage.getLabelRefId() > 0) {
               tmpImage.setLabel(tmpLabels.get(tmpImage.getLabelRefId()));
            }
         }
         
      } catch (ParserConfigurationException e) {
         log.error("Cant build new Document builder when parse panel content", e);
      } catch (SAXException e) {
         log.error("Parse panel content error", e);
      } catch (IOException e) {
         log.error("The data from ORConnection is bad when parse panel content", e);
      } finally {
         try {
            inputStream.close();
         } catch (IOException e) {
            log.error("close inputstream error when parse panel content", e);
         }
      }
      return panelXmlEntity;
   }
}
