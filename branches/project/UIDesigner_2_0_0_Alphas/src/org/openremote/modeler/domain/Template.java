package org.openremote.modeler.domain;

public class Template extends BusinessEntity {
   private static final long serialVersionUID = -4719734393235222900L;
   
   public static final long PRIVATE = -1L;
   public static final long PUBLIC = 0L;
   
   private long shareTo = PRIVATE ;
   
   private String name = "";
   private String content = "";
//   private String model = "";
//   private String type = "";
//   private String vendor = "";
   private String keywords = "";
   private boolean shared = false;
   
   private Screen screen;
   
   public Template(){}
   
   public Template(String name,Screen screen){
      this.name = name;
      this.screen = screen;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getContent() {
      return content;
   }

   public void setContent(String content) {
      this.content = content;
   }

   
   /*public String getModel() {
      return model;
   }

   public void setModel(String model) {
      this.model = model;
   }

   public String getType() {
      return type;
   }

   public void setType(String type) {
      this.type = type;
   }

   public String getVendor() {
      return vendor;
   }

   public void setVendor(String vendor) {
      this.vendor = vendor;
   }*/

   public String getKeywords() {
      return keywords;
   }

   public void setKeywords(String keywords) {
      this.keywords = keywords;
   }

   public boolean isShared() {
      return shared;
   }

   public void setShared(boolean shared) {
      this.shared = shared;
   }

   public Screen getScreen() {
      return screen;
   }

   public void setScreen(Screen screen) {
      this.screen = screen;
   }

   public long getShareTo() {
      return shareTo;
   }

   public void setShareTo(long shareTo) {
      this.shareTo = shareTo;
   }
   
   public String getDisplayName() {
      return name + "( " +keywords +" )";
   }
}
