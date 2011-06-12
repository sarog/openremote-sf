package org.openremote.web.console.domain;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Forwards to screen or do other logical functions.
 * Includes to group, to screen, to previous screen , to next screen, back, login, logout and setting.
 */
@SuppressWarnings("serial")
public class Navigate extends BusinessEntity {

   private int toScreen;
   private int toGroup;
   
   /** To previous screen in a group. */
   private boolean previousScreen;
   
   /** To next screen in a group. */
   private boolean nextScreen;
   
   /** Back to last display screen. */
   private boolean back;
   
   /** To display settings window. */
   private boolean setting;
   
   /** To display login view. */
   private boolean login;
   
   /** Make the user to logout. */
   private boolean logout;

   /**
    * Instantiates a new navigate without properties.
    */
   public Navigate() {
   }
   
   /**
    * Instantiates a new navigate by parse navigate node.
    * 
    * @param node the navigate node
    */
   public Navigate(Node node) {
      NamedNodeMap nodeMap = node.getAttributes();
      if (nodeMap.getNamedItem("toGroup") != null) {
         this.toGroup = Integer.valueOf(nodeMap.getNamedItem("toGroup").getNodeValue());
         if (nodeMap.getNamedItem("toScreen") != null) {
            this.toScreen = Integer.valueOf(nodeMap.getNamedItem("toScreen").getNodeValue());
         }
      } else if (nodeMap.getNamedItem("to") != null) {
         String toValue = nodeMap.getNamedItem("to").getNodeValue().toLowerCase();
         if ("previousscreen".equals(toValue)) {
            this.previousScreen = true;
         } else if ("nextscreen".equals(toValue)) {
            this.nextScreen = true;
         } else if ("login".equals(toValue)) {
            this.login = true;
         } else if ("logout".equals(toValue)) {
            this.logout = true;
         } else if ("setting".equals(toValue)) {
            this.setting = true;
         } else if ("back".equals(toValue)) {
            this.back = true;
         }
      }
   }

   public Navigate(int toGroup, int toScreen) {
      this.toGroup = toGroup;
      this.toScreen = toScreen;
   }
   
   public boolean isPreviousScreen() {
      return previousScreen;
   }

   public boolean isNextScreen() {
      return nextScreen;
   }

   public boolean isBack() {
      return back;
   }

   public boolean isSetting() {
      return setting;
   }

   public boolean isLogin() {
      return login;
   }

   public boolean isLogout() {
      return logout;
   }

   public int getToScreen() {
      return toScreen;
   }

   public int getToGroup() {
      return toGroup;
   }

   public void setToScreen(int toScreen) {
      this.toScreen = toScreen;
   }

   public void setToGroup(int toGroup) {
      this.toGroup = toGroup;
   }

   public void setPreviousScreen(boolean previousScreen) {
      this.previousScreen = previousScreen;
   }

   public void setNextScreen(boolean nextScreen) {
      this.nextScreen = nextScreen;
   }

   public void setBack(boolean back) {
      this.back = back;
   }

   public void setLogin(boolean login) {
      this.login = login;
   }

   public void setLogout(boolean logout) {
      this.logout = logout;
   }

}
