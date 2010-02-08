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
package org.openremote.beehive.api.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.openremote.beehive.api.dto.TemplateDTO;
import org.openremote.beehive.api.service.TemplateService;
import org.openremote.beehive.domain.Account;
import org.openremote.beehive.domain.Template;


public class TemplateServiceImpl extends BaseAbstractService<Template> implements TemplateService {

   @Override
   public List<TemplateDTO> loadAllTemplatesByAccountOid(long accountOid) {
      List<TemplateDTO> templateDTOs = new ArrayList<TemplateDTO>();
      Account account = genericDAO.loadById(Account.class, accountOid);
      for (Template template : account.getTemplates()) {
         TemplateDTO t = new TemplateDTO();
         t.setName(template.getName());
         t.setOid(template.getOid());
         t.setContent(template.getContent());
         templateDTOs.add(t);
      }
      return templateDTOs;
   }
   
   @Override
   public TemplateDTO loadTemplateByOid(long templateOid) {
      Template template = genericDAO.loadById(Template.class, templateOid);
      TemplateDTO t = new TemplateDTO();
      t.setName(template.getName());
      t.setOid(template.getOid());
      t.setContent(template.getContent());
      return t;
   }

   @Override
   public long save(Template t) {
      return (Long) genericDAO.save(t);
   }

   @Override
   public boolean delete(long templateOid) {
      Template t = genericDAO.loadById(Template.class, templateOid);
      if (t != null) {
         genericDAO.delete(t);
         return true;
      }
      return false;
   }

}
