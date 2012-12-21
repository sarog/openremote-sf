package org.openremote.controller.protocol.elexolUSB;
//package org.openremote.controller.protocol.x10;

import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import gnu.io.PortInUseException;
import gnu.io.UnsupportedCommOperationException;
import gnu.io.NoSuchPortException;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

public class SerialPortManager extends Thread {

    private static boolean error = false;
    private static CommPortIdentifier comPortID = null;
    private static SerialPort serialPort = null; 
    private static OutputStream outputStream = null;
    private static InputStream inputStream = null;
    private static final Object serialLock = new Object();

    private byte mask;
    private PortType port;

    private static byte portA = 0x00;
    private static byte portB = 0x00;
    private static byte portC = 0x00;

    public SerialPortManager()
    {
	/*
	 * Check is the Serial Connection set up and if not set it up
	 */
	if (comPortID == null){
	    try
	    {
		byte[] buffer = new byte[1024];
		int len = -1;

		comPortID = CommPortIdentifier.getPortIdentifier("/dev/ttyUSB0");
		serialPort = (SerialPort) comPortID.open("USBIO24", 2000);

		serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
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
		    }
		    else
		    {
			error = false;
		    }
		}

		if(error)
	        {
		    System.out.print("Error response was not for a USB-IO-24 Module\n\r");
		}

		if(!error)
	        {
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
	    }
	    catch (IOException e) {
		e.printStackTrace();
	    }
	    catch (UnsupportedCommOperationException e) {
		e.printStackTrace();
	    }
	    catch (NoSuchPortException e) {
		e.printStackTrace();
	    }
	    catch (PortInUseException e) {
		e.printStackTrace();
	    }
	    catch (InterruptedException e) {
		e.printStackTrace();
	    }
	}
    }

    void Send(CommandType command, PortType port, PinType pin){

	byte value = 0x00;

	// System.out.print("USBPortManagerSend");
	// if(command == ElexolUSBCommandType.SWITCH_ON){
	//     System.out.print("On");
	// }
	// else if(command == ElexolUSBCommandType.SWITCH_ON){
	//     System.out.print("Off");
	// }
	// System.out.print("Port " + port);
	// System.out.print("Pin" + Byte.toString(pin));
	
	if(serialPort != null){
	    synchronized(serialLock){
		try{
		    /*
		     * work out the new value to write to the port
		     */
		    this.port = port;
		    mask = (byte)(0x01 << (pin.toByte() - 1));
		    
		    if(  (command == CommandType.SWITCH_ON)
		       ||(command  == CommandType.PULSE)  ){
			if(port == PortType.PORT_A){
			    this.portA |= mask;
			    value = this.portA;
			}
			else if(port == PortType.PORT_B){
			    this.portB |= mask;
			    value = this.portB;
			}
			else if(port == PortType.PORT_C){
			    this.portC |= mask;
			    value = this.portC;
			}
		    }
		    else if(command  == CommandType.SWITCH_OFF){
			if(port == PortType.PORT_A){
			    this.portA &= ~mask;
			    value = this.portA;
			}
			else if(port == PortType.PORT_B){
			    this.portB &= ~mask;
			    value = this.portB;
			}
			else if(port == PortType.PORT_C){
			    this.portC &= ~mask;
			    value = this.portC;
			}
		    }

		    /*
		     * Write new command to the port
		     */
		    outputStream.write(port.toString().getBytes("UTF8"));
		    outputStream.write(value);

		    if(command  == CommandType.PULSE){
			try{
			    Thread.sleep(50);  
			} catch (InterruptedException ie){
			    System.out.println(ie.getMessage());
			}

			if(port == PortType.PORT_A){
			    this.portA &= ~mask;
			    value = this.portA;
			}
			else if(port == PortType.PORT_B){
			    this.portB &= ~mask;
			    value = this.portB;
			}
			else if(port == PortType.PORT_C){
			    this.portC &= ~mask;
			    value = this.portC;
			}

			outputStream.write(port.toString().getBytes("UTF8"));
			outputStream.write(value);
			//this.start();
		    }
		}
		catch (UnsupportedEncodingException e) {
		    e.printStackTrace();
		}
		catch (IOException e) {
		    e.printStackTrace();
		}
	    }
	}
	else {
	    System.out.println("Serial port not open");
	}
    }

    public void run() {
	/*
	 * end the pulse that was started
	 */
    }
}
