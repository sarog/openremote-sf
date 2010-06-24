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
package org.openremote.web.console.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.openremote.web.console.net.ORConnection;
import org.openremote.web.console.net.ORHttpMethod;
import org.openremote.web.console.service.PanelIdentityService;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * The Class PanelIdentityServiceImpl.
 */
public class PanelIdentityServiceImpl implements PanelIdentityService {

   private static Logger log = Logger.getLogger(PanelIdentityServiceImpl.class);
   
   public List<String> getPanels(String url, String username, String password) {
      List<String> panels = null;
      url = url + "/rest/panels";
      ORConnection orConnection = new ORConnection(url, ORHttpMethod.GET, username, password);
      InputStream data = orConnection.getResponseData();
      if (data != null) {
         panels = parsePanelsFromInputStream(orConnection.getResponseData());
      }
      return panels;
   }

   public List<String> parsePanelsFromInputStream(InputStream inputStream) {
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
      } catch (IOException e) {
         log.error("The data is from ORConnection is bad", e);
      } catch (ParserConfigurationException e) {
         log.error("Cant build new Document builder", e);
      } catch (SAXException e) {
         log.error("Parse data error", e);
      }
      return panels;
   }
}
