package org.openremote.modeler.client.model;

import org.openremote.modeler.client.Constants;

public class UIButtonX10Event extends UIButtonEvent {

   @Override
   protected String getType() {
      return Constants.X10_TYPE;
   }
}
