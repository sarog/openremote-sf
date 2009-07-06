package org.openremote.modeler.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("serial")
public class Authority implements Serializable {
   private String username;
   private List<String> roles;
   
   public Authority() {
      roles = new ArrayList<String>();
   }
   public String getUsername() {
      return username;
   }
   public List<String> getRoles() {
      return roles;
   }
   public void setUsername(String username) {
      this.username = username;
   }

   public void addRole(String role){
      roles.add(role);
   }
   
   
   
}
