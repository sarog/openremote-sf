/* OpenRemote, the Home of the Digital Home.
 * Copyright 2008, OpenRemote Inc.
 * 
 * See the contributors.txt file in the distribution for a
 * full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3.0 of
 * the License, or (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.openremote.controller.protocol.knx;

import org.openremote.controller.event.Stateful;

/**
 * The Class KNXStatusEvent.
 * 
 * This class can hold connectionManager, groupAddress with extending KNXEvent,
 * and also can query status for specified groupAddress with connectionManager and implementing stateful interface.
 */
public class KNXStatusEvent extends KNXEvent implements Stateful {
    
    /**
     * Instantiates a new kNX status event.
     * 
     * @param connectionManager the connection manager
     * @param groupAddress the group address
     */
    public KNXStatusEvent(KNXConnectionManager connectionManager, String groupAddress) {
       this.connectionManager = connectionManager;
       this.groupAddress = groupAddress;
    }

    /*
     * (non-Javadoc)
     * @see org.openremote.controller.protocol.knx.KNXEvent#exec()
     */
    @Override
    public String queryStatus() {
        String rst = "";
        try {
            KNXConnection connection = connectionManager.getConnection();
            String groupAddress = "1.0.0";//TODO: remove it.
            String dptTypeID = getDataPointTypeID();
            rst = connection.readDeviceStatus(groupAddress, dptTypeID);
        } catch (Exception e) {
            log.error("Occured exception when excuting knxStatusEvent", e);
        }
        return rst;
    }

    /**
     * Gets the DataPointType id.
     * 
     * @return the data point type id
     */
    private String getDataPointTypeID() {
        return "1.001"; //Switch DTP ID
    }
}
