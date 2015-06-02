/* 
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2011, OpenRemote Inc.
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
package org.openremote.controller.protocol.ictprotege;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.jdom.Element;
import org.openremote.controller.ICTProtegeConfiguration;
import org.openremote.controller.command.Command;
import org.openremote.controller.command.CommandBuilder;
import org.openremote.controller.exception.NoSuchCommandException;
import org.openremote.controller.protocol.ictprotege.network.ProtegeConnectionManager;
import org.openremote.controller.utils.Logger;

/**
 * Command Builder implementation to create ICT's ProtegeCommand objects.
 *
 * TODO: Replace "//MC" hints with a multiple controller communication
 * implementation.
 * 
 * @author Tomas Morton
 */
public class ProtegeCommandBuilder implements CommandBuilder
{

    /**
     * General Packet Structure Overview: 
     * 1 Header (0x49) 
     * 2 Header (0x43) 
     * 3 Length (low byte of the total length of this packet including ALL bytes)
     * 4 Length (high byte of the total length of this packet including ALL
     *  bytes) (16 bit number in little endian format (lowest value byte comes
     *  first)) 
     * 5 Packet type (Command 0x00, Data 0x01, System 0xC0) (mainly
     *  use Command) 
     * 6 Format (Encryption / Address) - first 2 bits for encryption, 
     *  th bit for address (reserved) 5th-8th bits reserved 
     * 7... Data 
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
     *  For example:
     *      6 Activation Time (low byte) 
     *      7 Activation Time (high byte)
     */
    private static Logger log = ProtegeUtils.log;

    //String constants for parsing XML values for commands from controller.xml
    public final static String PROTEGE_XMLPROPERTY_RECORD_COMMAND = "record-command";
    public final static String PROTEGE_XMLPROPERTY_RECORD_INDEX = "record-index";
    public final static String PROTEGE_XMLPROPERTY_RECORD_VALUE = "record-value";

    private static ICTProtegeConfiguration controllerConfig;
    private static ProtegeConnectionManager connectionManager;
    private static Map<String, ProtegePacket> packetMap;

    private EncryptionType encryptionType;
    private ChecksumType checksumType;
    private String monitoringPIN;
    private String encryptionKey;
    private String ipAddress;
    private int port;
    private boolean connectionInitialized;
    
    //Command string maps to a map of protege packets and connection managers
    //MC    private Map<String, Map<ProtegePacket, ProtegeConnectionManager>> multiControllerPacketMap();
    //MC private Map<String, Integer> addressMap;

    /**
     * Processes an element from the XML and creates a Command instance.
     *
     * @param element
     * @return
     */
    @Override
    public Command build(Element element)
    {
        //Initialize, once only
        if (!connectionInitialized)
        {
            connectionInitialized = true;
            loadConfiguration();
            connectToController();
            log.debug("ICT Protege ConnectionManager initialized.");
        }

        //Check if the packet is already in the cache
        ProtegePacket packet = packetMap.get(element.getAttribute("id").getValue());
        if (packet == null)
        {
            packet = createPacket(element);
        }
        else
        {
            packet.updateSequenceNumber();
        }
        return new ProtegeCommand(connectionManager, packet);
    }

    /**
     * Creates a new ProtegePacket from the controller.xml.
     *
     * @param element
     * @return
     */
    private ProtegePacket createPacket(Element element)
    {
        Map<String, String> paramMap = new HashMap<String, String>();
        List<Element> propertyElements = element.getChildren(
                CommandBuilder.XML_ELEMENT_PROPERTY, element.getNamespace());
        //Read the command from XML
        for (Element e : propertyElements)
        {
            String protegePropertyName = e.getAttributeValue(CommandBuilder.XML_ATTRIBUTENAME_NAME);
            String protegePropertyValue = e.getAttributeValue(CommandBuilder.XML_ATTRIBUTENAME_VALUE);
            //Change spaces for underscores to match the enumerator names
            protegePropertyValue = protegePropertyValue.replace(' ', '_').toUpperCase();
            log.debug("Reading from XML: " + protegePropertyName + "=" + protegePropertyValue);
            paramMap.put(protegePropertyName.toLowerCase(), protegePropertyValue);
        }
        //Validate the mandatory elements    
        if (!paramMap.containsKey(PROTEGE_XMLPROPERTY_RECORD_COMMAND))
        {
            log.error("Protege command is missing the mandatory '" + PROTEGE_XMLPROPERTY_RECORD_COMMAND + "' property");
            throw new NoSuchCommandException(
                    "Protege command is missing the mandatory '" + PROTEGE_XMLPROPERTY_RECORD_COMMAND + "' property"
            );
        }
        if (ProtegeCommandType.valueOf(paramMap.get(ProtegeCommandBuilder.PROTEGE_XMLPROPERTY_RECORD_COMMAND)) == ProtegeCommandType.SEND_LOGIN)
        {
            paramMap.put(ProtegeCommandBuilder.PROTEGE_XMLPROPERTY_RECORD_VALUE, connectionManager.getUserPin());
        }

        //Translate the XML values into a Packet. Attribute validation is done inside the class.
        ProtegePacket packet = new ProtegePacket(paramMap, encryptionType, checksumType);
        //cache the packet, based on its XML id. Except SEND_LOGIN
        if (packet.getCommandType() != ProtegeCommandType.SEND_LOGIN)
        {
            packetMap.put(element.getAttribute("id").getValue(), packet);
        }

        return packet;
    }

    /**
     * Loads ICT Protege configuration values from controller.xml.
     *
     */
    private void loadConfiguration()
    {
        log.info("Loading ICT Protege configuration from controller.xml.");
        controllerConfig = ICTProtegeConfiguration.readXML();
        String[] addresses = controllerConfig.getAddress().split("[,; ]");
        int portSplitter = addresses[0].indexOf(':');
        ipAddress = addresses[0].substring(0, portSplitter);
        port = Integer.parseInt(addresses[0].substring(portSplitter + 1));
        //MC
//        for (String s : addresses)
//        {            
//            int portSplitter = s.indexOf(':');
//            String ipAddress = s.substring(0, portSplitter);
//            int port = Integer.parseInt(s.substring(portSplitter + 1));
//            addressMap.put(ipAddress, port);
//        }
        monitoringPIN = controllerConfig.getMonitoringPIN();
        checksumType = ChecksumType.CHECKSUM_8; //No longer a user option
        String encryptionStr = controllerConfig.getEncryptionType();
        encryptionType = (encryptionStr.toUpperCase().equals("AES 128")) ? EncryptionType.ENCRYPTION_AES_128
                : (encryptionStr.toUpperCase().equals("AES 192")) ? EncryptionType.ENCRYPTION_AES_192
                : (encryptionStr.toUpperCase().equals("AES 256")) ? EncryptionType.ENCRYPTION_AES_256
                : EncryptionType.ENCRYPTION_NONE;
        encryptionKey = controllerConfig.getEncryptionKey();
        //Validate key length
        int keyLength = (encryptionType == EncryptionType.ENCRYPTION_AES_128) ? 16 
                :            (encryptionType == EncryptionType.ENCRYPTION_AES_192) ? 24
                :            32;
        if (encryptionKey.length() > keyLength)
        {
            log.error("Encryption key is too long (Expected " + keyLength + " characters).  Please confirm that the key matches the one set in Protege.");
            encryptionKey = encryptionKey.substring(0, keyLength);
        } else
        {
            log.error("Encryption key is too short (Expected " + keyLength + " characters).  Please confirm that the key matches the one set in Protege.");            
            while (encryptionKey.length() < keyLength)
            {
                encryptionKey += ' '; //pad with spaces to match the GX/WX function.
            }
        }
        //Debug information
        log.info("IP address: " + ipAddress);
        log.info("Port: " + port);
        log.info("Monitoring PIN: <Hidden>");
        log.info("Encryption key: <Hidden>");
        log.info("Encryption type: " + encryptionType);
        log.info("Configuration successfully loaded.");
    }

    /**
     * Sets up a connection to the Protege Controller and starts its thread.
     *
     */
    private void connectToController()
    {
        log.error("Connecting to Protege controller at " + ipAddress + ":" + port + "...");
        try
        {
            connectionManager = new ProtegeConnectionManager(ipAddress, port,
                    encryptionType, encryptionKey, checksumType, monitoringPIN);
            new Thread(connectionManager, "ProtegeConnectionManager").start();
        }
        catch (Exception e) //Safety net
        {
            log.error("Failed to connect to the Protege controller at " + ipAddress
                    + ":" + port + ". " + e);
            connectionInitialized = false;
        }
        log.error("Connection established.");
        //intialize cache
        packetMap = new HashMap<String, ProtegePacket>();
        
//MC    addressMap = new HashMap<String, Integer>();
//MC        multiControllerPacketMap = new HashMap<String, Map<String, ProtegePacket>>();
    }
}
