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
package org.openremote.modeler.server.cbus.importmodel;

import java.util.HashSet;
import java.util.Set;
/**
 * Represenets a CBus Application eg. lighting
 * An application contains multiple groups
 * 
 * @author Jamie Turner
 *
 */
public class Application 
{

  private String name;
  private Set<GroupAddress> addresses;
  
  public Application(String name) 
  {
    super();
    this.name = name;
    this.addresses = new HashSet<GroupAddress>();
  }

  public String getName()
  {
    return name;
  }
  
  public Set<GroupAddress> getGroupAddresses() 
  {
    return addresses;
  }

  public void addGroupAddress(GroupAddress address) 
  {
    addresses.add(address);
  }
  
}
