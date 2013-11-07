package org.openremote.controller.protocol.domintell.model;

public interface Input {

   public void beginShortPush(Integer input);
   
   public void endShortPush(Integer input);
   
   public void beginLongPush(Integer input);
   
   public void endLongPush(Integer input);
   
   public void queryState(Integer input);
   
}
