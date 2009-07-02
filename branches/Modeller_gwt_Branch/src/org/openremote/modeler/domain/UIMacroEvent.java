package org.openremote.modeler.domain;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name = "ui_macro_event")
public class UIMacroEvent extends UIBusinessEntity {
   
   private List<UIDesignerEventRef> UIDesignerEventRefs;

   @OneToMany(mappedBy = "parentUIMacroEvent", cascade = CascadeType.REMOVE)
   public List<UIDesignerEventRef> getUIDesignerEventRefs() {
      return UIDesignerEventRefs;
   }

   public void setUIDesignerEventRefs(List<UIDesignerEventRef> uIDesignerEventRefs) {
      UIDesignerEventRefs = uIDesignerEventRefs;
   }
}
