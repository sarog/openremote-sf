package org.openremote.useraccount.domain;

import java.io.Serializable;

public class ControllerDTO implements Serializable
{

  /**
   * 
   */
  private static final long serialVersionUID = -7020136000150801129L;

  private String uuid;

  public String getUuid()
  {
    return uuid;
  }

  public void setUuid(String uuid)
  {
    this.uuid = uuid;
  }
  
  
}
