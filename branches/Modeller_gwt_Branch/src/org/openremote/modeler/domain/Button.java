package org.openremote.modeler.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

@SuppressWarnings("serial")
@Entity
public class Button extends UIBusinessEntity {
   
   private UIDesignerEventRef UIDesignerEventRef;
   private Screen screen;
   private int x;
   private int y;
   private int zIndex;

   @ManyToOne
   @JoinColumn(nullable = false)
   public Screen getScreen() {
      return screen;
   }

   public void setScreen(Screen screen) {
      this.screen = screen;
   }

   @OneToOne
   @JoinColumn(name="ui_designer_event_ref_oid")
   public UIDesignerEventRef getUIDesignerEventRef() {
      return UIDesignerEventRef;
   }

   public void setUIDesignerEventRef(UIDesignerEventRef uIDesignerEventRef) {
      UIDesignerEventRef = uIDesignerEventRef;
   }

   public int getX() {
      return x;
   }

   public void setX(int x) {
      this.x = x;
   }

   public int getY() {
      return y;
   }

   public void setY(int y) {
      this.y = y;
   }

   @Column(name="z_index")
   public int getzIndex() {
      return zIndex;
   }

   public void setzIndex(int zIndex) {
      this.zIndex = zIndex;
   }
   
}
