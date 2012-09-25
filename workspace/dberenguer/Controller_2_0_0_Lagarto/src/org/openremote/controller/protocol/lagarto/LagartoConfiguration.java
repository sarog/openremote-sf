/**
 * Copyright (c) 2012 Daniel Berenguer <dberenguer@usapiens.com>
 *
 * This file is part of the lagarto project.
 *
 * lagarto  is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * lagarto is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with panLoader; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301
 * USA
 *
 *  @author Daniel Berenguer
 *  @date   2012-09-15
 */
package org.openremote.controller.protocol.lagarto;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import org.openremote.controller.Configuration;

/**
 * Lagarto configuration class
 */
public class LagartoConfiguration extends Configuration
{
  /**
  * Path to the config file
  */
  private final static String CONFIG_FILE_PATH = "lagartoclient.properties";

  /**
  * Configuration file
  */
  private Properties configFile;

  /**
  * Class constructor
  */
  public LagartoConfiguration() throws LagartoException
  {
    configFile = new Properties();

    try
    {
      configFile.load(new FileInputStream(CONFIG_FILE_PATH));
    } catch (IOException ex)
    {
      throw new LagartoException("Unable to read config file: " + CONFIG_FILE_PATH);
    }
  }

  /**
  * Save configuration file
  */
  public void save() throws LagartoException
  {
    try
    {
      configFile.store(new FileOutputStream("CONFIG_FILE_PATH"), null);
    } catch (IOException e) {
      throw new LagartoException("Unable to save config file: " + CONFIG_FILE_PATH);
    }
  }

  /**
  * Get Lagarto broadcast address
  *
  * @return broadcast address
  */
  public String getBroadcastAddr()
  {
    return configFile.getProperty("broadcast", "tcp://127.0.0.1:5001");
  }
}
