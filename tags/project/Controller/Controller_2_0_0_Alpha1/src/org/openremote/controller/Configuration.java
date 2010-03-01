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
   private boolean copyLircdconf;
   
   /** The webapp port. */
   private int webappPort;
   
   /** The multicast address. */
   private String multicastAddress;
   
   /** The multicast port. */
   private int multicastPort;
   
   /** The resource path. */
   private String resourcePath;
   
   /** The resource upload switch. */
   private boolean resourceUpload;
   
   private long macroIRExecutionDelay = 500;
   
   private String webappIp;
   
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
    * Checks if is copy lircdconf.
    * 
    * @return true, if is copy lircdconf
    */
   public boolean isCopyLircdconf() {
      return copyLircdconf;
   }

   /**
    * Sets the copy lircdconf.
    * 
    * @param copyLircdconf the new copy lircdconf
    */
   public void setCopyLircdconf(boolean copyLircdconf) {
      this.copyLircdconf = copyLircdconf;
   }

   /**
    * Gets the webapp port.
    * 
    * @return the webapp port
    */
   public int getWebappPort() {
      return webappPort;
   }

   /**
    * Sets the webapp port.
    * 
    * @param webappPort the new webapp port
    */
   public void setWebappPort(int webappPort) {
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

   /**
    * Gets the resource path.
    * 
    * @return the resource path
    */
   public String getResourcePath() {
      return resourcePath;
   }

   /**
    * Sets the resource path.
    * 
    * @param resourcePath the new resource path
    */
   public void setResourcePath(String resourcePath) {
      this.resourcePath = resourcePath;
   }

   /**
    * Checks if is resource upload.
    * 
    * @return true, if is resource upload
    */
   public boolean isResourceUpload() {
      return resourceUpload;
   }

   /**
    * Sets the resource upload.
    * 
    * @param resourceUpload the new resource upload
    */
   public void setResourceUpload(boolean resourceUpload) {
      this.resourceUpload = resourceUpload;
   }

   public long getMacroIRExecutionDelay() {
      return macroIRExecutionDelay;
   }

   public void setMacroIRExecutionDelay(long macroIRExecutionDelay) {
      this.macroIRExecutionDelay = macroIRExecutionDelay;
   }

   public String getWebappIp() {      
      return webappIp;
   }

   public void setWebappIp(String webappIp) {
      this.webappIp = webappIp;
   }
   
}
