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
 * Basic Configuration.
 * 
 * @author Dan 2009-6-1
 */
public class Configuration extends CustomConfiguration {
   
   /* the following constants are the keys from config.properties */
   public static final String RESOURCE_UPLOAD_ENABLE = "resource.upload.enable";
   public static final String RESOURCE_PATH = "resource.path";
   public static final String IRSEND_PATH = "irsend.path";
   public static final String MULTICAST_PORT = "multicast.port";
   public static final String MULTICAST_ADDRESS = "multicast.address";
   public static final String WEBAPP_PORT = "webapp.port";
   public static final String COPY_LIRCD_CONF_ON = "copy.lircd.conf.on";
   public static final String LIRCD_CONF_PATH = "lircd.conf.path";
   public static final String WEBAPP_IP = "webapp.ip";
   public static final String BEEHIVE_REST_ROOT_URL = "beehive.REST.Root.Url";
   public static final String CONTROLLER_APPLICATIONNAME = "controller.applicationname";
   
   /** The irsend path. */
   private String irsendPath;
   
   /** The lircd.conf path. */
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
   
   private String beehiveRESTRootUrl;
   
   private String webappName;
   
   public static Configuration parseFromControllerXML() {
      
      return null;
   }
   
   /* attributes getter/setter begin */
   
   /**
    * Gets the irsend cmd path.
    * 
    * @return the irsend path
    */
   public String getIrsendPath() {
      return preferAttrCustomValue(IRSEND_PATH, irsendPath);
   }

   public void setIrsendPath(String irsendPath) {
      this.irsendPath = irsendPath;
   }

   /**
    * Gets the lircd.conf path.
    * 
    * @return the lircd.conf path
    */
   public String getLircdconfPath() {
      return preferAttrCustomValue(LIRCD_CONF_PATH, lircdconfPath);
   }

   public void setLircdconfPath(String lircdconfPath) {
      this.lircdconfPath = lircdconfPath;
   }

   /**
    * Checks if is copy lircdconf.
    * 
    * @return true, if is copy lircdconf
    */
   public boolean isCopyLircdconf() {
      return preferAttrCustomValue(COPY_LIRCD_CONF_ON, copyLircdconf);
   }

   /**
    * Sets whether copying lircdconf is enabled.
    * 
    * @param copyLircdconf the new copy lircdconf
    */
   public void setCopyLircdconf(boolean copyLircdconf) {
      this.copyLircdconf = copyLircdconf;
   }

   public int getWebappPort() {
      return preferAttrCustomValue(WEBAPP_PORT, webappPort);
   }

   public void setWebappPort(int webappPort) {
      this.webappPort = webappPort;
   }

   public String getMulticastAddress() {
      return preferAttrCustomValue(MULTICAST_ADDRESS, multicastAddress);
   }

   public void setMulticastAddress(String multicastAddress) {
      this.multicastAddress = multicastAddress;
   }

   public int getMulticastPort() {
      return preferAttrCustomValue(MULTICAST_PORT, multicastPort);
   }

   public void setMulticastPort(int multicastPort) {
      this.multicastPort = multicastPort;
   }

   public String getResourcePath() {
//      return preferAttrCustomValue(RESOURCE_PATH, resourcePath);
      return resourcePath;
   }

   public void setResourcePath(String resourcePath) {
      this.resourcePath = resourcePath;
   }

   /**
    * Checks if is resource upload is enabled.
    * 
    * @return true, if is resource upload is enabled.
    */
   public boolean isResourceUpload() {
      return preferAttrCustomValue(RESOURCE_UPLOAD_ENABLE, resourceUpload);
   }

   public void setResourceUpload(boolean resourceUpload) {
      this.resourceUpload = resourceUpload;
   }

   public long getMacroIRExecutionDelay() {
      return preferAttrCustomValue("Macro.IR.Execution.Delay", macroIRExecutionDelay);
   }

   public void setMacroIRExecutionDelay(long macroIRExecutionDelay) {
      this.macroIRExecutionDelay = macroIRExecutionDelay;
   }

   public String getWebappIp() {      
      return preferAttrCustomValue(WEBAPP_IP, webappIp);
   }

   public void setWebappIp(String webappIp) {
      this.webappIp = webappIp;
   }

   public String getBeehiveRESTRootUrl() {
      return preferAttrCustomValue(BEEHIVE_REST_ROOT_URL, beehiveRESTRootUrl);
   }

   public void setBeehiveRESTRootUrl(String beehiveRESTRootUrl) {
      this.beehiveRESTRootUrl = beehiveRESTRootUrl;
   }
   
   public String getWebappName() {
      return preferAttrCustomValue(CONTROLLER_APPLICATIONNAME, webappName);
   }
   
   public void setWebappName(String webappName) {
      this.webappName = webappName;
   }
   
}
