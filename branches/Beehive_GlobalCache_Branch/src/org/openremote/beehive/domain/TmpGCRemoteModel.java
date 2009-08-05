/*
 * OpenRemote, the Home of the Digital Home. Copyright 2008-2009, OpenRemote Inc.
 * 
 * See the contributors.txt file in the distribution for a full listing of individual contributors.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package org.openremote.beehive.domain;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * The Class TmpGCRemoteModel.
 */
@Entity
@SuppressWarnings("serial")
@Table(name = "tmp_gc_remote_model")
public class TmpGCRemoteModel extends BusinessEntity {
   private String name;
   private List<TmpGCDeviceModel> tmpGCDeviceModels;
   private List<TmpGCIRCode> tmpGCIRCodes;
   private List<TmpGCIRFlag> tmpGCIRFlags;
   
   @Column(nullable = false)
   public String getName() {
      return name;
   }

   @ManyToMany(mappedBy = "tmpGCRemoteModels")
   public List<TmpGCDeviceModel> getTmpGCDeviceModels() {
      return tmpGCDeviceModels;
   }

   @OneToMany(mappedBy = "tmpGCRemoteModel", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
   public List<TmpGCIRCode> getTmpGCIRCodes() {
      return tmpGCIRCodes;
   }

   @OneToMany(mappedBy = "tmpGCRemoteModel", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
   public List<TmpGCIRFlag> getTmpGCIRFlags() {
      return tmpGCIRFlags;
   }

   public void setName(String name) {
      this.name = name;
   }

   public void setTmpGCDeviceModels(List<TmpGCDeviceModel> tmpGCDeviceModels) {
      this.tmpGCDeviceModels = tmpGCDeviceModels;
   }

   public void setTmpGCIRCodes(List<TmpGCIRCode> tmpGCIRCodes) {
      this.tmpGCIRCodes = tmpGCIRCodes;
   }

   public void setTmpGCIRFlags(List<TmpGCIRFlag> tmpGCIRFlags) {
      this.tmpGCIRFlags = tmpGCIRFlags;
   }

}
