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
 * contains informations for generating ir trans commands
 * @author wbalcaen
 *
 */
public class IRTrans implements Serializable {

  private static final long serialVersionUID = 1L;

  private String ipAdress;
  private String udpPort;
  private String irLed;

  public IRTrans() {
  }

  public IRTrans(String ipAdress, String udpPort, String irLed) {
    super();
    this.ipAdress = ipAdress;
    this.udpPort = udpPort;
    this.irLed = irLed;
  }

  public String getIpAdress() {
    return ipAdress;
  }

  public void setIpAdress(String ipAdress) {
    this.ipAdress = ipAdress;
  }

  public String getUdpPort() {
    return udpPort;
  }

  public void setUdpPort(String udpPort) {
    this.udpPort = udpPort;
  }

  public String getIrLed() {
    return irLed;
  }

  public void setIrLed(String irLed) {
    this.irLed = irLed;
  }

}
