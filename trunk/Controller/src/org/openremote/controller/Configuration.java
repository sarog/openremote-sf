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
package org.openremote.controller;

/**
 * The Class Configuration.
 * 
 * @author Dan 2009-6-1
 */
public class Configuration {
   
   /** The irsend path. */
   private String irsendPath;
   
   /** The lircdconf path. */
   private String lircdconfPath;
   
   /** Whether copy lircd.conf for user. */
   private String copyLircdconf;
   
   /** The webapp port. */
   private String webappPort;
   
   /** The multicast address. */
   private String multicastAddress;
   
   /** The multicast port. */
   private int multicastPort;
   
   /** The resource path. */
   private String resourcePath;

   /**
    * Gets the irsend path.
    * 
    * @return the irsend path
    */
   public String getIrsendPath() {
      return irsendPath;
   }

   /**
    * Sets the irsend path.
    * 
    * @param irsendPath the new irsend path
    */
   public void setIrsendPath(String irsendPath) {
      this.irsendPath = irsendPath;
   }

   /**
    * Gets the lircdconf path.
    * 
    * @return the lircdconf path
    */
   public String getLircdconfPath() {
      return lircdconfPath;
   }

   /**
    * Sets the lircdconf path.
    * 
    * @param lircdconfPath the new lircdconf path
    */
   public void setLircdconfPath(String lircdconfPath) {
      this.lircdconfPath = lircdconfPath;
   }

   /**
    * Gets the copy lircdconf.
    * 
    * @return the copy lircdconf
    */
   public String getCopyLircdconf() {
      return copyLircdconf;
   }

   /**
    * Sets the copy lircdconf.
    * 
    * @param copyLircdconf the new copy lircdconf
    */
   public void setCopyLircdconf(String copyLircdconf) {
      this.copyLircdconf = copyLircdconf;
   }

   /**
    * Gets the webapp port.
    * 
    * @return the webapp port
    */
   public String getWebappPort() {
      return webappPort;
   }

   /**
    * Sets the webapp port.
    * 
    * @param webappPort the new webapp port
    */
   public void setWebappPort(String webappPort) {
      this.webappPort = webappPort;
   }

   /**
    * Gets the multicast address.
    * 
    * @return the multicast address
    */
   public String getMulticastAddress() {
      return multicastAddress;
   }

   /**
    * Sets the multicast address.
    * 
    * @param multicastAddress the new multicast address
    */
   public void setMulticastAddress(String multicastAddress) {
      this.multicastAddress = multicastAddress;
   }

   /**
    * Gets the multicast port.
    * 
    * @return the multicast port
    */
   public int getMulticastPort() {
      return multicastPort;
   }

   /**
    * Sets the multicast port.
    * 
    * @param multicastPort the new multicast port
    */
   public void setMulticastPort(int multicastPort) {
      this.multicastPort = multicastPort;
   }

   public String getResourcePath() {
      return resourcePath;
   }

   public void setResourcePath(String resourcePath) {
      this.resourcePath = resourcePath;
   }
   
}
