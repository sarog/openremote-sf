package org.openremote.ir.domain;

import java.io.Serializable;

/**
 * allows to exchange xcfFileParser.IRCommand necessary information with the client side
 * @author wbalcaen
 *
 */
public class IRCommandInfo implements Serializable {

   private static final long serialVersionUID = 1L;

   private String name;
   private String code;
   private String originalCode;
   private String comment;
   private CodeSetInfo codeSet;
   
   public IRCommandInfo() {
   }

   public IRCommandInfo(String name, String code, String originalCode,
         String comment, CodeSetInfo codeSet) {
      setName(name);
      setCode(code);
      setOriginalCode(originalCode);
      setComment(comment);
      setCodeSet(codeSet);
   }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getOriginalCode() {
    return originalCode;
  }

  public void setOriginalCode(String originalCode) {
    this.originalCode = originalCode;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public CodeSetInfo getCodeSet() {
    return codeSet;
  }

  public void setCodeSet(CodeSetInfo codeSet) {
    this.codeSet = codeSet;
  }

}
