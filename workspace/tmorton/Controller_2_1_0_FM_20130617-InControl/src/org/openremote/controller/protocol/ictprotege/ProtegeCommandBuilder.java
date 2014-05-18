/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.openremote.controller.protocol.ictprotege;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.jdom.Element;
import org.openremote.controller.command.Command;
import org.openremote.controller.command.CommandBuilder;
import org.openremote.controller.exception.NoSuchCommandException;
import org.openremote.controller.utils.Logger;

/**
 * NOTE THAT ALL MONITORING COMMANDS MUST BE SENT EACH TIME YOU LOGIN
 * 
 * @author Tomas
 */
public class ProtegeCommandBuilder implements CommandBuilder{
    
    /**
     * General Packet Structure Overview:
     * 1 Header (0x49)
     * 2 Header (0x43)
     * 3 Length (low byte of the total length of this packet including ALL bytes)
     * 4 Length (high byte of the total length of this packet including ALL bytes)
     * (16 bit number in little endian format (lowest value byte comes first))
     * 5 Packet type (Command 0x00, Data 0x01, System 0xC0) (will mainly use Command)
     * 6 Format (Encryption / Address)
     * 7... Data
     *      first 2 bits for encryption
     *      3rd bit for address (reserved?)
     *      4th to 7th reserved
     * End-1 Check sum (low byte) (if a checksum is specified)
     * End Check sum (high byte) (if a 16 bit checksum is specified)
     * 
     * --
     * 
     * Command Packets Data Structure
     * 0 Command Group
     * 1 Sub command
     * 2 Index of record to control (low byte)
     * 3 Index of record to control (second lowest byte)
     * 4 Index of record to control (second highest byte)
     * 5 Index of record to control (high byte)
     * Optional (command dependant)
     * 6 Activation Time (low byte)
     * 7 Activation Time (high byte)
     */
    
    /**
     * Setup Commands
     *  PROTEGE_XML_PROPERTY_ENCRYPTION_TYPE
     *  PROTEGE_XML_PROPERTY_ENCRYPTION_KEY
     *  PROTEGE_XML_PROPERTY_USER_PIN
     * Control Commands
     *  PROTEGE_XML_PROPERTY_RECORD TYPE //eg Door / Area
     *  PROTEGE_XML_PROPERTY_RECORD_INDEX //eg 1st door on list 
     *  PROTEGE_XML_PROPERTY_ACTIVTION_TIME //optional, apply to outputs
     */
    
    private static Logger log = ProtegeSystemConstants.log;
    
//EXAMPLE DOCUMENTATION FOR COMMANDS
   /**
    * String constant for parsing KNX protocol XML entries from controller.xml file.
    *
    * This constant is the expected property name value for KNX commands ({@value}):
    *
    * <pre>{@code
    * <command protocol = "knx" >
    *   <property name = "groupAddress" value = "x/x/x"/>
    *   <property name = "command" value = "ON"/>
    * </command>
    * }</pre>
    */
    
//
    
    public final static String PROTEGE_XMLPROPERTY_LOGIN_PIN = "pin"; //2 bytes
    public final static String PROTEGE_XMLPROPERTY_LOGIN_TIMEOUT = "login-time"; //2 bytes
    public final static String PROTEGE_XMLPROPERTY_RECORD_TYPE = "record-type";
    public final static String PROTEGE_XMLPROPERTY_RECORD_COMMAND = "record-command";  
    public final static String PROTEGE_XMLPROPERTY_RECORD_INDEX = "record-index";    //4 bytes
    public final static String PROTEGE_XMLPROPERTY_RECORD_VALUE = "record-value"; //2 bytes
    
    private ProtegeConnectionManager connectionManager;
    private int encryptionType;
    private int checksumType;
   
//    public ProtegeCommandBuilder(String address, int port, int encryptionType, int checksumType)
//    {        
//        if (connectionManager == null)
//        {
//            connectionManager = new ProtegeConnectionManager(address, port, encryptionType, checksumType);
//        }
//    }
    
    /**
     * Processes an element from the XML and creates a Command instance.
     * 
     * @param element
     * @return 
     */
    @Override
    public Command build(Element element)
    {
        if (connectionManager == null)
        {            
            log.setLevel(Level.ALL);
            try
            {                
                connectionManager = new ProtegeConnectionManager("203.97.50.236", 9189, 
                        ProtegeSystemConstants.ENCRYPTION_NONE, ProtegeSystemConstants.CHECKSUM_NONE, "1");
            } catch(Exception e)
            {
                connectionManager = new ProtegeConnectionManager("10.47.192.209", 9189, 
                        ProtegeSystemConstants.ENCRYPTION_NONE, ProtegeSystemConstants.CHECKSUM_NONE, "123456");                    
            }
            Thread thread = new Thread(connectionManager, "ProtegePacketListener");
            thread.start();
        }
        Map<String, String> paramMap = new HashMap<>();        
    
        List<Element> propertyElements = element.getChildren(
                CommandBuilder.XML_ELEMENT_PROPERTY, element.getNamespace());
        //Read the command from XML
        for (Element e : propertyElements)
        {
            String protegePropertyName = e.getAttributeValue(CommandBuilder.XML_ATTRIBUTENAME_NAME);
            String protegePropertyValue = e.getAttributeValue(CommandBuilder.XML_ATTRIBUTENAME_VALUE);
            log.error("Reading from XML: " + protegePropertyName + "=" + protegePropertyValue);
            switch (protegePropertyName.toLowerCase())
            {                
                case PROTEGE_XMLPROPERTY_LOGIN_PIN :
                    paramMap.put(PROTEGE_XMLPROPERTY_LOGIN_PIN, protegePropertyValue);
                    break;
                    
                case PROTEGE_XMLPROPERTY_LOGIN_TIMEOUT :
                    paramMap.put(PROTEGE_XMLPROPERTY_LOGIN_TIMEOUT, protegePropertyValue);
                    break;
                    
                case PROTEGE_XMLPROPERTY_RECORD_VALUE :
                    paramMap.put(PROTEGE_XMLPROPERTY_RECORD_VALUE, protegePropertyValue);
                    break;                    
                    
                case PROTEGE_XMLPROPERTY_RECORD_COMMAND :
                    paramMap.put(PROTEGE_XMLPROPERTY_RECORD_COMMAND, protegePropertyValue.toUpperCase());
                    break;
                    
                case PROTEGE_XMLPROPERTY_RECORD_TYPE :
                    paramMap.put(PROTEGE_XMLPROPERTY_RECORD_TYPE, protegePropertyValue.toUpperCase());
                    break;
                    
                case PROTEGE_XMLPROPERTY_RECORD_INDEX :
                    paramMap.put(PROTEGE_XMLPROPERTY_RECORD_INDEX, protegePropertyValue);
                    break;       
                    
                default :
                    log.warn("Unknown Protege property '<" + CommandBuilder.XML_ELEMENT_PROPERTY + " " +
                            CommandBuilder.XML_ATTRIBUTENAME_NAME + " = \"" + protegePropertyName + "\" " +
                            CommandBuilder.XML_ATTRIBUTENAME_VALUE + " = \"" + protegePropertyValue + "\"/>'."
                    );                   
            }
        }
        //Validate the mandatory elements        
        if (!paramMap.containsKey(PROTEGE_XMLPROPERTY_RECORD_TYPE))
        {
            throw new NoSuchCommandException(
               "Protege command is missing the mandatory '" + PROTEGE_XMLPROPERTY_RECORD_TYPE + "' property"
            );
        }
        if (!paramMap.containsKey(PROTEGE_XMLPROPERTY_RECORD_COMMAND))
        {
            throw new NoSuchCommandException(
               "Protege command is missing the mandatory '" + PROTEGE_XMLPROPERTY_RECORD_COMMAND + "' property"
            );
        }
        //check the values are set
        for (String s : paramMap.keySet())
        {
            String value = paramMap.get(s);
            if (value == null || value.isEmpty())
            {
                //is there a reason to use this exception 
                throw new NoSuchCommandException("Protege command '" + s + "' is empty.");                
            }
        }        
        //Translate the XML values into a Packet.
        //This performs the validation on all of the provided fields.
        ProtegePacket packet = new ProtegePacket(paramMap, encryptionType, checksumType);
        ProtegeCommand command = new ProtegeCommand(connectionManager, packet);

        return command;
    }
}
