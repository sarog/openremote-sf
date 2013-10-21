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
package org.openremote.controller.protocol.cbus;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;
import org.openremote.controller.utils.Logger;

/**
 * CBusProjectList reads the full configuration of the installed CBus system including the projects, networks and so on
 * 
 * The config file needs to be set up before using the system
 * 
 * @author Jamie Turner
 *
 */
public class CBusProject 
{
   /**
    * The logger
    */
   private final static Logger log = Logger.getLogger(CBusCommandBuilder.CBUS_LOG_CATEGORY);

   
   /**
    * Singleton instance of the project list
    */
   private static CBusProject instance = new CBusProject();
   
   /**
    * Has the singleton instance been fully initialised?
    */
   private volatile boolean instanceBuilt = false;
   
   /**
    * Project address
    */
   private String projectAddress;
  
   /**
    * The default network to use (usually Local Network)
    */
   private String defaultNetwork;
   
   /**
    * A list of all the networks by address
    */
   private Map<String, CBusNetwork> networks;
   
   private BiDirectionalMap<String, String> networksLookup;
      
   /**
    * Initialise the singleton project object from the XML config file
    *    
    * @param filename
    *           filename of the project XML file
    *           
    * @throws CBusException
    *           An error occurred loading the project XML file
    *           
    */
   public static void loadProjectFromXMLFile(String filename) throws CBusException
   {
      if(instance != null)
      {
         synchronized(instance)
         {
            if(!instance.instanceBuilt)
            {
               InputStream is = null;
               String xmlData = null;
               try
               {
                  is = new BufferedInputStream(new FileInputStream(filename));
                  xmlData = convertStreamToString(is);
               }
               catch(IOException ioe)
               {
                  throw new CBusException("Error occurred reading XML project file: " + ioe.toString(), ioe);
               }
               finally
               {
                  if(is != null)
                  {
                     try
                     {
                        is.close();
                     }
                     catch(IOException ioe)
                     {                        
                     }
                  }
                     
               }

               SAXBuilder builder = new SAXBuilder();
               Document document = null;

               if (xmlData != null)
               {
                  //Remove UTF-8 Byte-order mark from the beginning of the data
                  xmlData = xmlData.trim().replaceFirst("^([\\W]+)<","<");

                  // parse the XML as a W3C Document
                  StringReader in = new StringReader(xmlData);
                  try
                  {
                     document = builder.build(in);

                     //get the project name and address first
                     XPath projectPath = XPath.newInstance("//Installation/Project");
                     Element projectElement = (Element) projectPath.selectSingleNode(document);
                     String projectAddress = projectElement.getChildText("Address");

                     instance.projectAddress = projectAddress;

                     log.debug(new StringBuilder("CBUS Project address: ").append(projectAddress).toString());

                     //get the network address
                     List<Element> networkResult = projectElement.getChildren("Network");
                     for (Element networkElement : networkResult)
                     {
                        String networkAddress = networkElement.getChildText("Address");
                        log.debug(new StringBuilder("CBUS Network address: ").append(networkAddress).toString());

                        if(networkAddress != null)
                        {
                           CBusNetwork network = new CBusNetwork();
                           network.setAddress(networkAddress);
                           network.setName(networkElement.getChildText("TagName"));

                           if(instance.networksLookup == null)
                              instance.networksLookup = new BiDirectionalMap<String, String>();

                           instance.networksLookup.put(network.getAddress(), network.getName());

                           // Query all GroupAddress elements for the network
                           List<Element> appResult = networkElement.getChildren("Application");
                           for (Element appElement : appResult)
                           {
                              String applicationName = appElement.getChildText("TagName");
                              String applicationAddress = appElement.getChildText("Address");

                              if(applicationAddress != null)
                              {
                                 CBusApplication app = new CBusApplication();
                                 app.setAddress(applicationAddress);
                                 app.setApplicationType(applicationName);

                                 List<Element> groupElements = appElement.getChildren("Group");
                                 for (Element grpElement : groupElements)
                                 {
                                    String groupName = grpElement.getChildText("TagName");
                                    String groupAddress = grpElement.getChildText("Address");

                                    if(groupAddress != null)
                                    {
                                       app.add(groupAddress, groupName);
                                       log.debug(new StringBuilder("Created CBUS Group: ").append(groupName).append(" addr: ").append(groupAddress).toString());          

                                    }
                                 }

                                 network.add(app);
                              }
                           }

                           if(instance.networks == null)
                              instance.networks = new HashMap<String, CBusNetwork>();
                           
                           instance.networks.put(networkAddress, network);
                           
                           instance.instanceBuilt = true;
                        }

                        else
                           log.error("*CBUS ERROR* Null XML data found in the project XML file.");
                     }
                  }
                  catch (Exception ex)
                  {
                     log.error("*CBUS ERROR* Error occurred reading project XML file: " + ex + " at " + ex.getStackTrace()[0]);
                  }

                  
               }
            }
         }
      }
   }

    
   /**
    * Read the stream contents into a string
    * 
    * @param is
    *           The input stream
    *           
    * @return The stream contents
    * 
    * @throws IOException
    */
   public static String convertStreamToString(InputStream is) throws IOException
   {
      if (is != null)
      {
         Writer writer = new StringWriter();

         char[] buffer = new char[1024];
         try
         {
            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1)
            {
               writer.write(buffer, 0, n);
            }
         } finally
         {
            is.close();
         }
         return writer.toString();
      } else
      {
         return "";
      }
   }

   /**
    * Singleton pattern
    * @return The singleton instance of the project list
    */
   public static CBusProject getInstance()
   {
      return instance;
   }

   /**
    * @return the instanceBuilt
    */
   public boolean isInstanceBuilt() 
   {
      return instanceBuilt;
   }  
   
   /**
    * 
    * @return the project address
    */
   public String getProjectAddress()
   {
      return projectAddress;
   }
   
   
   /**
    * Get a specific network based on the network name
    * @param name
    * @return The network if found, otherwise null.
    */
   public CBusNetwork getNetwork(String name)
   {
      if(networks == null || networks.size() < 1)
      {
         log.error("*CBUS PROJECT ERROR* No networks found.");
         return null;
      }
      
      if(name == null || name.trim().length() < 1)
         name = this.defaultNetwork;
      
      if(networksLookup.containsValue(name))
         return networks.get(networksLookup.getKey(name));
      else
      {
         log.error("*CBUS PROJECT ERROR* Network name:" + name + " not found!");
         return null;
      }
   }
   
   /**
    * Get a specific network based on the network address
    * @param address
    * @return The network if found, otherwise null.
    */
   public CBusNetwork getNetworkByAddress(String address)
   {
      if(networks == null || networks.size() < 1)
      {
         log.error("*CBUS PROJECT ERROR* No networks found.");
         return null;
      }
      if(networks.containsKey(address))
         return networks.get(address);
      else
      {
         log.error("*CBUS PROJECT ERROR* Network address:" + address + " not found!");
         return null;
      }
   }
   
   public void generateFullCBusAddress(String network, String application, String group)
   {
   
   }
}

/**
 * Represents a single CBus network
 * 
 * Networks contain applications, applications contain group addresses.
 * 
 * @author Jamie Turner
 *
 */
class CBusNetwork
{
   /**
    * The logger
    */
   private final static Logger log = Logger.getLogger(CBusCommandBuilder.CBUS_LOG_CATEGORY);
   
   /**
    * The default application is set to Lighting
    */
   private final static String DEFAULT_APPLICATION = "Lighting";
   
   /**
    * Network name
    */
   private String name;
   
   /**
    * Network address
    */
   private String address;
   
   /**
    * Applications contained by the network
    */
   private Map<String, CBusApplication> applications;
   
   /**
    *  Application name and address lookup table
    */
   private BiDirectionalMap<String, String> applicationsLookup;

   /**
    * Adds an application to the network
    * 
    * @param app
    *           The application
    *           
    * @throws CBusException
    */
   public void add(CBusApplication app) throws CBusException
   {
      if(app == null)
         throw new CBusException("*CBUS ERROR* Tried to add a null application to a network", null);
      
      if(applications == null)
         applications = new HashMap<String, CBusApplication>();
      
      if(applicationsLookup == null)
         applicationsLookup = new BiDirectionalMap<String, String>();
      
      applications.put(app.getAddress(), app);
      applicationsLookup.put(app.getAddress(), app.getApplicationType());
   }
   
   /**
    * @return the name
    */
   public String getName() 
   {
      return name;
   }
   

   /**
    * @param name the name to set
    */
   public void setName(String name) 
   {
      this.name = name;
   }

   /**
    * @return the address
    */
   public String getAddress() 
   {
      return address;
   }

   /**
    * @param address the address to set
    */
   public void setAddress(String address) 
   {
      this.address = address;
   }

   /**
    * @return the applications
    */
   public Map<String, CBusApplication> getApplications() 
   {
      return applications;
   }

   
   /**
    * Get application by name
    * @param name
    * @return The application if found, otherwise null.
    */
   public CBusApplication getApplication(String name)
   {
      if(applications == null || applicationsLookup == null || applications.isEmpty())
      {
         log.error("*CBUS NETWORK ERROR* No applications found.");
         return null;
      }
      
      if(name == null || name.trim().length() < 1)
         name = DEFAULT_APPLICATION;
      
      if(applicationsLookup.containsValue(name))
      {
         String key = applicationsLookup.getKey(name);
         if(applications.containsKey(key))
            return applications.get(key);
         else
         {
            log.error("*CBUS NETWORK ERROR* Application name:" + name + " not found!");
            return null;
         }
      }         
      else
      {
         log.error("*CBUS NETWORK ERROR* Application name:" + name + " not found!");
         return null;
      }
   }
   
   /**
    * Get application by address
    * @param address
    * @return The application if found, otherwise null.
    */
   public CBusApplication getApplicationByAddress(String address)
   {
      if(applications == null || applicationsLookup == null || applications.isEmpty())
      {
         log.error("*CBUS NETWORK ERROR* No applications found.");
         return null;
      }
            
      if(applications.containsKey(address))
         return applications.get(address);
      else
      {
         log.error("*CBUS NETWORK ERROR* Application address:" + address + " not found!");
         return null;
      }
      
   }
}

/**
 * Represents a CBus application
 * 
 * An application contains multiple group addresses.
 * 
 * @author Jamie Turner
 *
 */
class CBusApplication
{
   /**
    * The logger
    */
   private final static Logger log = Logger.getLogger(CBusCommandBuilder.CBUS_LOG_CATEGORY);
   
   /**
    * The application name
    */
   private String applicationType;
   
   /**
    * The application address
    */
   private String address;
   
   /**
    * Lookup table for group names and addresses
    */
   private BiDirectionalMap<String, String> groupAddressesLookup;
   
   /**
    * @param applicationType the applicationType to set
    */
   public void setApplicationType(String applicationType) 
   {
      this.applicationType = applicationType;
   }

   /**
    * @param address the address to set
    */
   public void setAddress(String address) 
   {
      this.address = address;
   }

   /**
    * @return the name
    */
   public String getApplicationType() 
   {
      return applicationType;
   }

   /**
    * @return the address
    */
   public String getAddress() 
   {
      return address;
   }
   
   /**
    * Add a group address to the application
    * 
    * @param groupAddress
    * @param groupName
    */
   public void add(String groupAddress, String groupName)
   {
      if(groupAddressesLookup == null)
         groupAddressesLookup = new BiDirectionalMap<String, String>();
      
      groupAddressesLookup.put(groupAddress, groupName);
   }
   
   /**
    * Get the group address based on the name
    * 
    * @param name
    * 
    * @return The group address if found, or null if not
    */
   public String getGroupAddress(String name)
   {
      if(groupAddressesLookup == null)
      {
         log.error("*CBUS ERROR* No group addresses set up yet");
         return null;
      }
         
      
      if(groupAddressesLookup.containsValue(name))
         return groupAddressesLookup.getKey(name);
      else
      {
         log.error("*CBUS ERROR* Group name: " + name + " from application " + this.applicationType + " not found.");
         return null;
      }
   }
}


