package org.openremote.controller.protocol.elexolUSB;
//package org.openremote.controller.protocol.x10;

// import java.util.Map;
// import java.io.OutputStream;
// import java.io.InputStream;
// import java.io.UnsupportedEncodingException;
// import java.io.IOException;
// import java.lang.InterruptedException;

// import gnu.io.PortInUseException;
// import gnu.io.UnsupportedCommOperationException;
// import gnu.io.NoSuchPortException;
// import gnu.io.CommPortIdentifier;
// import gnu.io.SerialPort;

import org.openremote.controller.command.ExecutableCommand;
import org.openremote.controller.exception.NoSuchCommandException;

public class ElexolCommand implements ExecutableCommand {

    private CommandType command;
    private PortType    port;
    private PinType     pin;
    private SerialPortManager USBPort;

    public ElexolCommand(SerialPortManager USBPort, CommandType command, PortType port, PinType pin)
    {
	this.USBPort = USBPort;
	this.command = command;
	this.port = port;
	this.pin = pin;
    }
	
    @Override
    public void send() {

	this.USBPort.Send(this.command, this.port, this.pin);

    }
}
