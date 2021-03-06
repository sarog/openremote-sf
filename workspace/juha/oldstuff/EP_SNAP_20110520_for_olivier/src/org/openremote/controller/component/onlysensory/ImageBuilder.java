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
package org.openremote.controller.component.onlysensory;

import java.util.List;

import org.jdom.Element;
import org.openremote.controller.component.Component;
import org.openremote.controller.component.ComponentBuilder;
import org.openremote.controller.model.sensor.Sensor;
import org.openremote.controller.model.xml.SensorBuilder;
import org.openremote.controller.exception.InitializationException;
import org.openremote.controller.utils.Logger;
import org.openremote.controller.Constants;
import org.openremote.controller.service.ServiceContext;

/**
 * TODO : This class is used to build a Image by parse controll.xml
 * 
 * @author Handy
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class ImageBuilder extends ComponentBuilder
{

  // Class Members --------------------------------------------------------------------------------

  /**
   * Common log category for all XML parsing related issues.
   */
  private final static Logger log = Logger.getLogger(Constants.XML_PARSER_LOG_CATEGORY);



  // Implements ComponentBuilder ------------------------------------------------------------------

  @Override public Component build(Element componentElement, String commandParam)
  {
    Image image = new Image();

    if (!image.isValidActionWith(commandParam))
    {
      return image;
    }

    List<Element> operationElements = componentElement.getChildren();

    for (Element operationElement : operationElements)
    {
      if (hasIncludeSensorElement(operationElement))
      {
        try
        {
          //Sensor sensor = parseSensor(componentElement, operationElement);
          SensorBuilder builder = (SensorBuilder) ServiceContext.getXMLBinding("sensor");
          Sensor sensor = builder.buildFromComponentInclude(operationElement);
          
          image.setSensor(sensor);
        }
        catch (InitializationException e)
        {
          log.error(
              "Unable to initialize a sensor for an image component. Some image components on " +
              "panels will not update correctly. Error message: {0}", e.getMessage()
          );
        }
      }
    }

    return image;
  }

}
