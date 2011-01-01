/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2009, OpenRemote Inc.
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

package org.openremote.controller.protocol.knx;

import org.openremote.controller.command.StatusCommand;
import org.openremote.controller.component.control.Control;

/**
 * The Class KNXStatusEvent.
 * 
 * This class can hold connectionManager, groupAddress with extending KNXEvent,
 * and also can query status for specified groupAddress with connectionManager and implementing stateful interface.
 */
public class KNXStatusCommand extends KNXCommand implements StatusCommand {
    
    public KNXStatusCommand(){}
    
    public KNXStatusCommand(KNXConnectionManager connectionManager, String groupAddress) {
       super(connectionManager, groupAddress, null);
    }

    /*
     * (non-Javadoc)
     * @see org.openremote.controller.protocol.knx.KNXEvent#exec()
     */
    public String read() {
       /* String rst = "unknown";
        try {
            KNXConnection connection = getConnectionManager().getConnection();
            String groupAddress = "1.0.0";//TODO: remove it.
            String dptTypeID = getDataPointTypeID();
            rst = connection.readDeviceStatus(groupAddress, dptTypeID);
        } catch (Exception e) {
            log.error("Occured exception when excuting knxStatusEvent", e);
        }
        return rst;*/
       // The following are simulate.
       String currentStatus = Control.CURRENT_STATUS;
       if ("off".equalsIgnoreCase(Control.CURRENT_STATUS)) {
          Control.CURRENT_STATUS = "on";
       } else {
          Control.CURRENT_STATUS = "off";
       }
       return currentStatus;
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
