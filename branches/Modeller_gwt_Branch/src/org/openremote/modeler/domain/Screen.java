package org.openremote.modeler.domain;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@SuppressWarnings("serial")
@Entity
public class Screen extends UIBusinessEntity {
   
   private List<Button> buttons;
   
   private Activity activity;
   
   private String bgImagePath;

   @OneToMany(mappedBy="screen")
   public List<Button> getButtons() {
      return buttons;
   }

   public void setButtons(List<Button> buttons) {
      this.buttons = buttons;
   }

   @ManyToOne
   @JoinColumn(nullable = false)
   public Activity getActivity() {
      return activity;
   }

   public void setActivity(Activity activity) {
      this.activity = activity;
   }

   @Column(name="bg_image_path")
   public String getBgImagePath() {
      return bgImagePath;
   }

   public void setBgImagePath(String bgImagePath) {
      this.bgImagePath = bgImagePath;
   }
   
   
}
