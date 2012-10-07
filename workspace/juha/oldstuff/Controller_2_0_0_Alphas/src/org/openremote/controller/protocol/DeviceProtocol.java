/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2011, OpenRemote Inc.
 *
 * See the contributors.txt file in the distribution for a
 * full listing of individual contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.openremote.controller.protocol;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

import org.openremote.controller.command.CommandBuilder;
import org.openremote.controller.command.Command;
import org.openremote.controller.command.ExecutableCommand;
import org.openremote.controller.command.StatusCommand;
import org.openremote.controller.exception.CommandBuildException;
import org.openremote.controller.exception.ProtocolException;
import org.openremote.controller.component.EnumSensorType;
import org.openremote.controller.component.Sensor;
import org.jdom.Element;

/**
 * TODO
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public abstract class DeviceProtocol implements CommandBuilder, ExecutableCommand, StatusCommand
{

  CommandProperties properties = new CommandProperties();


  private int pollingInterval = 0;

  public void setPollingInterval(int millis)
  {
    if (millis < 0)
      this.pollingInterval = 0;

    this.pollingInterval = millis;
  }


  @Override public Command build(Element element)
  {
    List<Element> propertyElements = element.getChildren(CommandBuilder.XML_ELEMENT_PROPERTY,
                                                         element.getNamespace());

    for (Element el : propertyElements)
    {
      String propertyName = el.getAttributeValue(CommandBuilder.XML_ATTRIBUTENAME_NAME);
      String propertyValue = el.getAttributeValue(CommandBuilder.XML_ATTRIBUTENAME_VALUE);


      properties.add(propertyName, propertyValue);
    }

    try
    {
      return createCommand(properties);
    }

    catch (ProtocolException e)
    {
      throw new CommandBuildException(e.getMessage(), e);
    }
  }


  private String lastKnownState = null;


  @Override public String read(EnumSensorType sensorType, Map<String, String> sensorProperties)
  {
    if (!shouldPollDevice())
      return lastKnownState;


    switch (sensorType)
    {
      case SWITCH:

        boolean b = readSwitch();

        lastKnownState = b ? "on" : "off";

        return lastKnownState;

      case LEVEL:

        int level = readLevel();

        level = enforceLevelLimits(level);

        lastKnownState = Integer.toString(level);

        return lastKnownState;

      case RANGE:

        int rangeMinBoundary = resolveRangeMinimum(sensorProperties);
        int rangeMaxBoundary = resolveRangeMaximum(sensorProperties);

        int range = readRange(rangeMinBoundary, rangeMaxBoundary);

        range = enforceRangeBoundaries(range, rangeMinBoundary, rangeMaxBoundary);

        lastKnownState = Integer.toString(range);

        return lastKnownState;
      
      case CUSTOM:


      default:

        // TODO

        throw new Error();
    }
  }


  protected boolean readSwitch()
  {
    return false;
  }

  protected int readLevel()
  {
    return 0;
  }

  protected int readRange(int rangeMin, int rangeMax)
  {
    return rangeMin;
  }



  private long lastPollTimeStamp = -1;

  private boolean shouldPollDevice()
  {
    long currentTime = System.currentTimeMillis();

    if (currentTime - lastPollTimeStamp > pollingInterval)
    {
      lastPollTimeStamp = currentTime;

      return true;
    }

    return false;
  }

  private int enforceLevelLimits(int level)
  {
    if (level > 100)
      return 100;

    if (level < 0)
      return 0;

    return level;
  }

  private int resolveRangeMinimum(Map<String, String> sensorProperties)
  {
    String rangeMin = sensorProperties.get(Sensor.RANGE_MIN_STATE);

    if (rangeMin == null)
      return Integer.MIN_VALUE;

    return Integer.parseInt(rangeMin);
  }

  private int resolveRangeMaximum(Map<String, String> sensorProperties)
  {
    String rangeMax = sensorProperties.get(Sensor.RANGE_MAX_STATE);

    if (rangeMax == null)
    {
      return Integer.MAX_VALUE;
    }

    return Integer.parseInt(rangeMax);
  }

  protected abstract Command createCommand(CommandProperties properties) throws ProtocolException;



}

