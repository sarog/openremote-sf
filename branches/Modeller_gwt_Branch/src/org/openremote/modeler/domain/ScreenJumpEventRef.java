package org.openremote.modeler.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@SuppressWarnings("serial")
@Entity
@DiscriminatorValue("SCREEN_JUMP_EVENT_REF")
public class ScreenJumpEventRef extends UIDesignerEventRef {
   
   private ScreenJumpEvent targetScreenJumpEvent;

   @OneToOne
   @JoinColumn(name="target_screen_jump_event_oid")
   public ScreenJumpEvent getTargetScreenJumpEvent() {
      return targetScreenJumpEvent;
   }

   public void setTargetScreenJumpEvent(ScreenJumpEvent targetScreenJumpEvent) {
      this.targetScreenJumpEvent = targetScreenJumpEvent;
   }
   
}
