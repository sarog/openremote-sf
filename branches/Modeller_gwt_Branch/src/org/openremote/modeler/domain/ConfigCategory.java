package org.openremote.modeler.domain;

@SuppressWarnings("serial")
public class ConfigCategory extends BusinessEntity{
   public static final String XML_NODE_NAME = "category";
   public static final String NAME_XML_ATRIBUTE_NAME = "name";
   public static final String DESCRIBTION_NODE_NAME = "description";
   
   
   private String name = "";
   private String description = "";
   
   public ConfigCategory(){};
   public ConfigCategory(String name,String description){
      this.name = name;
      this.description = description;
   }
   public String getName() {
      return name;
   }
   public void setName(String name) {
      this.name = name;
   }
   public String getDescription() {
      return description;
   }
   public void setDescription(String description) {
      this.description = description;
   }
}
