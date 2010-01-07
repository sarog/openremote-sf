package org.openremote.modeler.domain.component;

import java.util.ArrayList;
import java.util.List;

import org.openremote.modeler.domain.RangeSensor;
import org.openremote.modeler.domain.Sensor;
import org.openremote.modeler.domain.Slider;
import org.openremote.modeler.domain.UICommand;

@SuppressWarnings("serial")
public class UISlider extends UIControl implements SensorOwner{

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
      List<UICommand> commands = new ArrayList<UICommand>();
      if (slider != null && slider.getSetValueCmd() != null) {
         commands.add(slider.getSetValueCmd());
      }
      return commands;
   }

   @Override
   public String getPanelXml() {
      StringBuffer xmlContent = new StringBuffer();
      xmlContent.append("        <slider id=\"" + getOid() + "\" ");
      if (thumbImage != null) {
         xmlContent.append("thumbImage=\"" + thumbImage + "\" ");
      }
      if (vertical) {
         xmlContent.append("vertical=\"true\" ");
      }
      xmlContent.append(">\n");
      if(getSensor()!=null){
         xmlContent.append("<link type=\"sensor\" ref=\""+getSensor().getOid()+"\" />\n");
         if (getSensor() instanceof RangeSensor) {
            RangeSensor rangeSensor = (RangeSensor) getSensor();
            xmlContent.append("<min value=\"" + rangeSensor.getMin() + "\"");
            if (minImage != null) {
               xmlContent.append(" image=\"" + minImage + "\"");
            }
            if (minTrackImage != null) {
               xmlContent.append(" trackImage=\"" + minTrackImage + "\"");
            }
            xmlContent.append("/>\n");
            
            xmlContent.append("<max value=\"" + rangeSensor.getMax() + "\"");
            if (maxImage != null) {
               xmlContent.append("image=\"" + maxImage + "\" ");
            }
            if (maxTrackImage != null) {
               xmlContent.append("trackImage=\"" + maxTrackImage + "\" ");
            }
            xmlContent.append("/>\n");
         }
      }
      xmlContent.append("        </slider>\n");
      return xmlContent.toString();
   }

   @Override
   public void transImagePathToRelative(String relativeSessionFolderPath) {
      if (thumbImage != null) {
         thumbImage = relativeSessionFolderPath + thumbImage.substring(thumbImage.lastIndexOf("/") + 1);
      }
      if (minImage != null) {
         minImage = relativeSessionFolderPath + minImage.substring(minImage.lastIndexOf("/") + 1);
      }
      if (minTrackImage != null) {
         minTrackImage = relativeSessionFolderPath + minTrackImage.substring(minTrackImage.lastIndexOf("/") + 1);
      }
      if (maxImage != null) {
         maxImage = relativeSessionFolderPath + maxImage.substring(maxImage.lastIndexOf("/") + 1);
      }
      if (maxTrackImage != null) {
         maxTrackImage = relativeSessionFolderPath + maxTrackImage.substring(maxTrackImage.lastIndexOf("/") + 1);
      }
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

   @Override
   public Sensor getSensor() {
      if(slider!= null && slider.getSliderSensorRef()!=null){
         return slider.getSliderSensorRef().getSensor();
      }
      return null;
   }
}
