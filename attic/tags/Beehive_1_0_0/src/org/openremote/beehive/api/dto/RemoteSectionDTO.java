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
package org.openremote.beehive.api.dto;

/**
 * A configuration section in a LIRC configuration file linked with a {@link Model}. It is possible to have more than
 * one remote configuration in a configuration file. Each remote configuration in a file should go to the database as a
 * separate remote record. User can later combine multiple sections into a single configuration file which can support
 * multiple remote devices.
 * 
 * @author Dan 2009-2-6
 * @author allen.wei 2009-2-18
 */
@SuppressWarnings("serial")
public class RemoteSectionDTO extends BusinessEntityDTO {

   private boolean raw;

   private String comment;

   private String name = "UNKNOWN";

   public RemoteSectionDTO() {
      comment = "";
   }

   public boolean isRaw() {
      return raw;
   }

   public void setRaw(boolean raw) {
      this.raw = raw;
   }

   public String getComment() {
      return comment;
   }

   public void setComment(String comment) {
      this.comment = comment;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

}