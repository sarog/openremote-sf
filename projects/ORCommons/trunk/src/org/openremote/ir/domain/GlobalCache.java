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
package org.openremote.ir.domain;

import java.io.Serializable;

/**
 * contains informations for generating global caché commands
 * 
 * @author wbalcaen
 * 
 */
public class GlobalCache implements Serializable {

  private static final long serialVersionUID = 1L;

  private String IpAddress;
  private String tcpPort;
  private String connector;

  public GlobalCache(String ipAddress, String tcpPort, String connector) {
    super();
    IpAddress = ipAddress;
    this.tcpPort = tcpPort;
    this.connector = connector;
  }

  public GlobalCache() {
  }

  public String getIpAddress() {
    return this.IpAddress;
  }

  public String getTcpPort() {
    return this.tcpPort;
  }

  public String getConnector() {
    return this.connector;
  }

  public void setIpAddress(String ipAddress) {
    IpAddress = ipAddress;
  }

  public void setTcpPort(String tcpPort) {
    this.tcpPort = tcpPort;
  }

  public void setConnector(String connector) {
    this.connector = connector;
  }

}
