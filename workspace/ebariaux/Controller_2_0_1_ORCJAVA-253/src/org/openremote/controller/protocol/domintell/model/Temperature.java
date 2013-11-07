package org.openremote.controller.protocol.domintell.model;

import org.openremote.controller.protocol.domintell.TemperatureMode;

public interface Temperature {
   
   public void setSetPoint(Float setPoint);
   
   public void setMode(TemperatureMode mode);
   
   public void queryState();

}
