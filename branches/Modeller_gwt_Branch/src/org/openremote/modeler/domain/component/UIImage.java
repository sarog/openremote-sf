package org.openremote.modeler.domain.component;

import org.openremote.modeler.domain.Sensor;

@SuppressWarnings("serial")
public class UIImage extends UIComponent {

   private String src = "";
   
   private Sensor sensor = null;
   
   private UILabel label = null;
   
   public UIImage(){}
   public UIImage(long oid){
      super(oid);
   }
   
   public UIImage(UIImage uiImage) {
      this.setOid(uiImage.getOid());
      this.src = uiImage.src;
      this.sensor = uiImage.sensor;
      this.label = uiImage.label;
   }
   public String getSrc() {
      return src;
   }

   public void setSrc(String src) {
      this.src = src;
   }

   public Sensor getSensor() {
      return sensor;
   }

   public void setSensor(Sensor sensor) {
      this.sensor = sensor;
   }

   public UILabel getLabel() {
      return label;
   }

   public void setLabel(UILabel label) {
      this.label = label;
   }

   
   @Override
   public String getName() {
      return "Image";
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
   
}
