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

import org.apache.log4j.Logger;

public class ElexolCommand implements ExecutableCommand {

    /**
     * Logging. Use common Elexol USB log category.
     */
    private static Logger log = Logger.getLogger(ElexolCommandBuilder.ELEXOL_USB_LOG_CATEGORY);

    private ElexolUsbDevice device = null;
    private CommandType command;
    private PortType    port;
    private PinType     pin;
    private Integer     duration;

    public ElexolCommand(String usbPort, PortType ioPort, PinType pinNumber, CommandType command, Integer duration)
    {
	this.device = DeviceManager.GetDevice(usbPort);
	this.command = command;
	this.port = ioPort;
	this.pin = pinNumber;
        this.duration = duration;
    }
	
    @Override
    public void send() {

	this.device.Send(this.command, this.port, this.pin, this.duration);

	log.warn("ElexolCommand Sent");
    }
}
