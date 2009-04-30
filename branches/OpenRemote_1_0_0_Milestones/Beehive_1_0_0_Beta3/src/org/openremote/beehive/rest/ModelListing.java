/* OpenRemote, the Home of the Digital Home.
 * Copyright 2008, OpenRemote Inc.
 * 
 * See the contributors.txt file in the distribution for a
 * full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3.0 of
 * the License, or (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.openremote.beehive.rest;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.openremote.beehive.api.dto.ModelDTO;

/**
 * In order to let rest service to serialize list of model User: allenwei Date: 2009-2-10 Time: 13:57:29
 */
@XmlRootElement(name = "models")
public class ModelListing {

   private List<ModelDTO> models = new ArrayList<ModelDTO>();

   public ModelListing() {
   }

   public ModelListing(List<ModelDTO> models) {
      this.models = models;
   }

   @XmlElement(name = "model")
   public List<ModelDTO> getModels() {
      return models;
   }

   public void setModels(List<ModelDTO> models) {
      this.models = models;
   }
}
