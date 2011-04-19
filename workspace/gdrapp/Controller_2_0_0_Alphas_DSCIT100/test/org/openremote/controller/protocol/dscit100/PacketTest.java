/**
 * 
 */
package org.openremote.controller.protocol.dscit100;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;
import org.openremote.controller.protocol.dscit100.Packet.PacketCallback;

/**
 * @author Greg Rapp
 * 
 */
public class PacketTest
{

  /**
   * Test method for
   * {@link org.openremote.controller.protocol.dscit100.Packet#Packet(java.lang.String, java.lang.String)}
   * .
   */
  @Test
  public void testPacketStringString()
  {
    Packet packet = new Packet("001", "1234");

    assertEquals("001", packet.getCommand());
    assertEquals("1234", packet.getData());
  }

  /**
   * Test method for
   * {@link org.openremote.controller.protocol.dscit100.Packet#Packet(java.lang.String, java.lang.String, org.openremote.controller.protocol.dscit100.Packet.PacketCallback)}
   * .
   */
  @Test
  public void testPacketStringStringPacketCallback()
  {
    Packet packet = new Packet("001", "1234", new PacketCallback()
    {
      @Override
      public void receive(DSCIT100Connection connection, Packet packet)
      {
      }
    });

    assertEquals("001", packet.getCommand());
    assertEquals("1234", packet.getData());
    assertTrue(packet.getCallback() instanceof PacketCallback);
  }

  /**
   * Test method for
   * {@link org.openremote.controller.protocol.dscit100.Packet#Packet(java.lang.String)}
   * .
   */
  @Test
  public void testPacketString()
  {
    Packet packet = null;
    try
    {
      packet = new Packet("50000126");
    }
    catch (IOException e)
    {
    }

    assertEquals("500", packet.getCommand());
    assertEquals("001", packet.getData());
  }

}
