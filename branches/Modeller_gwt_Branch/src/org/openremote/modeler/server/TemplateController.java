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
package org.openremote.modeler.server;

import org.openremote.modeler.client.rpc.TemplateRPCService;
import org.openremote.modeler.domain.Screen;
import org.openremote.modeler.domain.Template;
import org.openremote.modeler.service.TemplateService;

/**
 * The controller for Template. 
 * @author javen
 *
 */
@SuppressWarnings("serial")
public class TemplateController extends BaseGWTSpringController implements TemplateRPCService {

   private TemplateService templateService = null;
   
   @Override
   public Screen buildScreeFromTemplate(Template template) {
      return templateService.buildScreenFromTemplate(template);
   }

   /*@Override
   public List<Template> getTemplateList() {
      return null;
   }*/

   @Override
   public Template saveTemplate(Template template) {
      return templateService.saveTemplate(template);
   }
   @Override
   public Boolean deleteTemplate(long templateId){
      return templateService.deleteTemplate(templateId);
   }
   public void setTemplateService(TemplateService templateService) {
      this.templateService = templateService;
   }

}
