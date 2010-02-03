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
package org.openremote.beehive.service;

import java.util.List;

import junit.framework.Assert;

import org.openremote.beehive.SpringTestContext;
import org.openremote.beehive.TestBase;
import org.openremote.beehive.api.dto.TemplateDTO;
import org.openremote.beehive.api.service.AccountService;
import org.openremote.beehive.api.service.TemplateService;
import org.openremote.beehive.domain.Account;
import org.openremote.beehive.domain.Template;

public class TemplateServiceTest extends TestBase {

   private TemplateService service = (TemplateService) SpringTestContext.getInstance().getBean("templateService");
   private AccountService accountService = (AccountService) SpringTestContext.getInstance().getBean("accountService");

   public void testTemplates() {
      Account a = new Account();
      Template t1 = new Template();
      t1.setAccount(a);
      t1.setName("t1");
      t1.setContent("content");
      a.addTemplate(t1);
      Template t2 = new Template();
      t2.setAccount(a);
      t2.setName("t2");
      t2.setContent("content");
      a.addTemplate(t2);
      accountService.save(a);

      List<TemplateDTO> templates = service.loadAllTemplatesByAccountOid(1L);
      Assert.assertEquals(templates.size(), 2);
      Assert.assertEquals(templates.get(0).getName(), "t1");
      Assert.assertEquals(templates.get(1).getName(), "t2");
   }

}
