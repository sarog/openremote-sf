/*
 * OpenRemote, the Home of the Digital Home. Copyright 2008-2013, OpenRemote Inc.
 * 
 * See the contributors.txt file in the distribution for a full listing of individual contributors.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package org.openremote.modeler.server.cbus.importmodel;

import java.util.HashSet;
import java.util.Set;

/**
 * A CBus Network eg. Local Network
 * multiple applications run on a network
 *  
 * @author Jamie Turner
 */
public class Network 
{

    /** network name */
    private String name;
    
    /** the list of applications running on this network*/
    private Set<Application> applications;

    public Network(String name) 
    {
	super();
	this.name = name;
	this.applications = new HashSet<Application>();
    }

    public String getName() 
    {
	return name;
    }

    public Set<Application> getApplications() 
    {
	return applications;
    }

    public void addApplication(Application application) 
    {
	applications.add(application);
    }

}
