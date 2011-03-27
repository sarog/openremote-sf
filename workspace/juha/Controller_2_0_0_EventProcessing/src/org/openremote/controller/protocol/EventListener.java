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


/**
 * Event listeners are intended for collecting events from "active" devices which broadcast
 * them over various transport mechanisms -- examples include bus installations such as
 * KNX bus or IP-network broadcasts.  <p>
 *
 * An event listener can be used with the same 'sensor' abstraction (seen for example in
 * the controller's <tt>controller.xml</tt) configuration file) as the
 * {@link org.openremote.controller.command.StatusCommand status command}. Both
 * <tt>EventListener</tt> and <tt>StatusCommand</tt> are treated as
 * {@link org.openremote.controller.protocol.EventProducer event producers}. <p>
 *
 * Creating an event listener may occur via the common
 * {@link org.openremote.controller.command.CommandBuilder} interface.  <p>
 *
 * The <tt>EventListener</tt> implementations are disconnected from the sensor polling
 * threads used with (polling) read commands (a.k.a. <tt>StatusCommand</tt>). No active threads
 * are associated with an <tt>EventListener</tt> instance on behalf of the controller
 * framework. Instead, <tt>EventListener</tt> instances are expected to create their own threads
 * which implement the listening functionality and also directly push received events to the
 * (global) state cache of the controller using the callback API provided.  <p>
 *
 * The <tt>EventListener</tt> implementations can use two APIs to interact with the controller's
 * device state cache: a callback interface is provided by the controller at initialization time
 * which provides sensor identifier(s) this event listener is bound to. Events themselves can
 * be pushed to state cache using the state cache's
 * {@link org.openremote.controller.statuscache.StatusCache#update} method which is available
 * via {@link org.openremote.controller.service.ServiceContext#getDeviceStateCache()} method,
 * as shown in the example below:
 *
 * <pre>{@code
 *
 *  public class BusListener implements EventListener, Runnable
 *  {
 *    // This implementation assumes a listener instance per sensor, so only deals with a
 *    // single sensor ID value...
 *
 *    private int sensorID;
 *
 *    @Override public void setID(int ID)
 *    {
 *      this.sensorID = ID;
 *
 *      // Initialize the sensor with a default value...
 *
 *      ServiceContext.getDeviceStateCache().update(sensorID, "0");
 *
 *      // This implementation starts a listening thread per sensor. If you want multiple
 *      // listeners / sensors to share same resources or threads, this can be managed in
 *      // the command builder implementation...
 *
 *      Thread t = new Thread(this);
 *      t.start();
 *    }
 *
 *    @Override public void run()
 *    {
 *      String sensorValue = implementYourListenerLogic()
 *
 *      ServiceContext.getDeviceStateCache().update(sensorID, sensorValue);
 *    }
 *  }
 *
 * }</pre>
 *
 *
 *
 * <b>NOTE:</b> The controller at this point does not implement any flow control on its side --
 *              therefore a very busy listener can overflow it with too many events. Control
 *              flow must be implemented co-operatively by the listener implementation ensuring
 *              that not too many events are created.
 *
 * @see EventProducer
 * @see org.openremote.controller.command.StatusCommand
 * @see org.openremote.controller.service.ServiceContext
 * @see org.openremote.controller.statuscache.StatusCache
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public interface EventListener extends EventProducer
{

  // TODO :
  //   - the API can (and eventually should) be improved for use case where a listener is bound to
  //     multiple sensors by giving a single callback ID for listener implementor to deal with,
  //     and mapping that to multiple sensor ID's on controller framework side so the implementer
  //     does not need to deal with updating multiple sensors explicitly.


  // TODO : enforce state values in statuscache update
  // TODO : enable sensor properties in event listener



  /**
   * Each event listener is initialized with one or more sensor IDs the listener is bound to.
   * If the listener is used as an input for multiple sensors, this callback is invoked multiple
   * times, once for each associated sensor ID.
   * 
   * @param sensorID    sensor this event listener is bound to
   */
  public void setSensorID(int sensorID);

}
