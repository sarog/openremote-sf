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
package org.openremote.web.console.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.openremote.web.console.SpringTestContext;

/**
 * The Class PanelIdentityServiceTest.
 */
public class PanelIdentityServiceTest {

   private PanelIdentityService panelIdentityService =
      (PanelIdentityService) SpringTestContext.getInstance().getBean("panelIdentityService");
   
   @Test
   public void testGetPanels() {
      String path = getClass().getResource("fixture").getPath() + "/panels.xml";
      File file = new File(path);
      if (!file.exists() || file.isDirectory()) {
         return;
      }
      FileInputStream fis = null;
      try {
         fis = new FileInputStream(path);
      } catch (FileNotFoundException e) {
         e.printStackTrace();
      }
      List<String> panels = panelIdentityService.parsePanelsFromInputStream(fis);
      Assert.assertEquals(6, panels.size());
   }
}
