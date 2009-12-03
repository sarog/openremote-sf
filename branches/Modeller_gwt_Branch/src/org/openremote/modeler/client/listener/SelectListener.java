package org.openremote.modeler.client.listener;

import org.openremote.modeler.client.event.SelectEvent;

import com.extjs.gxt.ui.client.event.Listener;

public abstract class SelectListener implements Listener<SelectEvent> {

   public void handleEvent(SelectEvent be) {
      if (be.getType() == SelectEvent.SELECT) {
         afterSelect(be);
      }
   }
   
   public abstract void afterSelect(SelectEvent be);
}
