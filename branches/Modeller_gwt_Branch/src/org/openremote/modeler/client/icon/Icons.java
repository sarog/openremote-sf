package org.openremote.modeler.client.icon;

import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.ImageBundle;

public interface Icons extends ImageBundle {
   @Resource("folder.gif")
   AbstractImagePrototype folder();
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
