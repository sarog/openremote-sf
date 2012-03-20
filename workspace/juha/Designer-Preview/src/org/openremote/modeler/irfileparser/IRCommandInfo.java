package org.openremote.modeler.irfileparser;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * allows to exchange xcfFileParser.IRCommand necessary information with the client side
 * @author wbalcaen
 *
 */
public class IRCommandInfo extends BaseModel implements IsSerializable {

   private static final long serialVersionUID = 1L;

   public IRCommandInfo() {
   }

   public IRCommandInfo(String name, String code, String originalCode,
         String comment, CodeSetInfo codeSet) {
      setName(name);
      setCode(code);
      setOriginalCodeString(originalCode);
      setComment(comment);
      setCodeSet(codeSet);
   }

   /**
    * returns the code set information
    * 
    * @return CodeSetInfo
    */
   public CodeSetInfo getCodeSet() {
      return get("codeSet");
   }

   /**
    * sets the code set info
    * 
    * @param codeSet
    */
   public void setCodeSet(CodeSetInfo codeSet) {
      set("codeSet", codeSet);

   }

   /**
    * returns the original code string
    * 
    * @return String
    */
   public String getOriginalCodeString() {
      return get("originalCode");
   }

   /**
    * sets the original code string
    * 
    * @param originalCode
    */
   public void setOriginalCodeString(String originalCode) {
      set("originalCode", originalCode);

   }

   /**
    * returns the comment
    * 
    * @return String
    */
   public String getComment() {
      return get("comment");
   }

   /**
    * sets the comment
    * 
    * @param comment
    */
   public void setComment(String comment) {
      set("comment", comment);

   }

   /**
    * returns the name
    * 
    * @return String
    */
   public String getName() {
      return get("name");
   }

   /**
    * sets the name
    * 
    * @param name
    */
   public void setName(String name) {
      set("name", name);
   }

   /**
    * returns the code
    * 
    * @return String
    */
   public String getCode() {
      return get("code");
   }

   /**
    * @param code
    */
   public void setCode(String code) {
      set("code", code);
   }

}
