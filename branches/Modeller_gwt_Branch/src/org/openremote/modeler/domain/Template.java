package org.openremote.modeler.domain;

@SuppressWarnings("serial")
public class Template extends BusinessEntity {
   private String name;
   private String content;
   
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

   public Screen getScreen() {
      return screen;
   }

   public void setScreen(Screen screen) {
      this.screen = screen;
   }
   
   
}
