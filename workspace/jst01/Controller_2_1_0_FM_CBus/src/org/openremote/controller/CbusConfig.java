/* 
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2013, OpenRemote Inc.
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

import org.openremote.controller.service.ServiceContext;

/**
 * Clipsal CBus is a lighting and automation protocol
 * 
 * OpenRemote accesses CBus by interacting with CGate, freely available as part of the CBus Toolkit application
 * The toolkit application is available from www.clipsal.com/cis
 * 
 * CGate needs to be running somewhere on the network for this protocol to work. Normally you can run it on the same server as the OpenRemote Controller
 * 
 * @author Jamie Turner
 *
 */
/**
 * @author Jamie
 *
 */
public class CbusConfig extends Configuration 
{
   /**
    * CGate Event port parameter name
    */
   public final static String EVENT_PORT = "cbus.event_port";
   
   /**
    * CGate Status port parameter name
    */
   public final static String STATUS_PORT = "cbus.status_port";
   
   /**
    * CGate Main port parameter name 
    */
   public final static String PORT = "cbus.port";
   
   /** Address of machine where CGate is running parameter name */
   public final static String ADDRESS = "cbus.ip_address";
   
   /** Location of CBUS project tag XML file */
   public final static String PROJECT_FILE = "cbus.project_file";
   
   /**
    * The CGate event port
    */
   private int eventPort;
   
   /**
    * The CGate status port
    */
   private int statusPort;
   
   /**
    * The IP address of the machine running CGate
    */
   private String address;
   
   /**
    * The main CGate port
    */
   private int port;
   
   /**
    * The CBus tag XML project file to use 
    */
   private String projectFile;
   
   
   /**
    * Read the configuration from the config files
    * 
    * @return A populated config object
    */
   public static CbusConfig readXML()
   {
      CbusConfig config = ServiceContext.getCBusConfiguration();
      return (CbusConfig)Configuration.updateWithControllerXMLConfiguration(config);
   }


   /**
    * @return event port
    */
   public int getEventPort() 
   {
      return preferAttrCustomValue(EVENT_PORT, eventPort);
   }

   
   /**
    * @param event port
    */
   public void setEventPort(int eventPort) 
   {
      this.eventPort = eventPort;
   }

  
   /**
    * @return status port
    */
   public int getStatusPort() 
   {
      return preferAttrCustomValue(STATUS_PORT, statusPort);
   }


   /**
    * @param status port
    */
   public void setStatusPort(int statusPort) 
   {
      this.statusPort = statusPort;
   }

 
   /**
    * @return address
    */
   public String getAddress() 
   {
      return preferAttrCustomValue(ADDRESS, address);
   }


   /**
    * @param address
    */
   public void setAddress(String address) 
   {
      this.address = address;
   }


   /**
    * @return port
    */
   public int getPort() 
   {
      return preferAttrCustomValue(PORT, port);
   }


   /**
    * @param port
    */
   public void setPort(int port) 
   {
      this.port = port;
   }

   /**
    * 
    * @return project file
    */
   public String getProjectFile() 
   {
      return preferAttrCustomValue(PROJECT_FILE, projectFile);
   }

   /**
    * 
    * @param projectFile
    */
   public void setProjectFile(String projectFile)
   {
      this.projectFile = projectFile;
   }
   
   
}
