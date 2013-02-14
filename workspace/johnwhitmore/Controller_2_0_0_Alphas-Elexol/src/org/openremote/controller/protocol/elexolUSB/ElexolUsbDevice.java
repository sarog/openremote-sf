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
package org.openremote.controller.protocol.elexolUSB;

import org.openremote.controller.exception.NoSuchCommandException;

import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.UnsupportedCommOperationException;
import gnu.io.NoSuchPortException;
import gnu.io.SerialPort;

import org.apache.log4j.Logger;

public class ElexolUsbDevice {

  /**
   * Logging. Use common Elexol USB log category.
   */
  private Logger log = ElexolCommandBuilder.log;

  private CommPortIdentifier comPortID = null;

  private boolean error = false;

  private SerialPort serialPort = null; 
  private OutputStream outputStream = null;
  private InputStream inputStream = null;
  private final Object serialLock = new Object();

  private byte mask;
  private PortType port;

  private static byte portA = 0x00;
  private static byte portB = 0x00;
  private static byte portC = 0x00;

  public ElexolUsbDevice(CommPortIdentifier comPortID)
  {
    this.comPortID = comPortID;

    try
    {
      byte[] buffer = new byte[1024];
      int len = -1;

      serialPort = (SerialPort) comPortID.open("USBIO24", 2000);

      serialPort.setSerialPortParams(9600, 
				     SerialPort.DATABITS_8, 
				     SerialPort.STOPBITS_1,
				     SerialPort.PARITY_NONE);

      outputStream = serialPort.getOutputStream();
      inputStream = serialPort.getInputStream();

      outputStream.write("?".getBytes("UTF8"));
      Thread.sleep(250); // do nothing for 250 miliseconds

      while (inputStream.available() > 0)
      {
	len = inputStream.read(buffer);
	String response = new String(buffer,0,len);
	if(!response.equals("USB I/O 24f2\r\n"))
	{
	  error = true;
	  serialPort.close();
	  comPortID = null;
	  serialPort = null; 
	  outputStream = null;
	  InputStream inputStream = null;

	  throw new NoSuchCommandException("USB port is not Elexol USB.");
	}
      }

      // Initialise all 3 ports to initial value
      outputStream.write("!A".getBytes("UTF8"));
      outputStream.write(0x00);
      outputStream.write("A".getBytes("UTF8"));
      outputStream.write(portA);

      outputStream.write("!B".getBytes("UTF8"));
      outputStream.write(0x00);
      outputStream.write("B".getBytes("UTF8"));
      outputStream.write(portB);

      outputStream.write("!C".getBytes("UTF8"));
      outputStream.write(0x00);
      outputStream.write("C".getBytes("UTF8"));
      outputStream.write(portC);
    }
    catch (IOException e) 
    {
      log.warn("ElexolUsbDevice IOException");
      throw new NoSuchCommandException("USB port is not Elexol USB.");
    }
    catch (UnsupportedCommOperationException e) 
    {
      log.warn("ElexolUsbDevice UnsupportedCommOperationException");
      throw new NoSuchCommandException("USB port is not Elexol USB.");
    }
    catch (PortInUseException e)
    {
      log.warn("ElexolUsbDevice PortInUseException");
      throw new NoSuchCommandException("USB port is not Elexol USB.");
    }
    catch (InterruptedException e) 
    {
      log.warn("ElexolUsbDevice InterruptedException");
      throw new NoSuchCommandException("USB port is not Elexol USB.");
    }
  }

  public CommPortIdentifier getComPortID()
  {
    return(this.comPortID);
  }

  void Send(CommandType command, PortType port, PinType pin, Integer duration)
  {
    byte value = 0x00;

    log.debug("SerialPortManager Send");
	
    if(serialPort != null)
    {
      log.debug("SerialPortManager Serial port Open");

      synchronized(serialLock)
      {
	log.debug("SerialPortManager Sync'd");
	try
	{
	  /*
	   * work out the new value to write to the port
	   */
	  this.port = port;
	  mask = (byte)(0x01 << (pin.toByte() - 1));
		    
	  if(  (command == CommandType.SWITCH_ON)
	     ||(command  == CommandType.PULSE)  )
	  {
	    if(port == PortType.PORT_A)
	    {
	      this.portA |= mask;
	      value = this.portA;
	    }
	    else if(port == PortType.PORT_B)
	    {
	      this.portB |= mask;
	      value = this.portB;
	    }
	    else if(port == PortType.PORT_C)
	    {
	      this.portC |= mask;
	      value = this.portC;
	    }
	  }
	  else if(command  == CommandType.SWITCH_OFF)
	  {
	    if(port == PortType.PORT_A)
	    {
		this.portA &= ~mask;
		value = this.portA;
	    }
	    else if(port == PortType.PORT_B)
	    {
	      this.portB &= ~mask;
	      value = this.portB;
	    }
	    else if(port == PortType.PORT_C)
	    {
	      this.portC &= ~mask;
	      value = this.portC;
	    }
	  }

	  /*
	   * Write new command to the port
	   */
	  log.debug("SerialPortManager write to port " + port.toString());
	  outputStream.write(port.toString().getBytes("UTF8"));
	  outputStream.write(value);

	  if(command  == CommandType.PULSE)
	  {
	    try
	    {
	      Thread.sleep(duration);  
	    } 
	    catch (InterruptedException ie)
	    {
	      log.error("ElexolUsbDevice Interrupted!");
	    }

	    if(port == PortType.PORT_A)
	    {
	      this.portA &= ~mask;
	      value = this.portA;
	    }
	    else if(port == PortType.PORT_B)
	    {
	      this.portB &= ~mask;
	      value = this.portB;
	    }
	    else if(port == PortType.PORT_C)
	    {
	      this.portC &= ~mask;
	      value = this.portC;
	    }

	    outputStream.write(port.toString().getBytes("UTF8"));
	    outputStream.write(value);
	  }
	}
	catch (UnsupportedEncodingException e) 
	{
	  log.error("SerialPortManager UnsupportedEncodingException");
	}
	catch (IOException e) 
	{
	  log.error("SerialPortManager IOException");
	}
      }
      log.error("SerialPortManager Finished Sync Section");
    }
    else
    {
      log.error("SerialPortManager Serial port not open");
    }
  }
}
