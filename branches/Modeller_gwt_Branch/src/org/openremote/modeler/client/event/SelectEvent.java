package org.openremote.modeler.client.event;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.EventType;

public class SelectEvent extends BaseEvent {

   public static final EventType SELECT = new EventType();
   private Object data;
   
   public SelectEvent() {
      super(SELECT);
   }
   
   public SelectEvent(Object d) {
      this();
      this.data = d;
   }
   
   @SuppressWarnings("unchecked")
   public <X> X getData() {
      return (X) data;
    }
}
