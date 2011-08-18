/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2010, OpenRemote Inc.
*
* See the contributors.txt file in the distribution for a
* full listing of individual contributors.
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as
* published by the Free Software Foundation, either version 3 of the
* License, or (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Affero General Public License for more details.
*
* You should have received a copy of the GNU Affero General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*/
package org.openremote.modeler.domain.component;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.Transient;

import org.openremote.modeler.domain.UICommand;
import org.openremote.modeler.utils.StringUtils;

import flexjson.JSON;

/**
 * UIButton defines some properties for changing display images, sending command and navigating.
 * Include default image, pressed image, navigate, uicommand and repeat.
 */
public class UIButton extends UIControl implements ImageSourceOwner{
   
   private static final long serialVersionUID = 2511411866454281810L;

   /** The label. */
   private String name = "Button";

   /** If pressed the button, repeat to send command or not. */
   private boolean repeate;
   
   /** Delay between each command send when repeating */
   private int repeatDelay = 100;
   
   /** The button's default image. */
   private ImageSource image;
   
   /** The button's pressed image. */
   private ImageSource pressImage;
   
   /** If click the button, navigate to. */
   private Navigate navigate = new Navigate();

   /** If click the button, send the uicommand. */
   private UICommand pressCommand;
   
   /** Command sent when button is released after short press */
   private UICommand shortReleaseCommand;
   
   /** Command sent when button is pressed for long time */
   private UICommand longPressCommand;
   
   /** Command sent when button released after long press */
   private UICommand longReleaseCommand;
   
   /** Delay after press for it to be considered long */
   private int longPressDelay = 250;

   /**
    * Instantiates a new uI button.
    */
   public UIButton() {
      super();
   }
   public UIButton(UIButton btn) {
      this.setOid(btn.getOid());
      this.name = btn.name;
      this.repeate = btn.repeate;
      this.repeatDelay = btn.repeatDelay;
      this.image = btn.image;
      this.navigate = btn.navigate;
      this.pressImage = btn.pressImage;
      this.pressCommand = btn.pressCommand;
      this.shortReleaseCommand = btn.shortReleaseCommand;
      this.longPressCommand = btn.longPressCommand;
      this.longReleaseCommand = btn.longReleaseCommand;
      this.longPressDelay = btn.longPressDelay;
   }
   /**
    * Instantiates a new uI button.
    * 
    * @param id the id
    */
   public UIButton(long id) {
      super(id);
   }

   /**
    * Gets the press command.
    * 
    * @return the ui command
    */
   public UICommand getPressCommand() {
      return pressCommand;
   }

   /**
    * Sets the press command.
    * 
    * @param pressCommand the new ui command
    */
   public void setPressCommand(UICommand pressCommand) {
      this.pressCommand = pressCommand;
   }
   
  public UICommand getShortReleaseCommand() {
    return shortReleaseCommand;
  }
   
  public void setShortReleaseCommand(UICommand shortReleaseCommand) {
    this.shortReleaseCommand = shortReleaseCommand;
  }
  
  public UICommand getLongPressCommand() {
    return longPressCommand;
  }
  
  public void setLongPressCommand(UICommand longPressCommand) {
    this.longPressCommand = longPressCommand;
  }
  
  public UICommand getLongReleaseCommand() {
    return longReleaseCommand;
  }
  
  public void setLongReleaseCommand(UICommand longReleaseCommand) {
    this.longReleaseCommand = longReleaseCommand;
  }
 
  public int getRepeatDelay() {
    return repeatDelay;
  }
  
  public void setRepeatDelay(int repeatDelay) {
    this.repeatDelay = repeatDelay;
  }
  
  public int getLongPressDelay() {
    return longPressDelay;
  }
  
  public void setLongPressDelay(int longPressDelay) {
    this.longPressDelay = longPressDelay;
  }
  
  @Override
   public String getName() {
      return name;
   }

   public boolean isRepeate() {
      return repeate;
   }

   public ImageSource getImage() {
      return image;
   }

   public ImageSource getPressImage() {
      return pressImage;
   }

   public Navigate getNavigate() {
      return navigate;
   }

   public void setName(String name) {
      this.name = name;
   }

   public void setRepeate(boolean repeate) {
      this.repeate = repeate;
   }

   public void setImage(ImageSource image) {
      this.image = image;
   }

   public void setPressImage(ImageSource pressImage) {
      this.pressImage = pressImage;
   }

   public void setNavigate(Navigate navigate) {
      this.navigate = navigate;
   }

   @Override
   @JSON(include=false)
   public List<UICommand> getCommands() {
      List<UICommand> commands = new ArrayList<UICommand>();
      if (pressCommand != null) {
         commands.add(pressCommand);
      }
      if (shortReleaseCommand != null) {
        commands.add(shortReleaseCommand);
      }
      if (longPressCommand != null) {
        commands.add(longPressCommand);
      }
      if (longReleaseCommand != null) {
        commands.add(longReleaseCommand);
      }
      return commands;
   }

   @Transient
   @JSON(include = false)
   @Override
   public String getPanelXml() {
      StringBuffer xmlContent = new StringBuffer();
      xmlContent.append("        <button id=\"" + getOid() + "\" name=\"" + StringUtils.escapeXml(getName()) + "\"");
      if (pressCommand != null) {
         xmlContent.append(" hasPressCommand=\"true\"");
      }
      if (shortReleaseCommand != null) {
        xmlContent.append(" hasShortReleaseCommand=\"true\"");
      }
      if (longPressCommand != null) {
        xmlContent.append(" hasLongPressCommand=\"true\"");
      }
      if (longReleaseCommand != null) {
        xmlContent.append(" hasLongReleaseCommand=\"true\"");
      }
      if (longPressCommand != null || longReleaseCommand != null) {
        xmlContent.append(" longPressDelay=\"" + longPressDelay + "\"");
      }
      
      if (repeate) {
         xmlContent.append(" repeat=\"" + repeate + "\" repeatDelay=\"" + repeatDelay + "\"");
      }
      xmlContent.append(">\n");
      if (image != null && image.getImageFileName() != null) {
         xmlContent.append("          <default>\n");
         xmlContent.append("          <image src=\"" + image.getImageFileName() + "\" />\n");
         xmlContent.append("          </default>\n");
      }
      if (pressImage != null && pressImage.getImageFileName() != null) {
         xmlContent.append("          <pressed>\n");
         xmlContent.append("          <image src=\"" + pressImage.getImageFileName() + "\" />\n");
         xmlContent.append("          </pressed>\n");
      }
      if (navigate.isSet()) {
         xmlContent.append("          <navigate");
         if (navigate.getToGroup() != -1) {
            xmlContent.append(" toGroup=\"" + navigate.getToGroup() + "\"");
            if (navigate.getToScreen() != -1) {
               xmlContent.append(" toScreen=\"" + navigate.getToScreen() + "\"");
            }
         } else {
            xmlContent.append(" to=\"" + navigate.getToLogical() + "\"");
         }
         xmlContent.append(" />\n");
      }
      xmlContent.append("        </button>\n");
      return xmlContent.toString();
   }
   @Override
   @JSON(include = false)
   public Collection<ImageSource> getImageSources() {
      Collection<ImageSource> imageSources = new ArrayList<ImageSource>(2);
      if (this.pressImage != null && !this.pressImage.isEmpty()) {
         imageSources.add(pressImage);
      }
      
      if (this.image != null && ! this.image.isEmpty()) {
         imageSources.add(image);
      }
      return imageSources;
   }

}
