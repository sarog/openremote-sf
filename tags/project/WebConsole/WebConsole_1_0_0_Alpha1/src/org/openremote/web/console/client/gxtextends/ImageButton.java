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
package org.openremote.web.console.client.gxtextends;

import org.openremote.web.console.client.Constants;
import org.openremote.web.console.client.rpc.AsyncSuccessCallback;
import org.openremote.web.console.client.utils.ClientDataBase;
import org.openremote.web.console.client.utils.ORListenerManager;
import org.openremote.web.console.client.widget.ScreenButton;
import org.openremote.web.console.domain.Button;

import com.extjs.gxt.ui.client.core.Template;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;

/**
 * Generate a image button component by {@link org.openremote.web.console.domain.Button}.
 */
public class ImageButton extends com.extjs.gxt.ui.client.widget.button.Button {

   private boolean hasIcon;
   private Button uiButton;
   private String defaultImage;
   private String pressedImage;
   private final static int REPEAT_CMD_INTERVAL = 300;
   private Timer timer;
   
   /** The failed times is for repeat send command failed. */
   private int failedTimes;
   
   public ImageButton(Button uiButton) {
      this.uiButton = uiButton;
      hasIcon = uiButton.getDefaultImage() != null;
      if (hasIcon) {
         defaultImage = ClientDataBase.getResourceRootPath() + URL.encode(uiButton.getDefaultImage().getSrc());
      }
      if (uiButton.getPressedImage() != null) {
         pressedImage = ClientDataBase.getResourceRootPath() + URL.encode(uiButton.getPressedImage().getSrc());
      }
      setSize(uiButton.getFrameWidth(), uiButton.getFrameHeight());
      setText(uiButton.getName());
   }
   
   @Override
   protected void onRender(Element target, int index) {
      if (hasIcon) {
         template = new Template("<div style=\"height:100%; text-align: center; background: url(" + defaultImage
               + ") no-repeat 0 0;\"><button style=\"margin-top:30%;\">" + uiButton.getName()
               + "</button></div>");
      }
      super.onRender(target, index);
   }

   @Override
   protected void onMouseDown(ComponentEvent ce) {
      super.onMouseDown(ce);
      failedTimes = 0;
      cancelTimer();
      if (pressedImage != null) {
         setStyleAttribute("backgroundImage", "url(" + pressedImage + ")");
      } else if (hasIcon) {
         addStyleName("pressed");
      }
      if (uiButton.isHasControlCommand()) {
         sendCommand();
         if (uiButton.isRepeat()) {
            startTimer();
         }
      }
   }

   @Override
   protected void onMouseUp(ComponentEvent ce) {
      super.onMouseUp(ce);
      cancelTimer();
      resetIcon();
      if (uiButton.getNavigate() != null) {
         removeStyleName("x-btn-over");
         ORListenerManager.getInstance().notifyOREventListener(Constants.ListenerNavigateTo, uiButton.getNavigate());
      }
   }

   /**
    * Send "click" command.
    */
   private void sendCommand() {
      ((ScreenButton) getParent()).sendCommand("click", new AsyncSuccessCallback<Void>() {
         public void onSuccess(Void result) {
            // do nothing.
         }

         public void onFailure(Throwable caught) {
            failedTimes = failedTimes + 1;
            if (failedTimes == 1) {
               cancelTimer();
               resetIcon();
               super.onFailure(caught);
            }
         }
      });
   }
   
   /**
    * Repeat command when the button is repeatable.
    */
   private void startTimer() {
      timer = new Timer() {
         @Override
         public void run() {
            sendCommand();
         }
      };
      timer.scheduleRepeating(REPEAT_CMD_INTERVAL);
   }
   
   /**
    * Cancel repeat send command when MouseUp.
    */
   private void cancelTimer() {
      if (timer != null) {
         timer.cancel();
      }
   }
   
   /**
    * Set default state for button when MouseUp.
    */
   private void resetIcon() {
      if (hasIcon) {
         if (pressedImage != null) {
            setStyleAttribute("backgroundImage", "url(" + defaultImage + ")");
         } else {
            removeStyleName("pressed");
         }
      }
   }
   
}
