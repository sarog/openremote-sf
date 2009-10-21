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
package org.openremote.controller;

/**
 * TODO
 * 
 * @author Dan 2009-6-1
 * @author Jerome Velociter
 * @author <a href="mailto:juha@openremote.org>Juha Lindfors</a>
 */
public class Configuration {
   
   private String irsendPath;
   
   private String lircdconfPath;
   
   /** Whether copy lircd.conf for user. */
   private boolean copyLircdconf;
   
   private String webappPort;
   
   private String multicastAddress;
   
   private int multicastPort;
   
   private String resourcePath;
   
   /** The resource upload switch. */
   private boolean resourceUpload;

   /** The COM (Serial) port the ORC should use (for example, to send X10 events)*/
   private String comPort;
   
   /** The transmitter to use for X10 */
   private String x10transmitter;



   public String getIrsendPath() {
      return irsendPath;
   }

   public void setIrsendPath(String irsendPath) {
      this.irsendPath = irsendPath;
   }

   public String getLircdconfPath() {
      return lircdconfPath;
   }

   public void setLircdconfPath(String lircdconfPath) {
      this.lircdconfPath = lircdconfPath;
   }

   public boolean isCopyLircdconf() {
      return copyLircdconf;
   }

   public void setCopyLircdconf(boolean copyLircdconf) {
      this.copyLircdconf = copyLircdconf;
   }

   public String getWebappPort() {
      return webappPort;
   }

   public void setWebappPort(String webappPort) {
      this.webappPort = webappPort;
   }

   public String getMulticastAddress() {
      return multicastAddress;
   }

   public void setMulticastAddress(String multicastAddress) {
      this.multicastAddress = multicastAddress;
   }

   public int getMulticastPort() {
      return multicastPort;
   }

   public void setMulticastPort(int multicastPort) {
      this.multicastPort = multicastPort;
   }

   public String getResourcePath() {
      return resourcePath;
   }

   public void setResourcePath(String resourcePath) {
      this.resourcePath = resourcePath;
   }

   public boolean isResourceUpload() {
      return resourceUpload;
   }

   public void setResourceUpload(boolean resourceUpload) {
      this.resourceUpload = resourceUpload;
   }


  /**
   * @return the COM (Serial) port
   */
  public String getComPort() {
    return comPort;
  }

  /**
   * Sets the COM (Serial) port
   *
   * @param comPort the value of the COM (Serial) port to set
   */
  public void setComPort(String comPort) {
    this.comPort = comPort;
  }

  public String getX10transmitter() {
    return x10transmitter;
  }

  public void setX10transmitter(String x10transmitter) {
    this.x10transmitter = x10transmitter;
  }

}
