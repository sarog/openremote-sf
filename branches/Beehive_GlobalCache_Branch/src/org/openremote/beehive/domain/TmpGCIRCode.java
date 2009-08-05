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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * The Class TmpGCIRCode.
 */
@Entity
@SuppressWarnings("serial")
@Table(name = "tmp_gc_ir_code")
public class TmpGCIRCode extends BusinessEntity {
   private String name;
   private String value;
   private int status;
   private TmpGCRemoteModel tmpGCRemoteModel;

   @Column(nullable = false)
   public String getName() {
      return name;
   }

   @Column(nullable = false)
   public String getValue() {
      return value;
   }

   public int getStatus() {
      return status;
   }

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "tmp_gc_remote_model_oid", nullable = false)
   public TmpGCRemoteModel getTmpGCRemoteModel() {
      return tmpGCRemoteModel;
   }

   public void setName(String name) {
      this.name = name;
   }

   public void setValue(String value) {
      this.value = value;
   }

   public void setStatus(int status) {
      this.status = status;
   }

   public void setTmpGCRemoteModel(TmpGCRemoteModel tmpGCRemoteModel) {
      this.tmpGCRemoteModel = tmpGCRemoteModel;
   }

}
