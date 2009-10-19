package org.openremote.beehive.api.dto;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;


/**
 * Business entity class for all DTO entities with the common property oid.
 *
 * @author allen 2009-2-17
 *
 */
public abstract class BusinessEntityDTO implements Serializable {

   private static final long serialVersionUID = -3871334485197341321L;
   private long oid;

   @XmlElement(name="id")
   public long getOid() {
      return oid;
   }

   public void setOid(long oid) {
      this.oid = oid;
   }
}