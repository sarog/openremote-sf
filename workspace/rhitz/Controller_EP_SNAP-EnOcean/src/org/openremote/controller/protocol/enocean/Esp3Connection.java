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
package org.openremote.controller.protocol.enocean;

import org.openremote.controller.protocol.enocean.packet.*;
import org.openremote.controller.protocol.enocean.packet.command.Esp3RdIDBaseCommand;
import org.openremote.controller.protocol.enocean.packet.radio.Esp34BSTelegram;
import org.openremote.controller.protocol.enocean.packet.radio.Esp31BSTelegram;
import org.openremote.controller.protocol.enocean.packet.radio.Esp3RPSTelegram;
import org.openremote.controller.protocol.enocean.packet.radio.EspRadioTelegram;
import org.openremote.controller.utils.Logger;

/**
 * EnOcean connection implementation based on the EnOcean serial protocol 3 (ESP3).
 *
 * @author Rainer Hitz
 */
public class Esp3Connection implements EnOceanConnection, Esp3ProcessorListener
{

  // Class Members --------------------------------------------------------------------------------

  /**
   * EnOcean logger. Uses a common category for all EnOcean related logging.
   */
  private final Logger log = Logger.getLogger(EnOceanCommandBuilder.ENOCEAN_LOG_CATEGORY);


  // Private Instance Fields ----------------------------------------------------------------------

  /**
   * Object for handling the EnOcean serial protocol 3 (ESP3).
   */
  private EspProcessor<Esp3Packet> processor;

  /**
   * Listener which will be notified of received radio telegrams.
   */
  private RadioTelegramListener radioListener;

  /**
   * EnOcean module base device ID.
   *
   * @see DeviceID
   */
  private DeviceID baseID;

  // Constructors ---------------------------------------------------------------------------------

  /**
   * Constructs an EnOcean serial protocol 3 (ESP3) based connection with given
   * serial protocol processor and radio telegram listener.
   *
   * @param processor   processor for handling the EnOcean serial protocol 3 (ESP3)
   *
   * @param listener    listener which will be notified of received radio telegrams
   */
  public Esp3Connection(EspProcessor<Esp3Packet> processor, RadioTelegramListener listener)
  {
    if(processor == null)
    {
      throw new IllegalArgumentException("null processor");
    }

    this.processor = processor;
    this.radioListener = listener;
  }


  // Implements EnOceanConnection -----------------------------------------------------------------

  /**
   * {@inheritDoc}
   */
  @Override public void connect() throws ConnectionException, ConfigurationException
  {
    processor.start();

    Esp3RdIDBaseCommand readBaseIDCmd = new Esp3RdIDBaseCommand();

    try
    {
      readBaseIDCmd.send(processor);
    }
    catch (InterruptedException e)
    {
      throw new ConnectionException("Interrupted while connecting to EnOcean module.");
    }

    this.baseID = readBaseIDCmd.getBaseID();
  }

  /**
   * {@inheritDoc}
   */
  @Override public void disconnect() throws ConnectionException
  {
    processor.stop();
  }

  /**
   * {@inheritDoc}
   */
  @Override public void sendRadio(EspRadioTelegram.RORG rorg, DeviceID deviceID, byte[] payload, byte statusByte)
      throws ConnectionException, ConfigurationException
  {
    if(baseID == null)
    {
      throw new IllegalStateException("Connect hasn't been successfully called.");
    }

    try
    {
      deviceID = deviceID.resolve(baseID);
    }
    catch (InvalidDeviceIDException e)
    {
      throw new ConfigurationException(e.getMessage(), e);
    }

    Esp3Request radioTelegram = null;

    if(rorg == EspRadioTelegram.RORG.RPS ||
       rorg == EspRadioTelegram.RORG.RPS_ESP2)
    {
      radioTelegram = new Esp3RPSTelegram(deviceID, payload[0], statusByte);
    }

    else if(rorg == EspRadioTelegram.RORG.BS1 ||
            rorg == EspRadioTelegram.RORG.BS1_ESP2)
    {
      radioTelegram = new Esp31BSTelegram(deviceID, payload[0], statusByte);
    }

    else if(rorg == EspRadioTelegram.RORG.BS4 ||
            rorg == EspRadioTelegram.RORG.BS4_ESP2)
    {
      radioTelegram = new Esp34BSTelegram(deviceID, payload, statusByte);
    }

    else
    {
      throw new RuntimeException("Unhandled radio telegram type '" + rorg +"'.");
    }


    if(radioTelegram != null)
    {
      try
      {
        radioTelegram.send(processor);
      }

      catch (InterruptedException e)
      {
        throw new ConnectionException("Interrupted while sending radio telegram.");
      }
    }
  }

  // Implements Esp3ProcessorListener -------------------------------------------------------------

  /**
   * {@inheritDoc}
   */
  @Override public void radioTelegramReceived(EspRadioTelegram telegram)
  {
    if(radioListener != null)
    {
      radioListener.radioTelegramReceived(telegram);
    }
  }
}
