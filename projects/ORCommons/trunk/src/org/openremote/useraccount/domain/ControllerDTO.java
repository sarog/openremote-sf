package org.openremote.useraccount.domain;

import java.io.Serializable;
import java.util.List;

public class ControllerDTO implements Serializable
{

  /**
   * 
   */
  private static final long serialVersionUID = -7020136000150801129L;

  /** The oid */
  private Long oid;
  
  /** The mac address */
  private String macAddress;

  /** The account */
  private AccountDTO account;
  
  /** The linked flag */
  private boolean linked;
  
  public ControllerDTO()
  {
  }

  public Long getOid()
  {
    return oid;
  }

  public void setOid(Long oid)
  {
    this.oid = oid;
  }

  public String getMacAddress()
  {
    return macAddress;
  }

  public void setMacAddress(String macAddress)
  {
    this.macAddress = macAddress;
  }

  public AccountDTO getAccount()
  {
    return account;
  }

  public void setAccount(AccountDTO account)
  {
    this.account = account;
  }

  public boolean isLinked()
  {
    return linked;
  }

  public void setLinked(boolean linked)
  {
    this.linked = linked;
  }
  
  
}
