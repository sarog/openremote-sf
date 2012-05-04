package org.openremote.devicediscovery.domain;

import java.io.Serializable;
import java.util.List;

public class DiscoveredDeviceDTO implements Serializable
{
  /**
   * 
   */
  private static final long serialVersionUID = -6004225908783010315L;

  /** The oid. */
  private long oid;
  
  /** The device name. */
  private String name;

  /** The device's model. */
  private String model;
  
  /** The OpenRemote protocol which is responsible to control this devices */
  private String protocol;

  /** The device type eg. Switch, Dimmer, Thermometer, TV, .... */
  private String type;
  
  /** Is this device already used in the Building Modeler */ 
  private Boolean used;

  /** The device attrs. */
  private List<DiscoveredDeviceAttrDTO> deviceAttrs;


  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public String getModel()
  {
    return model;
  }

  public void setModel(String model)
  {
    this.model = model;
  }

  public String getProtocol()
  {
    return protocol;
  }

  public void setProtocol(String protocol)
  {
    this.protocol = protocol;
  }

  public String getType()
  {
    return type;
  }

  public void setType(String type)
  {
    this.type = type;
  }

  /**
   * Gets the device attrs.
   * 
   * @return the device attrs
   */
  public List<DiscoveredDeviceAttrDTO> getDeviceAttrs()
  {
    return deviceAttrs;
  }

  /**
   * Sets the device attrs.
   * 
   * @param deviceAttrs
   *          the new device attrs
   */
  public void setDeviceAttrs(List<DiscoveredDeviceAttrDTO> deviceAttrs)
  {
    this.deviceAttrs = deviceAttrs;
  }


  public Boolean getUsed()
  {
    return used;
  }

  public void setUsed(Boolean used)
  {
    this.used = used;
  }

  
  public long getOid()
  {
    return oid;
  }

  public void setOid(long oid)
  {
    this.oid = oid;
  }

  @Override
  public int hashCode()
  {
    return (int) getOid();
  }

  @Override
  public boolean equals(Object obj)
  {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    DiscoveredDeviceDTO other = (DiscoveredDeviceDTO) obj;
    return other.getOid() == getOid();
  }
}
