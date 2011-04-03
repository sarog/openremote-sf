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
package org.openremote.controller.protocol.livingColors;

import java.util.Map;

import org.apache.log4j.Logger;
import org.openremote.controller.command.ExecutableCommand;
import org.openremote.controller.command.StatusCommand;
import org.openremote.controller.component.EnumSensorType;

/**
 * The Socket Event.
 * 
 * @author Marcus 2009-4-26
 */
public class LivingColorsCommand implements ExecutableCommand, StatusCommand {
   
   public static String colorEquipmentStatus = "000000";
    
    /** The logger. */
    private static Logger logger = Logger.getLogger(LivingColorsCommand.class.getName());
    
    /** The configuration. */
//    private Configuration configuration = ConfigFactory.getConfig();

    /** A name to identify event in controller.xml. */
    private String name;

    private String command;
    
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
     * @param name
     *            the new name
     */
    public void setName(String name) {
        this.name = name;
    }


    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void send() {
       /** 
        * Store the command value temporally and return this value by read method later.
        * It just let the workflow of sending command and read command work.
        * And that status returned by read method must be from some equipment with certain way in real situation.
        * So, the command value needn't be stored and status value is from equipment in real situation.
        */
       colorEquipmentStatus = command;
       logger.info("*********Command " + command + " of protocol livingColors had sent.**********");
       //TODO: The followings in detail is to be finishing.
//        byte[] dataBytes;
//        if (command.equalsIgnoreCase("on")) { //on
//            dataBytes = hexStringToByteArray("05 00 00 00 00 00".replaceAll(" ", "").toLowerCase());
//        } else if (command.equalsIgnoreCase("off")) { //off
//            dataBytes = hexStringToByteArray("07 00 00 00 00 00".replaceAll(" ", "").toLowerCase());
//        } else if (command.equalsIgnoreCase("red")) { //red
//            dataBytes = hexStringToByteArray("03 00 00 ff ff 00".replaceAll(" ", "").toLowerCase());
//        } else if (command.equalsIgnoreCase("green")) { //green
//            dataBytes = hexStringToByteArray("03 00 64 ff ff 00".replaceAll(" ", "").toLowerCase());
//        } else if (command.equalsIgnoreCase("blue")) { //blue
//            dataBytes = hexStringToByteArray("03 00 b8 ff ff 00".replaceAll(" ", "").toLowerCase());
//        } else { //yellow
//            dataBytes = hexStringToByteArray("03 00 32 ff ff 00".replaceAll(" ", "").toLowerCase());
//        }
//        if (configuration.getLivingColorsConnectionType().equalsIgnoreCase("UDP")) {
//            DatagramSocket clientSocket = null;
//            int port = 23000;
//            try {
//                InetAddress addr = InetAddress.getByName(configuration.getLivingColorsServerIp());
//                clientSocket = new DatagramSocket();
//                DatagramPacket sendPacket = new DatagramPacket(dataBytes, dataBytes.length, addr, port);
//                clientSocket.send(sendPacket);
//                if (clientSocket != null) {
//                    clientSocket.close();
//                }
//            } catch (Exception e) {
//                logger.error(e);
//                throw new RuntimeException(e);
//            }
//        } else {
//            try {
//                CommPortIdentifier id = CommPortIdentifier.getPortIdentifier(configuration.getLivingColorsComPort());
//                SerialPort serialPort = (SerialPort) id.open("ORBController", 2000);
//                serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
//                OutputStream outputStream = serialPort.getOutputStream();
//                outputStream.write(dataBytes);
//                outputStream.close();
//                serialPort.close();
//            } catch (Exception e) {
//                logger.error(e);
//                throw new RuntimeException(e);
//            }
//        }
    }
    
    @Override
    public String read(EnumSensorType sensorType, Map<String, String> stateMap) {
       //TODO: Implements the detail of query color and power status from equipment.
       // The following is fake and the status must be from equipment with certain way.
       return colorEquipmentStatus;
    }

    @SuppressWarnings("unused")
   private  byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

}
