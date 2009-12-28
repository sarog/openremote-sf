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
   @JoinColumn(name = "target_switch_on_oid")
   public Switch getOnSwitch() {
      return onSwitch;
   }

   public void setOnSwitch(Switch onSwitch) {
      this.onSwitch = onSwitch;
   }
   
   
}
