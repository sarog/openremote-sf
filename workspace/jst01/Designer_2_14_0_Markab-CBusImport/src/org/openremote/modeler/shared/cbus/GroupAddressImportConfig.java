/* OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2012, OpenRemote Inc.
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
package org.openremote.modeler.shared.cbus;

import java.io.Serializable;

/**
 * Represents a single CBus import record to be imported and turned into commands, switches, sliders and sensors
 * @author Jamie Turner
 *
 */
@SuppressWarnings("serial")
public class GroupAddressImportConfig implements Serializable 
{
    private String networkName;
    private String applicationName;
    private String groupAddressName;
    private String groupAddress;
    private boolean dimmable;
    private boolean switchCompatible;

    public GroupAddressImportConfig() 
    {
    }

    public GroupAddressImportConfig(String networkName, String applicationName,
	    String groupAddressName, String groupAddress, boolean canDim, boolean switchCompatible)
    {
	this();
	this.networkName = networkName;
	this.applicationName = applicationName;
	this.groupAddressName = groupAddressName;
	this.groupAddress = groupAddress;
	this.dimmable = canDim;
	this.switchCompatible = switchCompatible;
    }
    
    public GroupAddressImportConfig(String networkName, String applicationName,
	    String groupAddressName, String groupAddress)
    {
	this(networkName, applicationName, groupAddressName, groupAddress, false, false);
    }

    public String getNetworkName()
    {
	return networkName;
    }

    public void setNetworkName(String networkName)
    {
	this.networkName = networkName;
    }

    public String getApplicationName()
    {
	return applicationName;
    }

    public void setApplicationName(String applicationName)
    {
	this.applicationName = applicationName;
    }

    public String getGroupAddressName()
    {
	return groupAddressName;
    }

    public void setGroupAddressName(String groupAddressName)
    {
	this.groupAddressName = groupAddressName;
    }

    public String getGroupAddress()
    {
	return groupAddress;
    }

    public void setGroupAddress(String groupAddress)
    {
	this.groupAddress = groupAddress;
    }

    public boolean isDimmable()
    {
        return dimmable;
    }

    public void setDimmable(boolean canDim)
    {
        this.dimmable = canDim;
    }

    public boolean isSwitchCompatible()
    {
        return switchCompatible;
    }

    public void setSwitchCompatible(boolean switchCompatible)
    {
        this.switchCompatible = switchCompatible;
    }
    

}
