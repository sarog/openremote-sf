/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2010, OpenRemote Inc.
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
package org.openremote.controller.protocol.mockcam;

import java.util.List;
import java.util.Map;

import org.openremote.controller.command.CommandBuilder;
import org.openremote.controller.command.Command;
import org.openremote.controller.command.StatusCommand;
import org.openremote.controller.command.ExecutableCommand;
import org.openremote.controller.component.EnumSensorType;
import org.jdom.Element;

/**
 * TODO
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class MockCamCommandBuilder implements CommandBuilder
{

  MockCamFrameEvent mockFrameEvent = new MockCamFrameEvent();

  public MockCamCommandBuilder()
  {
    Thread t = new Thread(mockFrameEvent);
    t.start();
  }

  public Command build(Element element)
  {
    // Get the list of properties from XML. XML_ELEMENT_PROPERTY is defined to
    // match the "property" element name in the XML snippet.

    List<Element> propertyElements =
        element.getChildren(CommandBuilder.XML_ELEMENT_PROPERTY, element.getNamespace());

    // Iterate through each command property...

    for (Element el : propertyElements)
    {
      // XML_ATTRIBUTENAME_NAME defines the "name" attribute of "property" element in
      // the XML snippet, i.e. <property name = "foo" ... />

      String propertyName = el.getAttributeValue(CommandBuilder.XML_ATTRIBUTENAME_NAME);

      // XML_ATTRIBUTENAME_VALUE defines the "value" attribute of "property" element
      // in the XML snippet, i.e. <property name = "foo" value = "bar"/>

      String propertyValue = el.getAttributeValue(CommandBuilder.XML_ATTRIBUTENAME_VALUE);

      // Store your command properties for configuring/initializing your Command instances...

      if (propertyName.equalsIgnoreCase("STATUS"))
      {
        return new ReadCommand(mockFrameEvent);
      }

      if (propertyName.equalsIgnoreCase("WRITE"))
      {
        return new WriteCommand();
      }
    }

    throw new Error("ERR: 1");    
  }

  long frameTimeSnapshot;
  long sensorTimeSnapshot;

  class ReadCommand implements StatusCommand
  {
    MockCamFrameEvent frameSource;

    ReadCommand(MockCamFrameEvent frameSource)
    {
      this.frameSource = frameSource;
    }

    public String read(EnumSensorType sensorType, Map<String, String> stateMap)
    {
      StringBuffer buffer = new StringBuffer(100);

      MockCamFrameEvent.Frame frame = frameSource.getCurrentFrame();

      frameTimeSnapshot = frame.timeSnapshot;
      sensorTimeSnapshot = System.currentTimeMillis();

      buffer.append("[testcase]\t");
//      buffer.append(frame.counter);
//      buffer.append("\t");
//      buffer.append(frame.timeSnapshot);

      return buffer.toString();
    }

  }

  static long[] pathTimeDelays = new long[5000];
  static int arrayCounter = 0;
  static int warmupCycles = 0;

  class WriteCommand implements ExecutableCommand
  {
    public void send()
    {

      if (warmupCycles < 10000)
      {
        warmupCycles++;
      }
      else
      {
        long myTimeSnapshot = System.currentTimeMillis();

        if (arrayCounter < 5000)
          pathTimeDelays[arrayCounter++] = myTimeSnapshot - sensorTimeSnapshot;
      }

      if (arrayCounter == 5000)
      {
        long sum = 0;

        for (long time : pathTimeDelays)
        {
          sum += time;

          System.out.println("Value " + time + " ms.");
        }

        System.out.println("****** Avg time from sensor to command " + (double)sum/5000);

        arrayCounter=5001;
      }

//      System.out.println("Last camera frame received " + (myTimeSnapshot - frameTimeSnapshot) + " ms ago.");
//      System.out.println("Sensor to command time " + (myTimeSnapshot - sensorTimeSnapshot) + "ms.");


    }
  }
}

