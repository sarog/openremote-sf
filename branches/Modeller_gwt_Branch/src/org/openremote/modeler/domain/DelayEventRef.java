package org.openremote.modeler.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@SuppressWarnings("serial")
@Entity
@DiscriminatorValue("DELAY_EVENT_REF")
public class DelayEventRef extends BuildingModelerEventRef {
   
   private DelayEvent targetDelayEvent;

   @OneToOne
   @JoinColumn(name="target_delay_event_oid")
   public DelayEvent getTargetDelayEvent() {
      return targetDelayEvent;
   }

   public void setTargetDelayEvent(DelayEvent targetDelayEvent) {
      this.targetDelayEvent = targetDelayEvent;
   }

   
   
}
