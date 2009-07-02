package org.openremote.modeler.domain;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name="screen_jump_event")
public class ScreenJumpEvent extends BusinessEntity {
   
   private Screen targetScreen;

   @OneToOne
   @JoinColumn(nullable = false, name = "target_screen_oid")
   public Screen getTargetScreen() {
      return targetScreen;
   }

   public void setTargetScreen(Screen targetScreen) {
      this.targetScreen = targetScreen;
   }

   
}
