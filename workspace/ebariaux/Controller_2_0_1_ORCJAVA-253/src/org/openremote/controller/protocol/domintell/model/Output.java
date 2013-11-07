package org.openremote.controller.protocol.domintell.model;

public interface Output {

   public void on(Integer output);
   
   public void off(Integer output);
   
   public void toggle(Integer output);
   
   public void queryState(Integer output);

}
