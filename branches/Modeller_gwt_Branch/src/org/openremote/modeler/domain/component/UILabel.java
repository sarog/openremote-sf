package org.openremote.modeler.domain.component;

import javax.persistence.Transient;

import org.openremote.modeler.domain.Sensor;

@SuppressWarnings("serial")
public class UILabel extends UIComponent {

   private String text = "Label Text";
   private String font = "";
   private String color = "black";
   private int fontSize = 10;

   private Sensor sensor;

   public UILabel(long oid) {
     super(oid);
   }
   public UILabel() { }

   
   public UILabel(String text, String color, int fontSize, Sensor sensor) {
      this.text = text;
      this.color = color;
      this.fontSize = fontSize;
      this.sensor = sensor;
   }


   public UILabel(UILabel uiLabel) {
      this.text = uiLabel.text;
      this.font = uiLabel.font;
      this.fontSize = uiLabel.fontSize;
   }
   public String getText() {
      return text;
   }

   public void setText(String text) {
      this.text = text;
   }

   public String getFont() {
      return font;
   }

   public void setFont(String font) {
      this.font = font;
   }
   
   public String getColor() {
      return color;
   }
   public void setColor(String color) {
      this.color = color;
   }
   public int getFontSize() {
      return fontSize;
   }

   public void setFontSize(int fontSize) {
      this.fontSize = fontSize;
   }

   public Sensor getSensor() {
      return sensor;
   }

   public void setSensor(Sensor sensor) {
      this.sensor = sensor;
   }

   @Override
   public String getPanelXml() {
      //TODO 
      return null;
   }

   @Override
   public void transImagePathToRelative(String relativeSessionFolderPath) {
      //TODO
   }


   @Override
   public String getName() {
      return "Label";
   }
   
   @Transient
   public String getDisplayName() {
      int maxLength = 10;
      if(text.length()>maxLength){
         return text.substring(0,text.length()-maxLength)+"...";
      }
      return text;
   }
}
