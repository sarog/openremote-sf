package org.openremote.modeler.client.icon;

import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.ImageBundle;

public interface Icons extends ImageBundle {
   @Resource("folder.gif")
   AbstractImagePrototype folder();
   @Resource("cmd_add.png")
   AbstractImagePrototype addCmd();
   @Resource("cmd.png")
   AbstractImagePrototype deviceCmd();
   @Resource("delete.png")
   AbstractImagePrototype delete();
   @Resource("tv_add.png")
   AbstractImagePrototype addDevice();
   @Resource("tv.png")
   AbstractImagePrototype device();
   @Resource("pencil.png")
   AbstractImagePrototype edit();
   @Resource("add.png")
   AbstractImagePrototype add();
   @Resource("database_go.png")
   AbstractImagePrototype importFromDB();
   
   @Resource("brick_add.png")
   AbstractImagePrototype macroAddIcon();
   @Resource("brick_delete.png")
   AbstractImagePrototype macroDeleteIcon();
   @Resource("brick_edit.png")
   AbstractImagePrototype macroEditIcon();
   @Resource("brick.png")
   AbstractImagePrototype macroIcon();
   @Resource("add_delay.png")
   AbstractImagePrototype addDelayIcon();
}
