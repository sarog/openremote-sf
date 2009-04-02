package org.openremote.controller.service;

import junit.framework.TestCase;

import org.openremote.controller.spring.SpringContext;

public class ButtonCommandServiceTest extends TestCase {

   private ButtonCommandService buttonCommandService = (ButtonCommandService) SpringContext.getInstance().getBean(
         "buttonCommandService");
   
   public void testTrigger(){
      buttonCommandService.trigger("4");
   }
   
}
