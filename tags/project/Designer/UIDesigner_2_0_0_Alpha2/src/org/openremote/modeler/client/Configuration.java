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

package org.openremote.modeler.client;

/**
 * The Class Configuration.
 */
public class Configuration {

   /** The beehive rest root url. */
   private String beehiveRESTRootUrl;

   /** The beehive lircd.conf rest url. */
   private String beehiveLircdConfRESTUrl;

   /** The beehive rest icon url. */
   private String beehiveRestIconUrl;
   
   /** The iphone xsd path. */
   private String panelXsdPath;

   /** The controller xsd path. */
   private String controllerXsdPath;

   /** The webapp server root. */
   private String webappServerRoot;
   
   private String controllerConfigPath;

   /**
    * Gets the iphone xsd path.
    * 
    * @return the iphone xsd path
    */
   public String getPanelXsdPath() {
      return panelXsdPath;
   }

   /**
    * Gets the controller xsd path.
    * 
    * @return the controller xsd path
    */
   public String getControllerXsdPath() {
      return controllerXsdPath;
   }

   /**
    * Gets the beehive rest url.
    * 
    * @param iphoneXsdPath the iphone xsd path
    * 
    * @return the beehive rest url
    */

   /**
    * Sets the iphone xsd path.
    * 
    * @param iphoneXsdPath
    *           the new iphone xsd path
    */
   public void setPanelXsdPath(String panelXsdPath) {
      this.panelXsdPath = panelXsdPath;
   }

   /**
    * Sets the controller xsd path.
    * 
    * @param controllerXsdPath the new controller xsd path
    */
   public void setControllerXsdPath(String controllerXsdPath) {
      this.controllerXsdPath = controllerXsdPath;
   }

   /**
    * Gets the webapp server root.
    * 
    * @return the webapp server root
    */
   public String getWebappServerRoot() {
      return webappServerRoot;
   }

   /**
    * Sets the webapp server root.
    * 
    * @param webappServerRoot the new webapp server root
    */
   public void setWebappServerRoot(String webappServerRoot) {
      this.webappServerRoot = webappServerRoot;
   }

   /**
    * Gets the beehive rest root url.
    * 
    * @return the beehive rest root url
    */
   public String getBeehiveRESTRootUrl() {
      return beehiveRESTRootUrl;
   }

   /**
    * Sets the beehive rest root url.
    * 
    * @param beehiveRESTRootUrl the new beehive rest root url
    */
   public void setBeehiveRESTRootUrl(String beehiveRESTRootUrl) {
      this.beehiveRESTRootUrl = beehiveRESTRootUrl;
   }

   /**
    * Gets the beehive lircd conf rest url.
    * 
    * @return the beehive lircd conf rest url
    */
   public String getBeehiveLircdConfRESTUrl() {
      return beehiveLircdConfRESTUrl;
   }

   /**
    * Sets the beehive lircd conf rest url.
    * 
    * @param beehiveLircdConfRESTUrl the new beehive lircd conf rest url
    */
   public void setBeehiveLircdConfRESTUrl(String beehiveLircdConfRESTUrl) {
      this.beehiveLircdConfRESTUrl = beehiveLircdConfRESTUrl;
   }

   /**
    * Gets the beehive rest icon url.
    * 
    * @return the beehive rest icon url
    */
   public String getBeehiveRestIconUrl() {
      return beehiveRestIconUrl;
   }

   /**
    * Sets the beehive rest icon url.
    * 
    * @param beehiveRestIconUrl the new beehive rest icon url
    */
   public void setBeehiveRestIconUrl(String beehiveRestIconUrl) {
      this.beehiveRestIconUrl = beehiveRestIconUrl;
   }

   public String getControllerConfigPath() {
      return controllerConfigPath;
   }

   public void setControllerConfigPath(String controllerConfigPath) {
      this.controllerConfigPath = controllerConfigPath;
   }
   
   
}
