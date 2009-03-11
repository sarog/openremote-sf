package org.openremote.beehive.domain;

import java.io.Serializable;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;


/**
 * Business entity class for all JPA entities with the common property oid.
 * 
 * @author Dan 2009-2-6
 *
 */
@MappedSuperclass
public abstract class BusinessEntity implements Serializable {

   private static final long serialVersionUID = -3871334485197341321L;
   private long oid;

   @Id
   @GeneratedValue(strategy = GenerationType.AUTO)
   public long getOid() {
      return oid;
   }

   public void setOid(long oid) {
      this.oid = oid;
   }
}
