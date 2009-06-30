package org.openremote.modeller.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
public class Activity extends BusinessEntity {

   /**
    * 
    */
   private static final long serialVersionUID = -2619445820543928781L;
   private String name;
   private List<Screen> screens = new ArrayList<Screen>();
   
   public Activity(){}

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   @OneToMany(mappedBy = "activity", cascade= CascadeType.ALL, fetch = FetchType.LAZY)
   public List<Screen> getScreens() {
      return screens;
   }

   public void setScreens(List<Screen> screens) {
      this.screens = screens;
   }
   
   
   
   
}
