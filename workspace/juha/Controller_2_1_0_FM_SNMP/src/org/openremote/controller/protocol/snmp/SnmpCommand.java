/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2010, OpenRemote Inc.
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
package org.openremote.controller.protocol.snmp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.*;

import org.apache.log4j.Logger;
import org.openremote.controller.command.ExecutableCommand;
import org.openremote.controller.command.StatusCommand;
import org.openremote.controller.component.EnumSensorType;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.Variable;
import org.snmp4j.smi.AbstractVariable;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.smi.*;
import org.snmp4j.SNMP4JSettings;

/**
 * The Socket Event.
 *
 * @author Rde01
 */
public class SnmpCommand implements ExecutableCommand, StatusCommand {

   /** The logger. */
   private static Logger logger = Logger.getLogger(SnmpCommand.class.getName());

   /** The default timeout used to wait for a result */
   public static final int DEFAULT_TIMEOUT = 5000;
   
   /** A name to identify event in controller.xml. */
   private String name;

   /** A pipe separated list of command string that are sent over the socket */
   private String command;

   /** The IP to which the socket is opened */
   private String ip;

   /** The port that is opened */
   private String port;

   /** The snmp object identifier */
   private String oid;
   
   /** The snmp set object value */
   private String setvalue;
   
   /** The snmp set object type */
   private String settype;
   
   /** The get regex value */
   private String getregex="";
   
   /** The get replacement value */
   private String getregexreplacement="";
   
   /**
    * Gets the command.
    *
    * @return the command
    */
   public String getCommand() {
      return command;
   }

   /**
    * Sets the command.
    *
    * @param command the new command
    */
   public void setCommand(String command) {
      this.command = command.toLowerCase();
   }

   /**
    * Gets the name.
    *
    * @return the name
    */
   public String getName() {
      return name;
   }

   /**
    * Sets the name.
    *
    * @param name the new name
    */
   public void setName(String name) {
      this.name = name;
   }


   /**
    * Gets the ip
    * @return the ip
    */
   public String getIp() {
      return ip;
   }

   /**
    * Sets the ip
    * @param ip the new ip
    */
   public void setIp(String ip) {
      this.ip = ip;
   }

   /**
    * Gets the port
    * @return the port
    */
   public String getPort() {
      return port;
   }

	/**
	 * Sets the port
	 * @param port the new port
	 */
   public void setPort(String port) {
      this.port = port;
   }
   
   /**
    * Gets the oid
    * @return the oid
    */
   public String getOid() {
      return oid;
   }

	/**
	 * Sets the oid
	 * @param oid the new oid
	 */
   public void setOid(String oid) {
      this.oid = oid;
   }
   
   /**
    * Gets the value of the object
    * @return the value
    */
   public String getSetvalue() {
      return setvalue;
   }

	/**
	 * Sets the value of the object
	 * @param value the new value
	 */
   public void setSetvalue(String setvalue) {
     this.setvalue = setvalue;
   }
   
   /**
    * Gets the type of the object
    * @return the type
    */
   public String getSettype() {
      return settype;
   }

	/**
	 * Sets the type of the object
	 * @param value the new type
	 */
   public void setSettype(String settype) {
     this.settype = settype;
   }

   /**
    * Gets the regex for the get command return value
    * @return the value
    */
   public String getGetregex() {
      return getregex;
   }

	/**
	 * Sets the regex for the get return value
	 * @param value the new value
	 */
   public void setGetregex(String getregex) {
  	   this.getregex = getregex; 
   }
   
   /**
    * Gets the replacement for the regex get command return value
    * @return the value
    */
   public String getGetregexreplacement() {
      return getregexreplacement;
   }

	/**
	 * Sets the replacement for the regex get return value
	 * @param value the new value
	 */
   public void setGetregexreplacement(String getregexreplacement) {
  	   this.getregexreplacement = getregexreplacement; 
   }
   
   /**
    * {@inheritDoc}
    */
   @Override
   public void send() {
      //requestSocket();
	  if ("set".equals(this.getCommand())){
		  snmpSet(this.getIp(), "private", this.getOid());
	  } else if ("get".equals(this.getCommand())){
	  	  snmpGet(this.getIp(), "public", this.getOid());
	  } else {
	  	  System.out.println(this.getCommand() + " is a wrong command use get or set");
	  }
   }

   @Override
   public String read(EnumSensorType sensorType, Map<String, String> stateMap) {
	   String state = "";
	   String returnvalue = "";
	   state = snmpGet(this.getIp(), "public", this.getOid());
	   state=state.trim();
	   
	   try
	   {
		   //System.out.println("x-- "+getGetregex()+" --- "+getGetregexreplacement());
		   if(this.getGetregex()!="" || this.getGetregexreplacement()!="")
		   {
			   //System.out.println("1-- "+state);
			   String[] replacements = this.getGetregexreplacement().split("\\|");
			   //System.out.println("2-- "+replacements);
			   Pattern pattern = Pattern.compile(getGetregex());
			   //System.out.println("3-- "+pattern);
			   Matcher matcher = pattern.matcher(state);
			   //System.out.println("4-- "+matcher);
			   boolean matchfound = matcher.find();
			   //System.out.println("5-- "+matchfound);
			   
			   if (matchfound) {
			       // Get all groups for this match
			       for (int i=1; i<=matcher.groupCount(); i++) {
			           String group = matcher.group(i);
			           //System.out.println("6-- "+i+"--"+group);
			           if (i<=replacements.length && group!=null)
			           {
				           if(replacements[i-1].equals("null"))
				        	   returnvalue += group;
				           else
				        	   returnvalue += replacements[i-1];
				           //System.out.println("7-- "+i+"--"+replacements[i-1]);
			           }
			       }
			   }
			   else
			   {
				   return "N/A";
			   }
			   
		   }
		   else
		   {
			   return state;
		   }
		   
	   }
	   catch(Exception ex)
	   {
		   
	   }
	   
	   
	   return returnvalue;
	   /*
	   if(sensorType == EnumSensorType.SWITCH){
		   if (state == null)
		   {
			   return "off";
		   } else if (state.equals("1")){
			   return "on";
		   } else if (state.equals("2")){
			   return "off";
		   } else{
			   return "off";
		   }    
	   } else if (sensorType == EnumSensorType.LEVEL){
		   try{
			   int statusint = Integer.parseInt(state);
			   statusint = statusint % 100;
			   return Integer.toString(statusint);
		   }
		   catch(Exception e){
			   throw new Error ("Return value: "+state+" not usable as level.");
		   }
	   } else if (sensorType == EnumSensorType.RANGE){
		   throw new Error ("Range sensortype not implemented yet.");
	   } else if (sensorType == EnumSensorType.CUSTOM){
		   return state;
	   } else if (sensorType == EnumSensorType.COLOR){
		   throw new Error ("Color sensortype not implemented yet.");
	   } else {
		   throw new Error("Unrecognized sensor type " + sensorType);
	   }*/
   }
   
   public void snmpSet(String strAddress, String community, String strOID)
   {
		try
		{

			// Create TransportMapping and Listen
		    TransportMapping transport = new DefaultUdpTransportMapping();
		    transport.listen();
	
		    // Create Target Address object
		    CommunityTarget comtarget = new CommunityTarget();
		    comtarget.setCommunity(new OctetString(community));
		    comtarget.setVersion(SnmpConstants.version1);
		    comtarget.setAddress(new UdpAddress(strAddress+"/"+ this.getPort()));
		    comtarget.setRetries(2);
		    comtarget.setTimeout(DEFAULT_TIMEOUT);
	
		    // Create the PDU object
		    PDU pdu = new PDU();
		    
		    // Setting the Oid and Value for sysContact variable
		    OID oid = new OID(strOID);
		    //Variable var = new OctetString(sysContactValue);
		    
			//String snmpvariabletype = "Integer32";
			int smiSyntax = AbstractVariable.getSyntaxFromString(this.getSettype());
			Variable var = AbstractVariable.createFromSyntax(smiSyntax);
			if (var instanceof AssignableFromString) {
			      ((AssignableFromString)var).setValue(this.getSetvalue());
			}
			//System.out.println("---var: "+ var);
		    
		    //Variable var = new Integer32(this.getValue());
		    VariableBinding varBind = new VariableBinding(oid,var);
		    pdu.add(varBind);
		   
		    pdu.setType(PDU.SET);
		    //pdu.setRequestID(new Integer32(1));
	
		    // Create Snmp object for sending data to Agent
		    Snmp snmp = new Snmp(transport);
	
		    System.out.println("Request:\nSending Snmp Set Request to Agent...");
		    ResponseEvent response = snmp.set(pdu, comtarget);
	
		    // Process Agent Response
		    if (response != null)
		    {
			      System.out.println("\nResponse:\nGot Snmp Set Response from Agent");
			      PDU responsePDU = response.getResponse();
		
			      if (responsePDU != null)
			      {
				        int errorStatus = responsePDU.getErrorStatus();
				        int errorIndex = responsePDU.getErrorIndex();
				        String errorStatusText = responsePDU.getErrorStatusText();
			
				        if (errorStatus == PDU.noError)
				        {
				          System.out.println("Snmp Set Response = " + responsePDU.getVariableBindings());
				        }
				        else
				        {
				          System.out.println("Error: Request Failed");
				          System.out.println("Error Status = " + errorStatus);
				          System.out.println("Error Index = " + errorIndex);
				          System.out.println("Error Status Text = " + errorStatusText);
				        }
			      }
			      else
			      {
			        System.out.println("Error: Response PDU is null");
			      }
		    }
		    else
		    {
		      System.out.println("Error: Agent Timeout... ");
		    }
		    snmp.close(); 
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
   
   /*
	* The code is valid only SNMP version1. SnmpGet method
	* return Response for given OID from the Device.
	*/
	public String snmpGet(String strAddress, String community, String strOID)
	{
		String str="";
		try
		{
			TransportMapping transport = new DefaultUdpTransportMapping();
			transport.listen();
			CommunityTarget comtarget = new CommunityTarget();
			comtarget.setCommunity(new OctetString(community));
			comtarget.setVersion(SnmpConstants.version1);
			comtarget.setAddress(new UdpAddress(strAddress+"/"+ this.getPort()));
			comtarget.setRetries(2);
			comtarget.setTimeout(DEFAULT_TIMEOUT);
			PDU pdu = new PDU();
			ResponseEvent response;
			pdu.add(new VariableBinding(new OID(strOID)));
			pdu.setType(PDU.GET);
			Snmp snmp = new Snmp(transport);
			response = snmp.get(pdu,comtarget);
			if(response != null)
			{
				if(response.getResponse().getErrorStatusText().equalsIgnoreCase("Success"))
				{
					PDU pduresponse=response.getResponse();
					str=pduresponse.getVariableBindings().firstElement().toString();
					if(str.contains("="))
					{
						int len = str.indexOf("=");
						str=str.substring(len+1, str.length());
					}
				}
			}
			else
			{
				System.out.println("Feeling like a TimeOut occured ");
			}
			snmp.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		//System.out.println("Response="+str);
		return str;
	}

}
