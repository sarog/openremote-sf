package org.openremote.modeller.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

@Entity
public class Screen extends BusinessEntity {

   /**
    * 
    */
   private static final long serialVersionUID = -6519071799898041938L;
   private String name;
   private Activity activity;
   
   public Screen(){} 
   
   public String getName() {
      return name;
   }
   public void setName(String name) {
      this.name = name;
   }
   
   @ManyToOne(fetch = FetchType.LAZY)
   public Activity getActivity() {
      return activity;
   }
   public void setActivity(Activity activity) {
      this.activity = activity;
   }
   
   
}
