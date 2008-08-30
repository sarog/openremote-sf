package org.openremote.controller.protocol;

import org.openremote.controller.router.Router;

/**
 */
public class FridgeProtocolTranslator
{
  

  public String translateMessage(FridgeMessage msg)
  {
    return "no payload";
  }

  public FridgeMessage constructMessage(String messageFormat)
  {
    return new FridgeMessage();
  }
}

