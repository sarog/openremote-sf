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
import org.openremote.beehive.TemplateTestBase;
import org.openremote.beehive.api.dto.TemplateDTO;
import org.openremote.beehive.api.service.TemplateService;
import org.openremote.beehive.api.service.impl.GenericDAO;
import org.openremote.beehive.domain.Account;
import org.openremote.beehive.domain.Template;

public class TemplateServiceTest extends TemplateTestBase {

   private TemplateService service = (TemplateService) SpringTestContext.getInstance().getBean("templateService");
   
   private GenericDAO genericDAO = (GenericDAO) SpringTestContext.getInstance().getBean("genericDAO");
   
   public void testGetTemplatesByAccountOid() {
      List<TemplateDTO> templates = service.loadAllTemplatesByAccountOid(1L);
      Assert.assertEquals(2, templates.size());
      Assert.assertEquals("t1", templates.get(0).getName());
      Assert.assertEquals("t2", templates.get(1).getName());
   }
   
   public void testGetAllPublicTemplate() {
      Template t = new Template();
      t.setName("public");
      t.setContent("public");
      t.setAccount(null);
      service.save(t);
      
      List<TemplateDTO> templates = service.loadAllPublicTemplate();
      Assert.assertEquals(1, templates.size());
      Assert.assertEquals("public", templates.get(0).getName());
      Assert.assertEquals("public", templates.get(0).getContent());
   }
   public void testSave() {
      Account a = genericDAO.getByMaxId(Account.class);
      Template t3 = new Template();
      t3.setAccount(a);
      t3.setName("t3");
      t3.setContent("content");
      a.addTemplate(t3);

      long templateOid = service.save(t3);
      Template t = genericDAO.loadById(Template.class, templateOid);
      assertEquals("t3", t.getName());
      assertEquals("content", t.getContent());
      assertEquals(a.getOid(), t.getAccount().getOid());
   }
   
   public void testRemove() {
      assertTrue(service.delete(1L));
   }

}
