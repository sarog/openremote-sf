package org.openremote.modeler.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import flexjson.JSON;

@SuppressWarnings("serial")
@Entity
@Table(name = "config")
public class Config extends BusinessEntity{
   public static final String NAME_XML_ATTRIBUTE_NAME = "name";
   public static final String VALUE_XML_ATTRIBUTE_NAME = "value";
   public static final String VALIDATION_XML_ATTRIBUTE_NAME = "validation";
   public static final String OPTION_XML_ATTRIBUTE_NAME = "options";
   public static final String OPTION_SPLIT_SEPARATOR = ",";
   
   public static final String HINT_XML_NODE_NAME = "hint";
   public static final String XML_NODE_NAME = "config";
   
   private String category = "";
   private String name = "";
   private String value = "";
   private String hint = "";
   private String validation = ".+";
   private String options = "";
   
   private Account account = null;
   
   public Config(){}
   
   
   public Config(String category, String name, String value, String hint, Account account) {
      this.category = category;
      this.name = name;
      this.value = value;
      this.hint = hint;
      this.account = account;
   }


   @Column(nullable = false)
   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }
   @Transient
   @JSON(include=true)
   public String getHint() {
      return hint;
   }

   public void setHint(String hint) {
      this.hint = hint;
   }
   @Column(nullable = false)
   public String getValue() {
      return value;
   }

   public void setValue(String value) {
      this.value = value;
   }

   @ManyToOne
   @JSON(include = false)
   public Account getAccount() {
      return account;
   }

   public void setAccount(Account account) {
      this.account = account;
   }
   
   public String getCategory() {
      return category;
   }
   
   public void setCategory(String category) {
      this.category = category;
   }
   @Transient
   public String getValidation() {
      return validation;
   }


   public void setValidation(String validation) {
      this.validation = validation;
   }

   @Transient
   public String getOptions() {
      return options;
   }


   public void setOptions(String options) {
      this.options = options;
   }
   
   @Transient
   @JSON(include=false)
   public String[] optionsArray(){
      return options.split(OPTION_SPLIT_SEPARATOR);
   }
   
   @Transient
   public String toString(){
      StringBuilder sb = new StringBuilder();
      sb.append("<"+XML_NODE_NAME+" "+NAME_XML_ATTRIBUTE_NAME+"=\""+name+ "\""+ VALUE_XML_ATTRIBUTE_NAME +"=\""+value+"\" " +VALIDATION_XML_ATTRIBUTE_NAME+"=\""+validation+"\" "+OPTION_XML_ATTRIBUTE_NAME+"=\""+options+"\">\n");
      sb.append("\t<"+HINT_XML_NODE_NAME+">\n");
      sb.append(hint+"\n");
      sb.append("\t</"+HINT_XML_NODE_NAME+">\n");
      sb.append("</"+XML_NODE_NAME+">");
      return sb.toString();
   }
}
