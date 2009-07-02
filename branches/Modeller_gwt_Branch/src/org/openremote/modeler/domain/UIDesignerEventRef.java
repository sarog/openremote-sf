package org.openremote.modeler.domain;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "ref_type", discriminatorType=DiscriminatorType.STRING)
@DiscriminatorValue("UI_DESIGNER_EVENT_REF")
@Table(name = "ui_designer_event_ref")
public class UIDesignerEventRef extends BusinessEntity {
   
   private UIMacroEvent parentUIMacroEvent;
   private Button triggerButton;

   @ManyToOne
   @JoinColumn(name = "parent_ui_macro_event_oid")
   public UIMacroEvent getParentUIMacroEvent() {
      return parentUIMacroEvent;
   }

   public void setParentUIMacroEvent(UIMacroEvent parentUIMacroEvent) {
      this.parentUIMacroEvent = parentUIMacroEvent;
   }

   @OneToOne(mappedBy="UIDesignerEventRef")
   public Button getTriggerButton() {
      return triggerButton;
   }

   public void setTriggerButton(Button triggerButton) {
      this.triggerButton = triggerButton;
   }

}
