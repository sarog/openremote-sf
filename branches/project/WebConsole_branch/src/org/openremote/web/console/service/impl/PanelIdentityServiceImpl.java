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

import java.io.InputStream;
import java.util.List;

import org.openremote.web.console.domain.PanelXmlEntity;
import org.openremote.web.console.net.ORConnection;
import org.openremote.web.console.net.ORHttpMethod;
import org.openremote.web.console.service.PanelIdentityService;
import org.openremote.web.console.utils.XmlParserUtil;

/**
 * The implementation of <code>PanelIdentityService</code>.
 */
public class PanelIdentityServiceImpl implements PanelIdentityService {

   /**
    * Requests panels from controller as xml, and parse the xml to panel names.
    */
   public List<String> getPanelNames(String url, String username, String password) {
      List<String> panels = null;
      url = url + "/rest/panels";
      ORConnection orConnection = new ORConnection(url, ORHttpMethod.GET, username, password);
      InputStream data = orConnection.getResponseData();
      if (data != null) {
         panels = XmlParserUtil.parsePanelNamesFromInputStream(orConnection.getResponseData());
      }
      return panels;
   }

   /**
    * Requests panel.xml from controller, and parse the xml to panel xml entity.
    */
   public PanelXmlEntity getPanelXmlEntity(String url, String username, String password) {
      PanelXmlEntity panelXmlEntity = null;
      ORConnection orConnection = new ORConnection(url, ORHttpMethod.GET, username, password);
      InputStream data = orConnection.getResponseData();
      if (data != null) {
         panelXmlEntity = XmlParserUtil.parsePanelXmlFromInputStream(orConnection.getResponseData());
      }
      return panelXmlEntity;
   }

}
