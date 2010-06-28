package org.openremote.web.console.domain;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

@SuppressWarnings("serial")
public class Navigate extends BusinessEntity {

   private int toScreen;
   private int toGroup;
   private boolean previousScreen;
   private boolean nextScreen;
   private boolean back;
   private boolean setting;
   private boolean login;
   private boolean logout;
   private int fromScreen;
   private int fromGroup;

   public Navigate() {
   }
   
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

   public int getFromScreen() {
      return fromScreen;
   }

   public int getFromGroup() {
      return fromGroup;
   }

   public void setToScreen(int toScreen) {
      this.toScreen = toScreen;
   }

   public void setToGroup(int toGroup) {
      this.toGroup = toGroup;
   }

   public void setFromScreen(int fromScreen) {
      this.fromScreen = fromScreen;
   }

   public void setFromGroup(int fromGroup) {
      this.fromGroup = fromGroup;
   }

}
