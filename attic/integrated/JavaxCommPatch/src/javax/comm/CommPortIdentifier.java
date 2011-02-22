/*
 * CommPortIdentifier.java
 * Copyright (C) 2004 The Free Software Foundation
 *
 * This file is part of GNU CommAPI, a library.
 *
 * GNU CommAPI is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * GNU CommAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * As a special exception, if you link this library with other files to
 * produce an executable, this library does not by itself cause the
 * resulting executable to be covered by the GNU General Public License.
 * This exception does not however invalidate any other reasons why the
 * executable file might be covered by the GNU General Public License.
 */
package javax.comm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * A handle for obtaining a communications port.
 *
 * @author <a href='mailto:dog@gnu.org'>Chris Burdess</a>
 * @author <a href='mailto:juha@openremote.org'>Juha Lindfors</a>
 *
 * @version 2.0.3-OR1
 */
public class CommPortIdentifier
{

  /**
   * RS-232 serial port.
   */
  public static final int PORT_SERIAL = 1;
  
  /**
   * IEEE 1284 parallel port.
   */
  public static final int PORT_PARALLEL = 2;
  
  /**
   * List of all CommPortIdentifiers.
   */
  static List allIds;
  
  /**
   * Dictionary of CommPortIdentifiers by name.
   */
  static Map idByName;
  
  /**
   * Dictionary of CommPortIdentifiers by open port.
   */
  static Map idByPort;
  
  /**
   * Bootstrap.
   */
  static
  {
    allIds = new ArrayList();
    idByName = new HashMap();
    idByPort = new HashMap();
    
    // Load drivers
    String props = new StringBuffer(System.getProperty("java.home"))
      .append(File.separatorChar)
      .append("lib")
      .append(File.separatorChar)
      .append("javax.comm.properties")
      .toString();

    String commPropsOverride = System.getProperty("comm.properties");

    if (commPropsOverride != null)
    {
      props = commPropsOverride;
    }

    try
    {
      List lines = tokenize(props);
      for (Iterator i = lines.iterator(); i.hasNext(); )
      {
        String line = (String)i.next();
        int ei = line.indexOf('=');
        if (ei != -1)
        {
          String key = line.substring(0, ei).trim();
          if ("driver".equalsIgnoreCase(key))
          {
            // Instantiate and initialise driver
            String classname = line.substring(ei + 1).trim();
            try
            {
              Class driverClass = Class.forName(classname);
              CommDriver driver = 
                (CommDriver)driverClass.newInstance();
              driver.initialize();
            }
            catch (ClassNotFoundException e)
            {
              System.err.println("Driver class not found: " +
                  classname);
            }
            catch (Exception e)
            {
              System.err.println("Caught " + e +
                  "loading driver " + classname);
            }
          }
        }
      }
    }
    catch (IOException e)
    {
      e.printStackTrace(System.err);
      // FIXME what now?
    }
  }

  /**
   * Retrieve a list of lines in the specified file.
   * This strips whitespace and ignores any empty lines and lines with a
   * leading hash ('#').
   */
  static List tokenize(String file)
    throws IOException
  {
    BufferedReader in = null;
    List lines = new ArrayList();
    try
    {
      in = new BufferedReader(new FileReader(file));
      for (String line = in.readLine(); line != null;
          line = in.readLine())
      {
        line = line.trim();
        if (line.length() > 0 && line.charAt(0) != '#')
          lines.add(line);
      }
    }
    finally
    {
      if (in != null)
        in.close();
    }
    return lines;
  }
  
  /**
   * Returns an enumeration of all port identifiers in the system.
   */
  public static Enumeration getPortIdentifiers()
  {
    synchronized (CommPortIdentifier.class)
    {
      return new IteratorEnumeration(allIds.iterator());
    }
  }
  
  /**
   * Returns the port identifier for the specified name.
   * @param portName the name of the port
   * @exception NoSuchPortException if the specified port does not exist
   */
  public static CommPortIdentifier getPortIdentifier(String portName)
    throws NoSuchPortException
  {
    synchronized (CommPortIdentifier.class)
    {
      CommPortIdentifier id = (CommPortIdentifier)idByName.get(portName);
      if (id == null)
        throw new NoSuchPortException();
      return id;
    }
  }
  
  /**
   * Returns the port identifier for the specified port.
   * @param port the open port
   * @exception NoSuchPortException if the specified port is invalid
   */
  public static CommPortIdentifier getPortIdentifier(CommPort port)
    throws NoSuchPortException
  {
    synchronized (CommPortIdentifier.class)
    {
      CommPortIdentifier id = (CommPortIdentifier)idByPort.get(port);
      if (id == null)
        throw new NoSuchPortException();
      return id;
    }
  }
  
  /**
   * Adds the specified port name to the list of available ports.
   * @param portName the port name
   * @param portType PORT_SERIAL or PORT_PARALLEL
   * @param driver the driver controlling the specified port
   */
  public static void addPortName(String portName, int portType,
      CommDriver driver)
  {
    synchronized (CommPortIdentifier.class)
    {
      CommPortIdentifier id = new CommPortIdentifier(portName, portType,
          driver);
      allIds.add(id);
      idByName.put(portName, id);
    }
  }
  
  // -- Instance scope --
  
  /**
   * The name of this port identifier.
   */
  String name;
  
  /**
   * The port type of this identifier (PORT_SERIAL or PORT_PARALLEL).
   */
  int portType;
  
  /**
   * The driver that controls this port.
   */
  CommDriver driver;
  
  /**
   * The ownership listeners for this port.
   */
  List listeners;
  
  /**
   * Is this port currently owned?
   */
  boolean isOwned;
  
  /**
   * If so, by whom?
   */
  String owner;
  
  /**
   * The open communications port associated with this identifier.
   */
  CommPort port;
  
  /**
   * Constructor.
   * @param name the port name
   * @param portType PORT_SERIAL or PORT_PARALLEL
   * @param driver the driver for this port
   */
  CommPortIdentifier(String name, int portType, CommDriver driver)
  {
    this.name = name;
    this.portType = portType;
    this.driver = driver;
    listeners = new ArrayList();
    isOwned = false;
    owner = null;
    port = null;
  }
  
  /**
   * Returns the port name, typically an OS-dependent hardware label.
   */
  public String getName()
  {
    return name;
  }
  
  /**
   * Returns the port type, PORT_SERIAL or PORT_PARALLEL.
   */
  public int getPortType()
  {
    return portType;
  }
  
  /**
   * Returns the current owner of this port.
   */
  public String getCurrentOwner()
  {
    return owner;
  }
  
  /**
   * Indicates whether this port is currently owned.
   */
  public boolean isCurrentlyOwned()
  {
    return isOwned;
  }
  
  /**
   * Opens this communications port.
   * If the port is in use by another application, it will be notified
   * with a PORT_OWNERSHIP_REQUESTED event. If the current owner closes
   * the port, then this method will succeed. Otherwise a
   * PortInUseException will be thrown.
   * @param appName the name of the application requesting the port
   * @param timeout number of miliseconds to wait for the port to open
   */
  public CommPort open(String appname, int timeout)
    throws PortInUseException
  {
    // Handle ownership contention
    if (isOwned)
    {
      fireOwnershipChange(CommPortOwnershipListener.PORT_OWNERSHIP_REQUESTED);
      try
      {
        wait(timeout);
      }
      catch (InterruptedException e)
      {
        // TODO ?
        return null;
      }
      if (isOwned)
        throw new PortInUseException();
    }
    port = driver.getCommPort(name, portType);
    // TODO what if driver returns null?
    isOwned = true;
    owner = appname;
    fireOwnershipChange(CommPortOwnershipListener.PORT_OWNED);
    return port;
  }
  
  /**
   * Opens this port using a file descriptor.
   * @param fd the file descriptor
   * @exception UnsupportedCommOperationException if the platform does not
   * support this functionality
   */
  public CommPort open(FileDescriptor fd)
    throws UnsupportedCommOperationException
  {
    // TODO
    throw new UnsupportedCommOperationException();
  }
  
  /**
   * Adds the specified listener to the list of ownership listeners for
   * this port.
   * @param listener the ownership listener to add
   */
  public void addPortOwnershipListener(CommPortOwnershipListener listener)
  {
    synchronized (listeners)
    {
      if (!listeners.contains(listener))
        listeners.add(listener);
    }
  }
  
  /**
   * Removes the specified listener from the list of ownership listeners for
   * this port.
   * @param listener the ownership listener to remove
   */
  public void removePortOwnershipListener(CommPortOwnershipListener listener)
  {
    synchronized (listeners)
    {
      listeners.remove(listener);
    }
  }
  
  /**
   * Propagate an ownership change to onwership listeners.
   */
  void fireOwnershipChange(int type)
  {
    CommPortOwnershipListener[] l = null;
    synchronized (listeners)
    {
      l = new CommPortOwnershipListener[listeners.size()];
      listeners.toArray(l);
    }
    for (int i = 0; i < l.length; i++)
      l[i].ownershipChange(type);
  }
  
  /**
   * The CommPort object was closed.
   */
  void portClosed()
  {
    port = null;
    isOwned = false;
    owner = null;
    fireOwnershipChange(CommPortOwnershipListener.PORT_UNOWNED);
  }
  
  /*
   * Utility class to handle the conversion between Iterator and
   * Enumeration.
   */
  static class IteratorEnumeration implements Enumeration
  {
    
    Iterator i;
    
    IteratorEnumeration(Iterator i)
    {
      this.i = i;
    }
    
    public boolean hasMoreElements()
    {
      return i.hasNext();
    }
    
    public Object nextElement()
    {
      return i.next();
    }
    
  }
  
}
