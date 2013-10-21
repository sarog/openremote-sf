package org.openremote.controller.protocol.cbus;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

public class CBusSystem
{
   /**
    * The logger
    */
   private final static Logger log = Logger.getLogger(CBusCommandBuilder.CBUS_LOG_CATEGORY);
   
   /**
    * The gateway associated with the CBus system
    */
   private CBusGateway gateway;
   
   /**
    * Cache of group addresses and their values, based on the full formatted CBus address
    */
   private Map<String, CBusAddress> cache = new ConcurrentHashMap<String, CBusAddress>();
   
   /**
    * Cache of pulsed group addresses and their durations so we remember to turn them off
    */
   private static List<PulseCacheElement> pulseCache = new ArrayList<PulseCacheElement>();
      
   /**
    * Constructor
    * 
    * @param gateway
    *           The gateway this system is using to communicate with CGate
    */
   public CBusSystem(CBusGateway gateway)
   {
      this.gateway = gateway;
   }
   
   /**
    * Build the project list based on the configuration file
    */
   public synchronized void initialiseProjects(String cbusProjectFile) throws CBusException
   {
      if(!CBusProject.getInstance().isInstanceBuilt())
         CBusProject.loadProjectFromXMLFile(cbusProjectFile);
   }
   
   /**
    * Get an application
    * 
    * @param applicationName
    * @return The application if it exists, otherwise null.
    */
   static CBusApplicationType getApplicationType(String applicationName)
   {
      CBusApplicationType a = CBusApplicationType.NULL;
      try
      {
         a = CBusApplicationType.forProjectName(applicationName);
      }
      catch(Exception ex)
      {
         a = CBusApplicationType.NULL;
      }
      
      if(a == null || a.equals(CBusApplicationType.NULL))
      {
         try
         {
            a = CBusApplicationType.valueOf(applicationName);
         }
         catch(Exception ex)
         {
            log.error("**CBUS ERROR** Invalid application type from Project List: " + applicationName);
            a = CBusApplicationType.NULL;
         }
      }
      return a;      
   }
   
   /**
    * Get an application type by network address and application address
    * 
    * @param networkAddress
    * @param applicationAddress
    * @return The application if it exists, otherwise null.
    */
   static CBusApplicationType getApplicationTypeByAddress(String networkAddress, String applicationAddress)
   {
      CBusNetwork n = CBusProject.getInstance().getNetworkByAddress(networkAddress);
      if(n == null)
      {
         log.error("*CBUS ERROR* Invalid network address: " + networkAddress);
         return CBusApplicationType.NULL;
      }
      
      CBusApplication app = n.getApplicationByAddress(applicationAddress);
      if(app == null)
      {
         log.error("*CBUS ERROR* Invalid application address: " + applicationAddress);
         return CBusApplicationType.NULL;
      }
      
    
      return getApplicationType(app.getApplicationType());
            
   }
   
   /**
    * Process an update to something on the CBus
    * @param address
    *           The full formatted CBus address, including project, network, application and group address
    * @param val
    *           The value received from CGate
    */
   public void processUpdate(String address, int val)
   {
      log.debug(new StringBuilder("Cache updated - address=").append(address).append(", value=").append(val).toString());
      
      CBusAddress a = getCBusAddress(address);
      a.setLevel(val);
      cache.put(address, a);
      
      log.debug("Updating CBUS commands...");
      a.updateListeners();
   }
   
   /**
    * Get a single CBusAddress object based on the full formatted CBus address
    * 
    * @param rawAddress
    *           full formatted CBus address
    *           
    * @return The CBusAddress object if it exists, otherwise a new one is created and returned
    */
   public CBusAddress getCBusAddress(String rawAddress)
   {
      if(rawAddress != null && rawAddress.length() > 0)
      {
         CBusAddress a = cache.get(rawAddress);
         if(a == null)
         {
            a = new CBusAddress((CBusGateway) gateway, rawAddress);
            cache.put(rawAddress, a);
         }

         return a;
      }
      else
      {
         log.debug("Tried to get a null CBus Address");
         return new CBusAddress((CBusGateway) gateway, "");
      }
   }
   
   /**
    * Get a single CBusAddress object based on command values
    * 
    * @param network
    * @param application
    * @param group
    * @return The CBusAddress object if found, otherwise a new one is created and returned
    */
   public CBusAddress getCBusAddress(String network, String application, String group)
   {
      StringBuilder sb = new StringBuilder();
      if(CBusProject.getInstance().isInstanceBuilt())
      {
         CBusNetwork n = CBusProject.getInstance().getNetwork(network);
         if(n != null)
         {
            CBusApplication a = n.getApplication(application);
            if(a != null)
            {
               sb.append("//").append(CBusProject.getInstance().getProjectAddress()).append("/")
                  .append(n.getAddress()).append("/").append(a.getAddress()).append("/").append(a.getGroupAddress(group));
            }
            else
               log.error("*CBUS ERROR* CBus application " + application + " does not exist under network " + network);
         }
         else
            log.error("*CBUS ERROR* CBus network " + network + " does not exist");
      }
      else
         log.error("*CBUS ERROR* CBus project file not loaded!");
      
      return getCBusAddress(sb.toString());
   }
   
   /**
    * Get's the current value of the CBus Address based on the network, application and group address
    * 
    * @param networkAddress
    * @param applicationAddress
    * @param groupAddress
    * @return
    */
   public CBusAddress getCBusAddressByAddress(String networkAddress, String applicationAddress, String groupAddress)
   {
      if(CBusProject.getInstance().isInstanceBuilt())
      {
         return getCBusAddress((new StringBuilder("//").append(CBusProject.getInstance().getProjectAddress()).append("/")
               .append(networkAddress).append("/").append(applicationAddress).append("/").append(groupAddress)).toString());
      }
      else
         return null;
   }
   
   /**
    * Turn off any pulses that have expired
    */
   public void turnOffExpiredPulses()
   {
      if(pulseCache.size() > 0)
      {
         //use array so we can safely remove items without affecting the iterator
         PulseCacheElement[] array = (PulseCacheElement[]) pulseCache.toArray();
         for(PulseCacheElement pce : array)
         {
            if(pce.getExpires() != null)
            {
               if(pce.getExpires().after(new Date()))
               {               
                  if(pce.getAddress() != null)
                  {                  
                     pce.getAddress().turnOnOff(false);                  
                  }

                  //remove from cache
                  pulseCache.remove(pce);
               }
            }
         }
      }
      
   }

   /**
    * Store a reminder to turn off an address after time has elapsed
    * 
    * @param pulseCacheElement
    */
   public static void addToPulseCache(PulseCacheElement pulseCacheElement) 
   {
      pulseCache.add(pulseCacheElement);
      
   }
     
}

/**
 * Represents a reminder to turn off a group address after it has been pulsed ON for some time
 * 
 * @author Jamie Turner
 *
 */
class PulseCacheElement
{
   /**
    * The address that needs to be turned off
    */
   private CBusAddress address;
   
   /**
    * When the address needs to be turned off
    */
   private Date expires;
   
   /**
    * Default constructor
    */
   public PulseCacheElement() 
   {
   }

   /**
    * Constructor
    * 
    * @param address
    *           Address to turn off
    *           
    * @param expires
    *           When to turn it off
    */
   public PulseCacheElement(CBusAddress address, Date expires) 
   {
      this.address = address;
      this.expires = expires;
   }

   /**
    * @return the address
    */
   public CBusAddress getAddress() 
   {
      return address;
   }

   /**
    * @param address the address to set
    */
   public void setAddress(CBusAddress address) 
   {
      this.address = address;
   }

   /**
    * @return expires
    */
   public Date getExpires() 
   {
      return expires;
   }

   /**
    * @param expires the expires to set
    */
   public void setExpires(Date expires) 
   {
      this.expires = expires;
   }
      
}
