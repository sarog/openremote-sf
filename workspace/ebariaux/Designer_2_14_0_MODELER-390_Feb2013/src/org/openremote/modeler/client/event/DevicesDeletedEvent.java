/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2013, OpenRemote Inc.
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
package org.openremote.modeler.client.event;

import java.util.ArrayList;

import org.openremote.modeler.shared.dto.DeviceDTO;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event indicating that devices have been deleted.
 * Event usually contains the list of devices that have been deleted,
 * but by convention, the list is null, we consider that all devices have been deleted.
 * 
 * @author <a href="mailto:eric@openremote.org">Eric Bariaux</a>
 */
public class DevicesDeletedEvent extends GwtEvent<DevicesDeletedEventHandler> {

  public static Type<DevicesDeletedEventHandler> TYPE = new Type<DevicesDeletedEventHandler>();

  private final ArrayList<DeviceDTO> devices;

  public DevicesDeletedEvent() {
    super();
    this.devices = null;
  }

  public DevicesDeletedEvent(DeviceDTO device) {
    super();
    this.devices = new ArrayList<DeviceDTO>();
    this.devices.add(device);
  }
  
  public DevicesDeletedEvent(ArrayList<DeviceDTO> devices) {
    super();
    this.devices = devices;
  }

  public ArrayList<DeviceDTO> getDevices() {
    return devices;
  }

  @Override
  public com.google.gwt.event.shared.GwtEvent.Type<DevicesDeletedEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(DevicesDeletedEventHandler handler) {
    handler.onDevicesDeleted(this);
  }
  
}