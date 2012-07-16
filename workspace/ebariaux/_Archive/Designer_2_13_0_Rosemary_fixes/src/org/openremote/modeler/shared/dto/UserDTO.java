package org.openremote.modeler.shared.dto;

public class UserDTO implements DTO {

  private static final long serialVersionUID = 1L;
  
  private String userName;
  private String eMail;
  private String role;
  private long oid;
  
  public UserDTO() {
    super();
  }

  public UserDTO(long oid, String userName, String eMail, String role) {
    super();
    this.oid = oid;
    this.userName = userName;
    this.eMail = eMail;
    this.role = role;
  }

  public String getUserName() {
    return userName;
  }
  
  public void setUserName(String userName) {
    this.userName = userName;
  }
  
  public long getOid() {
    return oid;
  }
  
  public void setOid(long oid) {
    this.oid = oid;
  }

  public String geteMail() {
    return eMail;
  }

  public void seteMail(String eMail) {
    this.eMail = eMail;
  }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }

}
