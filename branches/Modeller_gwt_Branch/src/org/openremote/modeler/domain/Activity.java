package org.openremote.modeler.domain;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
@SuppressWarnings("serial")
@Entity
public class Activity extends UIBusinessEntity {
   
   private List<Screen> screens;
   
   @OneToMany(mappedBy="activity")
   public List<Screen> getScreens() {
      return screens;
   }

   public void setScreens(List<Screen> screens) {
      this.screens = screens;
   }
   
}
