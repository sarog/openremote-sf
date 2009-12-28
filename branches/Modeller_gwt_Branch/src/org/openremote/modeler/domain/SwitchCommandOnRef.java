package org.openremote.modeler.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@SuppressWarnings("serial")
@Entity
@DiscriminatorValue("SWITCH_CMD_ON_REF")
public class SwitchCommandOnRef extends CommandRefItem {

   private Switch onSwitch;

   @OneToOne
   @JoinColumn(name = "on_switch_oid")
   public Switch getOnSwitch() {
      return onSwitch;
   }

   public void setOnSwitch(Switch onSwitch) {
      this.onSwitch = onSwitch;
   }
   
   
}
