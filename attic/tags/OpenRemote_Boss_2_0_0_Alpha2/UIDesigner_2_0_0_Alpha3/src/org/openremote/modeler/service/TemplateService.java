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
package org.openremote.modeler.service;

import org.openremote.modeler.domain.Screen;
import org.openremote.modeler.domain.Template;

/**
 * The service for template.
 * @author javen
 *
 */
public interface TemplateService {
   /**
    * Save a template to the beehive.
    * @param template The template you want to save. 
    * @return The template after being saved. 
    */
   Template saveTemplate(Template template);
   /*
   String getTemplateContent(Screen screen);*/
   /**
    * Build a screen from a template. 
    * @param template The template you want to use to build a screen. 
    * @return A screen builded from the template. 
    */
   Screen buildScreenFromTemplate(Template template);
   
   boolean deleteTemplate(long templateOid);
}
