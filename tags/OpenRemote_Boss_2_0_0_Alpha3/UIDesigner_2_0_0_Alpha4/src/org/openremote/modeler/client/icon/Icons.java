/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2009, OpenRemote Inc.
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
package org.openremote.modeler.client.icon;

import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.ImageBundle;

/**
 * The Interface Icons.
 */
public interface Icons extends ImageBundle {

   /**
    * Folder.
    * 
    * @return the abstract image prototype
    */
   @Resource("folder.gif")
   AbstractImagePrototype folder();

   /**
    * Adds the cmd.
    * 
    * @return the abstract image prototype
    */
   @Resource("cmd_add.png")
   AbstractImagePrototype addCmd();

   /**
    * Device cmd.
    * 
    * @return the abstract image prototype
    */
   @Resource("cmd.png")
   AbstractImagePrototype deviceCmd();

   /**
    * Delete.
    * 
    * @return the abstract image prototype
    */
   @Resource("delete.png")
   AbstractImagePrototype delete();

   /**
    * Adds the device.
    * 
    * @return the abstract image prototype
    */
   @Resource("tv_add.png")
   AbstractImagePrototype addDevice();

   /**
    * Device.
    * 
    * @return the abstract image prototype
    */
   @Resource("tv.png")
   AbstractImagePrototype device();

   /**
    * Edits the.
    * 
    * @return the abstract image prototype
    */
   @Resource("edit.png")
   AbstractImagePrototype edit();

   /**
    * Adds the.
    * 
    * @return the abstract image prototype
    */
   @Resource("add.png")
   AbstractImagePrototype add();

   /**
    * Import from db.
    * 
    * @return the abstract image prototype
    */
   @Resource("database_go.png")
   AbstractImagePrototype importFromDB();

   /**
    * Macro add icon.
    * 
    * @return the abstract image prototype
    */
   @Resource("brick_add.png")
   AbstractImagePrototype macroAddIcon();

   /**
    * Macro delete icon.
    * 
    * @return the abstract image prototype
    */
   @Resource("brick_delete.png")
   AbstractImagePrototype macroDeleteIcon();

   /**
    * Macro edit icon.
    * 
    * @return the abstract image prototype
    */
   @Resource("brick_edit.png")
   AbstractImagePrototype macroEditIcon();

   /**
    * Macro icon.
    * 
    * @return the abstract image prototype
    */
   @Resource("brick.png")
   AbstractImagePrototype macroIcon();

   /**
    * Adds the delay icon.
    * 
    * @return the abstract image prototype
    */
   @Resource("add_delay.png")
   AbstractImagePrototype addDelayIcon();

   /**
    * Edits the delay icon.
    * 
    * @return the abstract image prototype
    */
   @Resource("edit_delay.png")
   AbstractImagePrototype editDelayIcon();

   /**
    * Delay icon.
    * 
    * @return the abstract image prototype
    */
   @Resource("delay.png")
   AbstractImagePrototype delayIcon();

   /**
    * Activity icon.
    * 
    * @return the abstract image prototype
    */
   @Resource("activity.png")
   AbstractImagePrototype activityIcon();

   /**
    * Activity edit icon.
    * 
    * @return the abstract image prototype
    */
   @Resource("activity_edit.png")
   AbstractImagePrototype editActivityIcon();

   /**
    * Activity delete icon.
    * 
    * @return the abstract image prototype
    */
   @Resource("activity_del.png")
   AbstractImagePrototype delActivityIcon();

   /**
    * Activity add icon.
    * 
    * @return the abstract image prototype
    */
   @Resource("activity_add.png")
   AbstractImagePrototype addActivityIcon();

   /**
    * Export icon.
    * 
    * @return the abstract image prototype
    */
   @Resource("export.gif")
   AbstractImagePrototype exportIcon();
   
   /**
    * Import icon.
    * 
    * @return the abstract image prototype
    */
   @Resource("import.gif")
   AbstractImagePrototype importIcon();
   
   
   /**
    * Screen icon.
    * 
    * @return the abstract image prototype
    */
   @Resource("screen.png")
   AbstractImagePrototype screenIcon();
   
   /**
    * Screen-add icon.
    * 
    * @return the abstract image prototype
    */
   @Resource("screen_add.png")
   AbstractImagePrototype addScreenIcon();
   
   /**
    * Screen-delete icon.
    * 
    * @return the abstract image prototype
    */
   @Resource("screen_delete.png")
   AbstractImagePrototype delScreenIcon();
   
   /**
    * Screen-edit icon.
    * 
    * @return the abstract image prototype
    */
   @Resource("screen_edit.png")
   AbstractImagePrototype editScreenIcon();
   
   /**
    * Devices root.
    * 
    * @return the abstract image prototype
    */
   @Resource("devices.png")
   AbstractImagePrototype devicesRoot();
   
   /**
    * Macros root.
    * 
    * @return the abstract image prototype
    */
   @Resource("macros.png")
   AbstractImagePrototype macrosRoot();
   
   /**
    * Logout.
    * 
    * @return the abstract image prototype
    */
   @Resource("log_out.png")
   AbstractImagePrototype logout();

   /**
    * Save icon.
    * 
    * @return the abstract image prototype
    */
   @Resource("save.gif")
   AbstractImagePrototype saveIcon();
   
   /**
    * Change icon.
    * 
    * @return the abstract image prototype
    */
   @Resource("picture_edit.png")
   AbstractImagePrototype changeIcon();
   
   /**
    * Button icon.
    * 
    * @return the abstract image prototype
    */
   @Resource("button.png")
   AbstractImagePrototype buttonIcon();
   
   /**
    * Switch icon.
    * 
    * @return the abstract image prototype
    */
   @Resource("switch.png")
   AbstractImagePrototype switchIcon();
   
   @Resource("panel.png")
   AbstractImagePrototype panelIcon();
   
   @Resource("grid.png")
   AbstractImagePrototype gridIcon();
   
   @Resource("tabbar_config.png")
   AbstractImagePrototype tabbarConfigIcon();
   
   @Resource("switch_add.png")
   AbstractImagePrototype switchAddIcon();
   
   @Resource("switch_edit.png")
   AbstractImagePrototype switchEditIcon();
   
   @Resource("switch_delete.png")
   AbstractImagePrototype switchDeleteIcon();
   
   @Resource("slider.png")
   AbstractImagePrototype sliderIcon();
   
   @Resource("vslider.png")
   AbstractImagePrototype vsliderIcon();
   
   @Resource("slider_add.png")
   AbstractImagePrototype sliderAddIcon();
   
   @Resource("slider_edit.png")
   AbstractImagePrototype sliderEditIcon();
   
   @Resource("slider_delete.png")
   AbstractImagePrototype sliderDeleteIcon();
   
   @Resource("label.gif")
   AbstractImagePrototype labelIcon();

   @Resource("image.png")
   AbstractImagePrototype imageIcon();

   @Resource("sensor.png")
   AbstractImagePrototype sensorIcon();

   @Resource("sensor_add.png")
   AbstractImagePrototype sensorAddIcon();

   @Resource("sensor_edit.png")
   AbstractImagePrototype sensorEditIcon();

   @Resource("sensor_delete.png")
   AbstractImagePrototype sensorDeleteIcon();
   
   @Resource("config.png")
   AbstractImagePrototype configIcon();
   
   @Resource("template.gif")
   AbstractImagePrototype templateIcon();
   
   @Resource("bm.png")
   AbstractImagePrototype bmIcon();
   
   @Resource("ud.png")
   AbstractImagePrototype udIcon();
   
   @Resource("export_zip.png")
   AbstractImagePrototype exportAsZipIcon();
}
