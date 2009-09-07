package org.openremote.modeler.client.model;

import org.openremote.modeler.client.Constants;

public class UIButtonKNXEvent extends UIButtonEvent {

   @Override
   protected String getType() {
      return Constants.KNX_TYPE;
   }
}
