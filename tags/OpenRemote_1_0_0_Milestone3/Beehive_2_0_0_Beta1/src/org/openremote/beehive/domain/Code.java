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
package org.openremote.beehive.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.openremote.beehive.utils.StringUtil;

/**
 * Infrared Codes (arbitrary name-value pairs). Typically consists of string names mapped to infrared code hex values.
 * 
 * <pre>
 * For example:
 * 	play	0x20
 * 	plus	0xD0
 * 	ffwd	0xE0
 * 	rev	0x10
 * 	minus	0xB0
 * 	menu	0x40
 * </pre>
 * 
 * @author Dan 2009-2-6
 * 
 */
@Entity
@SuppressWarnings("serial")
@Table(name = "code")
public class Code extends BusinessEntity {

   private String name;

   private String value;

   private String comment;

   private RemoteSection remoteSection;

   public Code() {
      value = "";
      comment = "";
   }

   public void addValueLine(String line) {
      value += (line + StringUtil.lineSeparator());
   }

   @Column(nullable = false)
   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   @Column(nullable = false, columnDefinition = "text")
   public String getValue() {
      return value;
   }

   public void setValue(String value) {
      this.value = value;
   }

   @Column(columnDefinition = "text")
   public String getComment() {
      return comment;
   }

   public void setComment(String comment) {
      this.comment = comment;
   }

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(nullable = false, name = "remote_section_oid")
   public RemoteSection getRemoteSection() {
      return remoteSection;
   }

   public void setRemoteSection(RemoteSection remoteSection) {
      this.remoteSection = remoteSection;
   }

   public String textLine() {
      String text = comment;
      if (getRemoteSection().isRaw()) {
         text += ("          " + "name " + name + StringUtil.lineSeparator());
         text += (value + StringUtil.lineSeparator());
      } else {
         text += "          " + name + StringUtil.remainedTabSpace(name) + value + StringUtil.lineSeparator();
      }
      return text;
   }

}
