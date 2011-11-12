package org.openremote.web.console.panel.entity;

/**
 * Forwards to screen or do other logical functions.
 * Includes to group, to screen, to previous screen , to next screen, back, login, logout and setting.
 */
public interface ButtonDefault {
   Image getImage();

   void setImage(Image img);
}
