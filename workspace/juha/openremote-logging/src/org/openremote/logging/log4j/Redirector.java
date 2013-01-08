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
package org.openremote.logging.log4j;

import java.io.IOException;
import java.util.logging.Handler;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.ErrorManager;
import java.util.Properties;
import java.text.MessageFormat;
import java.net.URI;
import java.io.File;

import org.apache.log4j.Logger;
import org.openremote.logging.AbstractLog;
import org.openremote.logging.Hierarchy;

import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.RollingFileAppender;
import org.apache.log4j.SimpleLayout;

/**
 * Log4j redirector for JUL. This implementation works by adding a handler for each of its
 * instances which maps incoming JUL log records to log4j logging API. This implementation itself
 * is a subclass of JUL logger and therefore managed by JUL log manager.
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class Redirector extends AbstractLog.Provider
{

  private static java.util.logging.Logger globalLog =
      java.util.logging.Logger.getLogger(java.util.logging.Logger.GLOBAL_LOGGER_NAME);

  /**
   * Creates a JUL logger under given hierarchy name and adds a redirector handler to this logger.
   * The installed handler will send the received log message to a correspondingly named log4j
   * logger instance.
   *
   * @param hierarchy   log hierarchy
   */
  public Redirector(Hierarchy hierarchy)
  {
    // IMPLEMENTATION NOTE :
    //
    //  This approach to redirection creates two parallel logger hierarchies -- one for JUL
    //  and another for log4j where the JUL loggers will have handlers sending messages to
    //  Log4j loggers. This seems somewhat wasteful although probably not excessively so.
    //
    //  It also means that (at least in theory) the JUL logger could have additional handlers
    //  next to the log4j redirector recording same log messages to alternative sources using
    //  JUL implementation.

    super(hierarchy);

    // Adds the log4j redirector handler...

    try
    {
      addHandler(new Log4jRedirect());
    }

    catch (SecurityException e)
    {
      errorManager.error(
          "Security Manager has prevented adding a log4j redirector handler to log hierarchy '" +
          hierarchy.toString() + "' (" + e.getMessage() + ").", e, ErrorManager.OPEN_FAILURE
      );
    }
  }


  /**
   * Loads a new log4j configuration into loggers. Notice that all existing configuration will be
   * erased first, therefore the configuration properties must contain a full set of new
   * properties. Also any registered log handlers (appenders in log4j terminology) will be removed
   * and must be re-added.
   *
   * @param properties  log4j properties
   */
  @Override public void loadConfiguration(Properties properties)
  {
    org.apache.log4j.LogManager.resetConfiguration();

    PropertyConfigurator.configure(properties);
  }

  /**
   * TODO
   *
   * @param type
   * @param config
   */
  @Override public void addLogConsumer(AbstractLog.ConsumerType type,
                                       AbstractLog.ConsumerConfiguration config)
  {

    Logger log4j = Logger.getLogger(hierarchy.getCanonicalLogHierarchyName());

    AbstractLog.FileConfiguration fileConfig;

    if (!(config instanceof AbstractLog.FileConfiguration))
    {
      fileConfig = new AbstractLog.FileConfiguration(errorManager);
    }

    else
    {
      fileConfig = (AbstractLog.FileConfiguration)config;
    }

    switch (type)
    {
      case SIMPLE_FILE:

        URI logFileId = fileConfig.getFileIdentifier();
        int backupCount = fileConfig.getFileBackupCount();
        int fileSizeLimit = fileConfig.getFileSizeLimit();
        boolean append = fileConfig.getFileAppendSetting();

        String logFilePath = new File(logFileId).getAbsolutePath();

        try
        {
          RollingFileAppender appender = new RollingFileAppender(
              new SimpleLayout(), logFilePath, append
          );

          appender.setMaxBackupIndex(backupCount);
          appender.setMaximumFileSize(fileSizeLimit);

          log4j.addAppender(appender);

          globalLog.info(
              "Log4j file appender '" + logFilePath + "' has been configured for hierarchy '" +
              hierarchy + "'."
          );
        }

        catch (IOException e)
        {
          // may be thrown by RollingFileAppender constructor...

          errorManager.error(
              "Log4j file appender '" + logFilePath + "' could not be created due to I/O error: " +
              e.getMessage(), e, ErrorManager.OPEN_FAILURE
          );
        }
        
        break;


      case XML_FILE:

        throw new Error("Not Yet Supported.");  // TODO

        //break;

      default:

        errorManager.error(
            "Unsupported log consumer type '" + type.name() + "'.",
            null, ErrorManager.GENERIC_FAILURE
        );
    }
  }



  // Nested Classes -------------------------------------------------------------------------------


  /**
   * Java util logging handler implementation to map JUL log records to log4j API and send
   * log messages to log4j.
   */
  private final static class Log4jRedirect extends Handler
  {

    /**
     * Translates and sends JUL log records to log4j logging.
     *
     * @param logRecord   java.util.logging log record
     */
    @Override public void publish(LogRecord logRecord)
    {

      // extract the level, message, log category and exception from log record...

      java.util.logging.Level level = logRecord.getLevel();
      String msg  = logRecord.getMessage();
      String category = logRecord.getLoggerName();
      Throwable thrown = logRecord.getThrown();
      Object[] params = logRecord.getParameters();

      // translate to log4j log category (1 to 1 name mapping)...

      org.apache.log4j.Logger log4j;

      try
      {
        log4j = org.apache.log4j.Logger.getLogger(category);
      }

      catch (NullPointerException e)
      {
        // NPE from log4j are possible during shutdown when logger references are being discarded
        // while threads are still attempting to log -- catching here and printing to standard
        // output...

        print(level, category, msg, params, thrown);

        return;
      }

      // mapping from JUL level to log4j levels...

      org.apache.log4j.Level log4jLevel = mapToLog4jLevel(level);

      // and log...

      if (log4j.isEnabledFor(log4jLevel))
      {
        if (params != null)
        {
          try
          {
            // TODO :
            //
            //   MessageFormat has in the past had fairly poor performance (unless the recent
            //   SDKs have addressed the issue). Should look for an alternative way to format
            //   log messages since formatting large amounts of messages will have a noticeable
            //   performance impact.

            msg = MessageFormat.format(msg, params);
          }
          catch (IllegalArgumentException e)
          {
            msg = msg + "  [LOG MESSAGE PARAMETERIZATION ERROR: " +
                  e.getMessage().toUpperCase() + "]";
          }
        }

        if (thrown != null)
        {
          log4j.log(log4jLevel, msg, thrown);
        }

        else
        {
          log4j.log(log4jLevel, msg);
        }
      }

    }

    /**
     * Does nothing -- Log4j API does not provide explicit flushing to appenders.
     */
    @Override public void flush()
    {
      // no-op
    }

    /**
     * Does nothing -- Log4j API does not provide an explicit close operation for appenders.
     */
    @Override public void close()
    {
      // no-op
    }


    /**
     * If a JUL log message level that has no corresponding or mapped log4j level available is
     * used, print a warning but do it only once and not for each one of such message -- this
     * flag indicates if such warning has already been printed.
     */
    private static boolean systemWarningOnUnmappedLevel = false;

    /**
     * Maps JUL levels to Log4j levels.
     *
     * @param level   the JUL level to map
     *
     * @return  the corresponding log4j level
     */
    private org.apache.log4j.Level mapToLog4jLevel(java.util.logging.Level level)
    {
      if (level == java.util.logging.Level.OFF)
      {
        return org.apache.log4j.Level.OFF;
      }

      else if (level == java.util.logging.Level.SEVERE)
      {
        return org.apache.log4j.Level.ERROR;
      }

      else if (level == java.util.logging.Level.WARNING)
      {
        return org.apache.log4j.Level.WARN;
      }

      else if (level == java.util.logging.Level.INFO)
      {
        return org.apache.log4j.Level.INFO;
      }

      else if (level == java.util.logging.Level.FINE)
      {
        return org.apache.log4j.Level.DEBUG;
      }

      else if (level == java.util.logging.Level.FINER || level == java.util.logging.Level.FINEST)
      {
        return org.apache.log4j.Level.TRACE;
      }

      else if (level == java.util.logging.Level.ALL)
      {
        return org.apache.log4j.Level.ALL;
      }

      else
      {
        if (!systemWarningOnUnmappedLevel)
        {
          globalLog.severe(
              "\n\n" +
              "-----------------------------------------------------------------------------" +
              "\n\n" +

              "  System is using custom log level (" + level.getName() + ") which has no \n" +
              "  defined mapping. Defaulting to INFO level." +

              "\n\n" +
              "-----------------------------------------------------------------------------" +
              "\n\n"
          );

          systemWarningOnUnmappedLevel = true;
        }

        return org.apache.log4j.Level.INFO;
      }
    }

    /**
     * Printing used if the logger references are already being discarded as part of shutdown
     * process but some threads still attempt to log...
     *
     * @param level       log level
     * @param hierarchy   log hierarhcy
     * @param message     log message (optionally parameterized)
     * @param params      log message parameters, if any
     * @param thrown      exception associated with the log message, if any
     */
    private void print(java.util.logging.Level level, String hierarchy, String message,
                       Object[] params, Throwable thrown)
    {
       StringBuffer[] paramStrings = null;

       if (params != null)
       {
         paramStrings = new StringBuffer[params.length];

         int index = 0;

         for (Object param : params)
         {
           try
           {
             paramStrings[index] = new StringBuffer().append(param);
           }

           catch (NullPointerException e)
           {
             paramStrings[index] = new StringBuffer().append("<null>");
           }

           finally
           {
             index++;
           }
         }
       }

      System.out.println(
          level + " [" + hierarchy + "]: " + MessageFormat.format(message, (Object[])paramStrings)
      );

      if (thrown != null)
      {
        thrown.printStackTrace();
      }
    }
  }

}

