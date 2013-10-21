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

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;
import org.openremote.controller.component.EnumSensorType;

/**
 * An individual CBus Group address, such as a lighting address
 *  
 * @author Jamie Turner
 */
public class CBusAddress 
{   
   /**
    * The logger
    */
   private final static Logger log = Logger.getLogger(CBusCommandBuilder.CBUS_LOG_CATEGORY);
   
   /** The project address portion of the full address */
   String projectAddress = null;
   
   /** The network address portion of the full address */
   String networkAddress = null;
   
   /** The application address portion of the full address */
   String applicationAddress = null;
   
   /** The group address portion of the full address */
   String groupAddress = null;
   
   /** The controller that talks to CGate for this address */
   CBusGateway gateway = null;
   
   /**
    * Commands we should update when our status changes
    */
   protected List<CBusCommand> commands;
   
   /** The current level of the group address */
   int level = -1;
      
   
   /**
    * Constructor
    * 
    * @param gateway
    *           Controls access to the CBus system
    * @param fullAddress
    *           The full address of this group address
    */
   public CBusAddress(CBusGateway gateway, String fullAddress)
   {
      this.gateway = gateway;
      this.commands = new ArrayList<CBusCommand>();      
      
      // separate the different parts of the address
      String[] splitAddress = fullAddress.split("/");
      
      for(int arrayIdx = 0; arrayIdx < splitAddress.length; arrayIdx++)
      {
         if(splitAddress[arrayIdx] != null && splitAddress[arrayIdx].length() > 0 && !splitAddress[arrayIdx].contains("/"))
         {
            if(projectAddress == null)
               projectAddress = splitAddress[arrayIdx];
            else if(networkAddress == null)
               networkAddress = splitAddress[arrayIdx];
            else if(applicationAddress == null)
               applicationAddress = splitAddress[arrayIdx];
            else
            {
               groupAddress = splitAddress[arrayIdx];
               break;
            }              
         }
      }
      
   }
   
   
   /**
    * Constructor
    * 
    * @param gateway
    *           Controls access to the CBus system
    * @param projectAddress
    *           Project address portion of the full address
    * 
    * @param networkAddress
    *           Network address portion of the full address
    *           
    * @param applicationAddress
    *           Application address portion of the full address
    *           
    * @param groupAddress
    *           Group address portion of the full address
    *           
    */
   public CBusAddress(CBusGateway gateway, String projectAddress, String networkAddress, String applicationAddress, String groupAddress)
   {
      this.projectAddress = projectAddress;
      this.networkAddress = networkAddress;
      this.applicationAddress = applicationAddress;
      this.groupAddress = groupAddress;
      this.gateway = gateway;
      //this.isValid = true;
   }
   
   /**
    * Gets the full formatted address
    * 
    * @return The full formatted address
    * 
    * @throws CBusException
    *            The full address is invalid because something is missing
    */
   public String getAddress() throws CBusException
   {
      if(projectAddress == null || projectAddress.length() < 1)
         throw new CBusException("Cannot create CBUS address with no CBUS project address: " + projectAddress, null);
      
      if(networkAddress == null || networkAddress.length() < 1)
         throw new CBusException("Cannot create CBUS address with invalid network address: " + networkAddress, null);
            
      if(applicationAddress == null || applicationAddress.length() < 1)
         throw new CBusException("Cannot create CBUS address with invalid application address: " + applicationAddress, null);
      
      if(groupAddress == null || groupAddress.length() < 1)
         throw new CBusException("Cannot create CBUS address with invalid group address: " + groupAddress, null);
         
      
      StringBuilder sb = new StringBuilder("//")
         .append(projectAddress)
         .append("/")
         .append(networkAddress)
         .append("/")
         .append(applicationAddress)
         .append("/")
         .append(groupAddress);
      
      return sb.toString();
   }
   
   /**
    * Dim the lights at this group address
    * 
    * @param dimPercent
    *           The percentage to dim (0-100)
    *           
    * @param duration
    *           How long to take to dim the lights
    *           
    */
   public void dim(String dimPercent, String duration) 
   {
      try
      {
         log.debug(new StringBuilder("Dimming ").append(getAddress()).append(" to >").append(dimPercent).append("< % in >").append(duration).append("<").toString());
         
         //CBUS doesn't like percentages in RAMP commands even though the manual says it does
         //convert dimPercent to a 0-255 value
         try
         {
            int dimRaw = Math.round(2.55f * Float.parseFloat(dimPercent));

            StringBuilder sb = new StringBuilder("ramp ")
            .append(getAddress())
            .append(" ")
            .append(dimRaw)
            .append(" ");

            //use 1 second if invalid duration provided
            if(duration != null && duration.length() > 0)
               sb.append(duration);
            else
               sb.append("1s");

            sendCBusMessage(sb.toString());
         }
         catch(NumberFormatException nfe)
         {
            log.error("*CBUS ERROR* Tried to dim using an invalid level value: " + dimPercent);
         }
         
      }
      catch(CBusException ce)
      {
         log.error("*CBUS ERROR* error occurred trying to dim: " + ce.getMessage());
      }
      
   }
   
   
   /**
    * Turn this group address ON or OFF
    * 
    * @param turnOn
    *           true turns the address ON, false turns it off
    */
   public void turnOnOff(boolean turnOn) {
      try
      {                  
         StringBuilder sb = new StringBuilder(turnOn ? "on " : "off ")
            .append(getAddress());
         
         sendCBusMessage(sb.toString());
         
      }
      catch(CBusException ce)
      {
         log.error("*CBUS ERROR* error occurred trying to turn on or off: " + ce.getMessage());
      }
      
   }
   
   /**
    * Set this address to a specific value
    * 
    * @param value
    *           The value
    */
   public void setValue(String value) 
   {
      try
      {
         CBusApplicationType appType = CBusSystem.getApplicationTypeByAddress(networkAddress, applicationAddress);
         log.debug("Got application type " + appType);
            
         StringBuilder sb = new StringBuilder();
         switch(appType)
         {
         case ENABLE:
            sb.append("enable set ");
            break;
            
         case TRIGGER:
            sb.append("trigger event ");
            break;
            
         case LIGHTING:
            sb.append("ramp ");
            break;
            
            default:
               log.warn("*CBUS warning* Setting raw values on applications other than Lighting, Enable and Trigger are not supported. App used: " + appType, null);
         }
         
            
         if((appType.equals(CBusApplicationType.LIGHTING) && checkValidLevelValue(value, true)) || checkValidLevelValue(value, false))
         {
            int rawValue = getValidRawValue(value); //sometimes have issues using percentages in commands
            if(rawValue != -1)
            {
               sb.append(getAddress())
               .append(" ")
               .append(rawValue);
               
               if(appType.equals(CBusApplicationType.LIGHTING))
                  sb.append(" 1s");

               sendCBusMessage(sb.toString());
            }
         }
         else
            throw new CBusException("Invalid value used in setValue for app:" + applicationAddress + ", value=" + value, null);
         
      }
      catch(CBusException ce)
      {
         log.error("*CBUS ERROR* error occurred trying to trigger event: " + ce.getMessage());
      }
      
   }
   
   /**
    * Get the current value of this address
    * 
    * @param asPercent
    *           true gets the value as a percentage (0-100), false gets the raw value (0-255)
    *           
    * @param includePercentageCharacter
    *           true appends a % to the value, false does nothing
    *           
    * @return The current value of this address
    */
   public String getValue(boolean asPercent, boolean includePercentageCharacter)
   {
      String returnValue = "0";
      if(level == -1)
      {
         log.debug(new StringBuilder("Getting initial value for //").append(projectAddress).append("/").append(networkAddress).append("/")
               .append(applicationAddress).append("/").append(groupAddress).toString());
         this.queryCBusLevel();         
      }         
      else
      {
         if(asPercent)
         {
            StringBuilder sb = new StringBuilder(Long.toString(Math.round((((float) level) / 255.0f) * 100.0f)));
            if(includePercentageCharacter)
               sb.append("%");
         
            returnValue = sb.toString();
         }                    
         else
            returnValue = Integer.toString(level);
      }      
            
      return returnValue;
   }
   
   /**
    * Check whether this address is ON or OFF
    * 
    * @return True if the address is ON, false if OFF
    */
   public boolean isOn()
   {
      String value = getValue(false, false);
      boolean returnValue = false;
      if(value != null && value.length() > 0)
      {
         try
         {

            int rawValue = Integer.parseInt(value);
            if(rawValue > 255 || rawValue < 0)
               log.error("*CBUS Error* Invalid value for " + networkAddress + "/" + applicationAddress + "/" + groupAddress + " = " + value);
            else
               returnValue = (rawValue > 0);

         }
         catch(NumberFormatException nfe)
         {
            log.error("*CBUS Error* Invalid value for " + networkAddress + "/" + applicationAddress + "/" + groupAddress + " = " + value);
         }
      }
      
      return returnValue;
   }
   
   /**
    * Pulse this address to ON for a specific number of seconds
    * 
    * @param seconds
    *           The number of seconds to turn the address ON for
    */
   public void pulse(int seconds)
   {
      try
      {
         String address = getAddress();
         StringBuilder sb = new StringBuilder("on ")
            .append(address);
         
         sendCBusMessage(sb.toString());
         
         Date d = new Date();
         Calendar c = Calendar.getInstance();
         c.setTime(d);
         c.add(Calendar.SECOND, seconds);
         
         CBusSystem.addToPulseCache(new PulseCacheElement(this, c.getTime()));
         
      }
      catch(CBusException ce)
      {
         log.error("*CBUS ERROR* error occurred trying to pulse a group address on: " + ce.getMessage());
      } 
   }
   
   /**
    * Set this address to a raw level value
    * 
    * @param level
    *           The level to set (-1 to 255). -1 indicates the level should be queried directly
    */
   public void setLevel(int level)
   {
      if(level >= -1 && level <= 255)
         this.level = level;
      else
         log.error("*CBUS ERROR* Tried to set an address to an invalid value: " + level);
   }
   
   /**
    * Query the CBus level directly by sending a query for this address.
    * Normally the value is published by CGate every time it changes.
    */
   public void queryCBusLevel()
   {
      try
      {
         this.sendCBusMessage(new StringBuilder("get ").append(getAddress()).append(" level").toString());
      }
      catch(CBusException ce)
      {
         log.error("*CBUS ERROR* Couldn't query the CBUS level for an address most likely because the address is invalid! Full error: " + ce.getMessage() + " at "+ ce.getStackTrace()[0].toString(), ce.getCause());
      }
   }
   
   /**
    * Send a command to CBus via CGate
    * @param msg
    *           The CGate command message to send.
    */
   private void sendCBusMessage(String msg) 
   {
      log.debug("Sending C-BUS Message: " + msg);
      ((CBusGateway) this.gateway).sendCommand(msg);      
   }
   
   /**
    * Utility method to convert the value received from CGate into a valid level value
    * @param value
    *           The value received from CGate
    *           
    * @return The converted level value
    */
   private int getValidRawValue(String value)
   {
      int returnValue = -1;
      
      if(value != null && value.length() > 0)
      {         
         if(value.indexOf("%") > 0)
         {
            try
            {
               float f = NumberFormat.getPercentInstance(Locale.ENGLISH).parse(value).floatValue();
               if(f >= 0.0 && f <= 1.0)
                  returnValue = Math.round(f * 255.0f);
            }
            catch(ParseException pe)
            {
               log.error("*CBUS Error* Invalid % value: " + value);
            }
         }
         else
         {
            try
            {
               returnValue = Integer.parseInt(value);
            }
            catch(NumberFormatException nfe)
            {
               log.error("*CBUS Error* Invalid raw value: " + value);
            }
         }
      }
      
      return returnValue;
   }
   
   /**
    * Utility method to check whether the value received from CGate is valid
    * 
    * @param value
    *           The CGate value to check
    *           
    * @param allowPercentages
    *           True treats percentage values as valid, false treats percentage values as invalid
    *           
    * @return True if the value is valid, false if not
    */
   private boolean checkValidLevelValue(String value, boolean allowPercentages)
   {
      boolean isValid = false;
      try
      {
         if(allowPercentages && value.indexOf("%") > 0)
         {
            double d = NumberFormat.getPercentInstance(Locale.ENGLISH).parse(value).doubleValue();
            if(d >= 0.0 && d <= 1.0)
               isValid = true;
         }
         else
         {
            int i = Integer.parseInt(value);
            if(i >= 0 && i <= 255)
               isValid = true;
         }
      }
      catch(NumberFormatException nfe)
      {}
      catch(ParseException pe)
      {}
      
      return isValid;
   }

   /**
    * Update all listeners to this address
    */
   public void updateListeners() 
   {
      log.debug("Updating commands for CBUS address");
      for (CBusCommand command : commands) {
         synchronized(command)
         {
            command.updateSensors(this);
         }
      }      
   }
   
   
   /**
    * Add a command to update on value change.
    * 
    * @param command CBusCommand to add
    */
   public void addCommand(CBusCommand command) 
   {
      commands.add(command);
   }
   
   /**
    * Remove a command to update on value change.
    * 
    * @param command CBusCommand to remove
    */
   public void removeCommand(CBusCommand command) 
   {
      commands.remove(command);
   }

   
   

}
