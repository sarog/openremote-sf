package org.openremote.modeler.client.model;

import org.openremote.modeler.client.Constants;

public class UIButtonIREvent extends UIButtonEvent {
   
   @Override
   protected String getType() {
      return Constants.INFRARED_TYPE;
   }
}
