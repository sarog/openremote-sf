package org.openremote.controller.protocol.enocean.datatype;

public class ScaleCategory
{
  /**
   * TODO
   */
  private String name;

  /**
   * TODO
   */
  private int minValue;

  /**
   * TODO
   */
  private int maxValue;

  /**
   * TODO
   */
  private String sensorStringValue;

  /**
   * TODO
   */
  private int sensorValue;

  /**
   * TODO
   *
   * @param name
   * @param minValue
   * @param maxValue
   */
  public ScaleCategory(String name, int minValue, int maxValue, String sensorStringValue, int sensorValue)
  {
    this.name = name;
    this.minValue = minValue;
    this.maxValue = maxValue;
    this.sensorStringValue = sensorStringValue;
    this.sensorValue = sensorValue;
  }

  /**
   * TODO
   *
   * @param value
   * @return
   */
  public boolean isInCategory(int value)
  {
    if(value >= minValue && value <= maxValue)
    {
      return true;
    }
    else
    {
      return false;
    }
  }

  public String getName()
  {
    return name;
  }

  public String getSensorStringValue()
  {
    return sensorStringValue;
  }

  public int getSensorValue()
  {
    return sensorValue;
  }
}
