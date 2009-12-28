package org.openremote.modeler.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@SuppressWarnings("serial")
@Entity
@DiscriminatorValue("SWITCH_CMD_OFF_REF")
public class SwitchCommandOffRef extends CommandRefItem {

   private Switch offSwitch;

   @OneToOne
   @JoinColumn(name = "target_switch_off_oid")
   public Switch getOffSwitch() {
      return offSwitch;
   }

   public void setOffSwitch(Switch offSwitch) {
      this.offSwitch = offSwitch;
   }
   
   
}
