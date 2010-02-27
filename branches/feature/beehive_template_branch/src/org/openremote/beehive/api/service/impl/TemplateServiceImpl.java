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

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.openremote.beehive.Configuration;
import org.openremote.beehive.api.dto.TemplateDTO;
import org.openremote.beehive.api.service.TemplateService;
import org.openremote.beehive.domain.Account;
import org.openremote.beehive.domain.Template;
import org.openremote.beehive.utils.FileUtil;


public class TemplateServiceImpl extends BaseAbstractService<Template> implements TemplateService {
   private static final Log logger = LogFactory.getLog(TemplateService.class);

   protected Configuration configuration = null;

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
   public List<TemplateDTO> loadAllPublicTemplate() {
      List<TemplateDTO> templatesDTOs = new ArrayList<TemplateDTO>();

      DetachedCriteria criteria = DetachedCriteria.forClass(Template.class);
      criteria.add(Restrictions.isNull("account"));
      List<Template> templates = genericDAO.findByDetachedCriteria(criteria);
      for (Template template : templates) {
         TemplateDTO templateDTO = new TemplateDTO();
         templateDTO.setName(template.getName());
         templateDTO.setContent(template.getContent());
         templateDTO.setOid(template.getOid());
         templatesDTOs.add(templateDTO);
      }
      return templatesDTOs;
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
      long templateOid = (Long) genericDAO.save(t);
      if (t.getAccount() == null) {
         createTemplateFolder(Template.PUBLIC_ACCOUNT_OID);
      } else {
         createTemplateFolder(templateOid);
      }
      return templateOid;
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

   @Override
   public File getTemplateResourceZip(long templateOid) {
      File templateFolder = createTemplateFolder(templateOid);
      File[] files = templateFolder.listFiles(new FilenameFilter() {

         @Override
         public boolean accept(File dir, String name) {
            return name.equalsIgnoreCase("template.zip");
         }

      });
      if (files != null && files.length != 0) {
         return files[0];
      }
      return null;
   }

   private File createTemplateFolder(long templateOid) {
      String templateFolder = configuration.getTemplateResourcesDir() + File.separator + templateOid;

      File templateFolderFile = new File(templateFolder);
      templateFolderFile.mkdirs();
      return templateFolderFile;
   }

   public boolean saveTemplateResourceZip(long templateOid, InputStream input) {
      File templateFolder = createTemplateFolder(templateOid);
      File zipFile = new File(templateFolder, TEMPLATE_RESOURCE_ZIP_FILE_NAME);
      FileOutputStream fos = null;
      try {
         FileUtil.deleteFileOnExist(zipFile);
         fos = new FileOutputStream(zipFile);
         byte[] buffer = new byte[1024];
         int length = 0;
         while ((length = input.read(buffer)) != -1) {
            fos.write(buffer, 0, length);
         }
         logger.info("save resource success!");
         return true;
      } catch (Exception e) {
         logger.error("falied to save resource from modeler to beehive", e);
         throw new RuntimeException("falied to save resource from modeler to beehive",e);
      } finally {
         if (fos != null) {
            try {
               fos.close();
            } catch (Exception e) {
            }
         }
      }
   }

   public void setConfiguration(Configuration configuration) {
      this.configuration = configuration;
   }

}
