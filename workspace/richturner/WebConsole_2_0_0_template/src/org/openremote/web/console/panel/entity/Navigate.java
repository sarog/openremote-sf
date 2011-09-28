package org.openremote.web.console.panel.entity;

/**
 * Forwards to screen or do other logical functions.
 * Includes to group, to screen, to previous screen , to next screen, back, login, logout and setting.
 */
public interface Navigate {
   String getTo();
   String getToGroup();
   String getToScreen();

   void setTo(String to);
   void setToGroup(String toGroup);
   void setToScreen(String toScreen);
}
