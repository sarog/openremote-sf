package org.openremote.modeler.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
@SuppressWarnings("serial")
@Entity
@DiscriminatorValue("UI_MACRO_EVENT_REF")
public class UIMacroEventRef extends UIDesignerEventRef {
   
   private UIMacroEvent targetUIMacroEvent;

   @OneToOne
   @JoinColumn(name="target_ui_macro_event_oid")
   public UIMacroEvent getTargetUIMacroEvent() {
      return targetUIMacroEvent;
   }

   public void setTargetUIMacroEvent(UIMacroEvent targetUIMacroEvent) {
      this.targetUIMacroEvent = targetUIMacroEvent;
   }
   
   

}
