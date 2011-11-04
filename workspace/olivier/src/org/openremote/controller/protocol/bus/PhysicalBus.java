package org.openremote.controller.protocol.bus;

import java.io.IOException;
import java.util.Map;

/**
 * The abstraction of a physical bus.
 * <p>
 * This interface describes the entry points of all the physical buses OpenRemote controller uses.
 * </p>
 */
public interface PhysicalBus {
   void configure(Map<String, Object> configuration);
   
   /**
    * Start the physical bus.
    * 
    */
   void start();

   /**
    * Stop the physical bus.
    * 
    * @throws InterruptedException
    */
   void stop();

   /**
    * Send a message to device(s) through the physical bus.
    * 
    * @param message
    *           The message to send.
    * @throws IOException
    */
   void send(Message message) throws IOException;

   /**
    * Receive a message from the physical bus. This method blocks until a message is received.
    * 
    * @return Expected message.
    * @throws IOException
    *            Message could not be received.
    */
   Message receive() throws IOException;
}
