/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2012, OpenRemote Inc.
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
package org.openremote.controller.protocol.knx;

import java.util.Deque;
import java.util.ArrayDeque;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.InetAddress;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;

import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;
import org.openremote.controller.protocol.bus.PhysicalBus;
import org.openremote.controller.protocol.bus.Message;
import org.openremote.controller.protocol.knx.datatype.DataPointType;
import org.openremote.controller.protocol.knx.ip.message.IpMessage;
import org.openremote.controller.protocol.knx.ip.message.IpDiscoverReq;
import org.openremote.controller.protocol.knx.ip.message.IpDiscoverResp;
import org.openremote.controller.protocol.knx.ip.message.Hpai;
import org.openremote.controller.protocol.knx.ip.message.IpConnectResp;
import org.openremote.controller.protocol.knx.ip.message.IpConnectReq;
import org.openremote.controller.protocol.knx.ip.message.IpConnectionStateReq;
import org.openremote.controller.protocol.knx.ip.message.IpConnectionStateResp;
import org.openremote.controller.protocol.knx.ip.message.IpTunnelingReq;
import org.openremote.controller.protocol.knx.ip.message.IpTunnelingAck;
import org.openremote.controller.protocol.knx.dib.DeviceInformation;
import org.openremote.controller.protocol.knx.dib.SupportedServiceFamily;
import org.openremote.controller.command.Command;
import org.openremote.controller.command.CommandBuilder;
import org.openremote.controller.command.CommandParameter;
import org.openremote.controller.utils.Strings;
import org.openremote.controller.utils.Logger;
import org.openremote.controller.exception.ConversionException;
import org.openremote.controller.component.EnumSensorType;
import org.jdom.Element;

/**
 * TODO
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class RangeReadTest
{


  // Test Setup -----------------------------------------------------------------------------------

  private KNXCommandBuilder builder = null;

  @Before public void setUp()
  {
    builder = new KNXCommandBuilder(null, 0, RangeReadMockGateway.class.getName());
  }


  // Tests ----------------------------------------------------------------------------------------


  @Test public void testBuild() throws Exception
  {
    GroupAddress addr = new GroupAddress("3/3/3");

    Command cmd = getCommand("STATUS", addr, DataPointType.VALUE_1_UCOUNT);

    Assert.assertTrue(cmd instanceof GroupValueRead);

    GroupValueRead knx = (GroupValueRead)cmd;


    Thread.sleep(3000);

    String returnValue = knx.read(EnumSensorType.RANGE, null);

    Assert.assertTrue(
        "Expected '100', got '" + returnValue + "'.",
        returnValue.equals("100")
    );
  }


  @Test public void testLevel() throws Exception
  {
    GroupAddress addr = new GroupAddress("3/3/3");

    Command cmd = getCommand("STATUS", addr, DataPointType.VALUE_1_UCOUNT);

    Assert.assertTrue(cmd instanceof GroupValueRead);

    GroupValueRead knx = (GroupValueRead)cmd;


    Thread.sleep(3000);

    String returnValue = knx.read(EnumSensorType.LEVEL, null);

    Assert.assertTrue(returnValue.equals(Integer.toString((int)(100 / 2.55))));
  }


  @Test public void testLevelRounding() throws Exception
  {
    GroupAddress addr = new GroupAddress("3/4/1");

    Command cmd = getCommand("STATUS", addr, DataPointType.SCALING);

    Assert.assertTrue(cmd instanceof GroupValueRead);

    GroupValueRead knx = (GroupValueRead)cmd;


    Thread.sleep(3000);

    String returnValue = knx.read(EnumSensorType.LEVEL, null);

    Assert.assertTrue(returnValue.equals("0"));



    addr = new GroupAddress("3/4/2");

    cmd = getCommand("STATUS", addr, DataPointType.SCALING);

    knx = (GroupValueRead)cmd;

    returnValue = knx.read(EnumSensorType.LEVEL, null);

    Assert.assertTrue(returnValue.equals("1"));



    addr = new GroupAddress("3/4/10");

    cmd = getCommand("STATUS", addr, DataPointType.SCALING);

    knx = (GroupValueRead)cmd;

    returnValue = knx.read(EnumSensorType.LEVEL, null);

    Assert.assertTrue(returnValue.equals("4"));



    addr = new GroupAddress("3/4/139");

    cmd = getCommand("STATUS", addr, DataPointType.SCALING);

    knx = (GroupValueRead)cmd;

    returnValue = knx.read(EnumSensorType.LEVEL, null);

    Assert.assertTrue(returnValue.equals("55"));


  }




  // Helpers --------------------------------------------------------------------------------------

  private Command getCommand(String cmd, GroupAddress groupAddress, DataPointType dpt)
  {
    return getCommand(cmd, GroupAddress.formatToMainMiddleSub(groupAddress.asByteArray()), dpt);
  }

  private Command getCommand(String cmd, String groupAddress, DataPointType dpt)
  {
    Element ele = new Element("command");
    ele.setAttribute("id", "test");
    ele.setAttribute(CommandBuilder.PROTOCOL_ATTRIBUTE_NAME, "knx");

    Element propAddr = new Element(CommandBuilder.XML_ELEMENT_PROPERTY);
    propAddr.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_NAME,
                          KNXCommandBuilder.KNX_XMLPROPERTY_GROUPADDRESS);
    propAddr.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_VALUE,
                          groupAddress);

    ele.addContent(propAddr);

    Element propAddr2 = new Element(CommandBuilder.XML_ELEMENT_PROPERTY);
    propAddr2.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_NAME,
                           KNXCommandBuilder.KNX_XMLPROPERTY_COMMAND);
    propAddr2.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_VALUE,
                           cmd);

    ele.addContent(propAddr2);

    Element propAddr3 = new Element(CommandBuilder.XML_ELEMENT_PROPERTY);
    propAddr3.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_NAME,
                           KNXCommandBuilder.KNX_XMLPROPERTY_DPT);
    propAddr3.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_VALUE,
                           dpt.getDPTID());

    ele.addContent(propAddr3);


    return builder.build(ele);
  }


  // Nested Classes -------------------------------------------------------------------------------

  public static class RangeReadMockGateway implements PhysicalBus
  {


    Logger log = Logger.getLogger(KNXCommandBuilder.KNX_LOG_CATEGORY);

    
    BlockingQueue<IpMessage> responseQueue = new LinkedBlockingQueue<IpMessage>();

    int seqCounter = 0;


    @Override public void start(Object a, Object b)
    {

    }

    @Override public void stop()
    {

    }

    @Override public void send(Message msg)
    {
      byte[] content = msg.getContent();

      Assert.assertTrue(IpMessage.isValidFrame(content));
      
      if (IpDiscoverReq.isSearchRequest(content))
      {
        try
        {
          log.info("--- GOT SEARCH REQUEST ---");


          InetSocketAddress address = new InetSocketAddress("1.2.3.4", 1234);

          Map<ServiceTypeIdentifier.Family, Integer> services =
              new HashMap<ServiceTypeIdentifier.Family, Integer>();
          services.put(ServiceTypeIdentifier.Family.CORE, 10);

          Hpai serverControlHPAI = new Hpai(address);

          DeviceInformation deviceInfo = new DeviceInformation(
              "Test Gateway", DeviceInformation.KNXMedium.TP0, new IndividualAddress(0x101),
              NetworkInterface.getByInetAddress(InetAddress.getByName("127.0.0.1")),
              1, 1, 1
          );

          SupportedServiceFamily supportedServices = new SupportedServiceFamily(services);

          responseQueue.add(new IpDiscoverResp(serverControlHPAI, deviceInfo, supportedServices));
        }

        catch (Throwable t)
        {
          Assert.fail(t.getMessage());
        }
      }

      else if (IpConnectReq.isConnectRequest(content))
      {
        log.info("--- GOT CONNECT REQUEST ---");

        responseQueue.add(
            new IpConnectResp(
                1, IpConnectResp.Status.NO_ERROR,
                new Hpai(new InetSocketAddress("127.0.0.1", 4444)),
                new IndividualAddress(0x0101)
            )
        );

        seqCounter = 0;
      }

      else if (IpConnectionStateReq.isConnectionStateRequest(content))
      {
        log.info("--- GOT CONNECT STATE REQUEST ---");

        responseQueue.add(new IpConnectionStateResp(1, IpConnectionStateResp.Status.NO_ERROR));  
      }

      else if (IpTunnelingReq.isTunnelingRequest(content))
      {
        responseQueue.add(new IpTunnelingAck(1, seqCounter++, 0x00 /*status*/));

        ByteArrayInputStream in = new ByteArrayInputStream(content, 6, content.length - 6);

        try
        {

          IpTunnelingReq tunnelingRequest = new IpTunnelingReq(in, content[6] + 9 + content[15]);

          byte[] cemiFrame = tunnelingRequest.getcEmiFrame();

          CommonEMI cemi = new CommonEMI(cemiFrame);

          GroupAddress addr = cemi.getDestinationAddress();

          CommandParameter param = null;

          if (addr.equals(new GroupAddress("3/3/3")))
          {
            param = new CommandParameter("100");
          }

          else if (addr.getMainGroup() == 3 && addr.getMiddleGroup() == 4)
          {
            param = new CommandParameter(Integer.toString(addr.getSubGroup()));
          }

          cemi = new CommonEMI(DataLink.MessageCode.DATA_INDICATE_BYTE, addr,
                               ApplicationProtocolDataUnit.createRange(param));

          tunnelingRequest = new IpTunnelingReq(1, seqCounter++, cemi.getFrameStructure());

          responseQueue.add(tunnelingRequest);
        }

        catch (IOException e)
        {
          Assert.fail(e.getMessage());
        }

        catch (InvalidGroupAddressException e)
        {
          Assert.fail(e.getMessage());
        }

        catch (ConversionException e)
        {
          Assert.fail(e.getMessage());
        }
      }

      else
      {
        StringBuilder builder = new StringBuilder();
        builder.append("what's this : ");

        for (byte b : content)
        {
          builder.append(Strings.byteToUnsignedHexString(b));
          builder.append(" ");
        }

        System.out.println(builder);
      }
    }

    @Override public Message receive() throws IOException
    {

      IpMessage response;

      try
      {
        response = responseQueue.poll(5, TimeUnit.MINUTES);

        if (response != null)
        {

          ByteArrayOutputStream bytes = new ByteArrayOutputStream();
          BufferedOutputStream bout = new BufferedOutputStream(bytes);

          response.write(bout);
          bout.flush();
          bout.close();
          

          return new Message(bytes.toByteArray());
        }

        else
        {
          throw new IOException("no response");
        }
      }

      catch (InterruptedException e)
      {
        throw new IOException("interrupted");
      }
    }
  }

}

