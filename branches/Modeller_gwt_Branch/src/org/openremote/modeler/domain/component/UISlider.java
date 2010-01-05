package org.openremote.modeler.domain.component;

import java.util.List;

import org.openremote.modeler.domain.Slider;
import org.openremote.modeler.domain.UICommand;

@SuppressWarnings("serial")
public class UISlider extends UIControl {

   private boolean vertical = false;
   private String thumbImage;
   private String minImage;
   private String minTrackImage;
   private String maxImage;
   private String maxTrackImage;
   private Slider slider;
   
   public UISlider() {
   }
   
   public UISlider(long id) {
      super(id);
   }
   
   public UISlider(UISlider uiSlider) {
      this.setOid(uiSlider.getOid());
      this.vertical = uiSlider.isVertical();
      this.thumbImage = uiSlider.getThumbImage();
      this.minImage = uiSlider.getMinImage();
      this.minTrackImage = uiSlider.getMinTrackImage();
      this.maxImage = uiSlider.getMaxImage();
      this.maxTrackImage = uiSlider.getMaxTrackImage();
   }
   public boolean isVertical() {
      return vertical;
   }

   public void setVertical(boolean vertical) {
      this.vertical = vertical;
   }

   public String getThumbImage() {
      return thumbImage;
   }

   public String getMinImage() {
      return minImage;
   }

   public String getMinTrackImage() {
      return minTrackImage;
   }

   public String getMaxImage() {
      return maxImage;
   }

   public String getMaxTrackImage() {
      return maxTrackImage;
   }

   public void setThumbImage(String thumbImage) {
      this.thumbImage = thumbImage;
   }

   public void setMinImage(String minImage) {
      this.minImage = minImage;
   }

   public void setMinTrackImage(String minTrackImage) {
      this.minTrackImage = minTrackImage;
   }

   public void setMaxImage(String maxImage) {
      this.maxImage = maxImage;
   }

   public void setMaxTrackImage(String maxTrackImage) {
      this.maxTrackImage = maxTrackImage;
   }

   public Slider getSlider() {
      return slider;
   }

   public void setSlider(Slider slider) {
      this.slider = slider;
   }

   @Override
   public List<UICommand> getCommands() {
      return null;
   }

   @Override
   public String getPanelXml() {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public void transImagePathToRelative(String relativeSessionFolderPath) {
      // TODO Auto-generated method stub

   }

   @Override
   public String getName() {
      return "Slider";
   }
   
   public @Override int getPreferredWidth(){
      int width = 150;
      return width;
   }
   
   public @Override int getPreferredHeight(){
      int height = 20;
      return height;
   }
}
