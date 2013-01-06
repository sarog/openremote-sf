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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Properties;
import java.util.logging.Level;
import java.net.URI;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openremote.logging.Hierarchy;
import org.openremote.logging.AbstractLog;

/**
 * Some base unit tests for {@link org.openremote.logging.log4j.Redirector} class.
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class RedirectorTest
{

  /**
   * Points to build/tmp/test dir to store log files for tests...
   */
  private URI baseURI;


  // Test Lifecycle -------------------------------------------------------------------------------

  /**
   * Sets up instance fields before each test method...
   */
  @Before public void beforeTest()
  {
    File buildDir = new File(System.getProperty("user.dir"), "build");
    File tmpDir   = new File(buildDir, "tmp");
    File testDir  = new File(tmpDir, "test");

    baseURI = testDir.toURI();
  }



  // Tests ----------------------------------------------------------------------------------------

  /**
   * Simple file log test -- checks that log statements end up in the file handler.
   *
   * @throws Exception  if test fails
   */
  @Test public void simpleLogTest() throws Exception
  {
    Log4jLogger log = new Log4jLogger("redirect test");

    URI logId = baseURI.resolve("simple-log-test.log");

    log.addFileLog(logId);

    log.log("log4j test");
    log.log("secondline");
    log.log("third line");

    File logFile = new File(logId);

    Assert.assertTrue(logFile.exists());

    BufferedReader bin = new BufferedReader(new FileReader(logFile));

    String firstLine = bin.readLine();

    Assert.assertTrue(
        "Expected to contain 'log4j test', got '" + firstLine + "'.",
        firstLine.contains("log4j test")
    );

    Assert.assertTrue(firstLine.contains("INFO"));

    String secondLine = bin.readLine();
    String thirdLine = bin.readLine();

    Assert.assertTrue(secondLine.contains("secondline"));
    Assert.assertTrue(secondLine.contains("INFO"));

    Assert.assertTrue(thirdLine.contains("third line"));
    Assert.assertTrue(thirdLine.contains("INFO"));
  }


  /**
   * Tests file handlers with multiple log file consumers. Log statements should end up
   * in each one.
   *
   * @throws Exception  if test fails
   */
  @Test public void multipleFileLogTargetsTest() throws Exception
  {
    Log4jLogger log = new Log4jLogger("multitarget test");

    URI logId1 = baseURI.resolve("multitarget1.log");
    URI logId2 = baseURI.resolve("multitarget2.log");
    URI logId3 = baseURI.resolve("multitarget3.log");

    log.addFileLog(logId1);
    log.addFileLog(logId2);
    log.addFileLog(logId3);

    log.log("multitarget test 1");
    log.log("multitarget test 2");
    log.log("multitarget test 3");

    File logFile1 = new File(logId1);
    File logFile2 = new File(logId2);
    File logFile3 = new File(logId3);

    Assert.assertTrue(logFile1.exists());
    Assert.assertTrue(logFile2.exists());
    Assert.assertTrue(logFile3.exists());

    BufferedReader bin1 = new BufferedReader(new FileReader(logFile1));

    String firstLine = bin1.readLine();

    Assert.assertTrue(
        "Expected to contain 'multitarget test 1', got '" + firstLine + "'.",
        firstLine.contains("multitarget test 1")
    );

    Assert.assertTrue(firstLine.contains("INFO"));

    String secondLine = bin1.readLine();
    String thirdLine = bin1.readLine();

    Assert.assertTrue(secondLine.contains("multitarget test 2"));
    Assert.assertTrue(secondLine.contains("INFO"));

    Assert.assertTrue(thirdLine.contains("multitarget test 3"));
    Assert.assertTrue(thirdLine.contains("INFO"));



    BufferedReader bin2 = new BufferedReader(new FileReader(logFile2));

    firstLine = bin2.readLine();

    Assert.assertTrue(
        "Expected to contain 'multitarget test 1', got '" + firstLine + "'.",
        firstLine.contains("multitarget test 1")
    );

    Assert.assertTrue(firstLine.contains("INFO"));

    secondLine = bin2.readLine();
    thirdLine = bin2.readLine();

    Assert.assertTrue(secondLine.contains("multitarget test 2"));
    Assert.assertTrue(secondLine.contains("INFO"));

    Assert.assertTrue(thirdLine.contains("multitarget test 3"));
    Assert.assertTrue(thirdLine.contains("INFO"));



    BufferedReader bin3 = new BufferedReader(new FileReader(logFile3));

    firstLine = bin3.readLine();

    Assert.assertTrue(
        "Expected to contain 'multitarget test 1', got '" + firstLine + "'.",
        firstLine.contains("multitarget test 1")
    );

    Assert.assertTrue(firstLine.contains("INFO"));

    secondLine = bin3.readLine();
    thirdLine = bin3.readLine();

    Assert.assertTrue(secondLine.contains("multitarget test 2"));
    Assert.assertTrue(secondLine.contains("INFO"));

    Assert.assertTrue(thirdLine.contains("multitarget test 3"));
    Assert.assertTrue(thirdLine.contains("INFO"));

  }


  /**
   * Test configuration loading and reloading (complete reset)
   *
   * @throws Exception  if test fails
   */
  @Test public void loadConfigurationResetTest() throws Exception
  {
    Log4jLevelLogger log = new Log4jLevelLogger("load-config-test");

    URI logId = baseURI.resolve("load-config-test.log");

    Properties properties = new Properties();
    properties.setProperty("log4j.logger.OpenRemote.load-config-test", "OFF");

    log.configure(properties);
    log.addFileLog(logId);

    log.error("log4j test");
    log.warning("secondline");
    log.info("third line");

    File logFile = new File(logId);

    Assert.assertTrue(logFile.exists());

    BufferedReader bin = new BufferedReader(new FileReader(logFile));

    String firstLine = bin.readLine();

    Assert.assertTrue("Got '" + firstLine + "'.", firstLine == null);



    properties = new Properties();
    properties.setProperty("log4j.logger.OpenRemote.load-config-test", "ERROR");

    // this resets all configuration including configured file handlers so they need to be
    // added again too...

    log.configure(properties);
    log.addFileLog(logId);

    log.error("error message");
    log.warning("warning message");
    log.info("info message");

    bin = new BufferedReader(new FileReader(logFile));

    firstLine = bin.readLine();

    Assert.assertTrue(firstLine.contains("error message"));
  }

  /**
   * Simple configuration load test with threshold setting.
   *
   * @throws Exception  if test fails
   */
  @Test public void loadConfigurationTest() throws Exception
  {
    Log4jLevelLogger log = new Log4jLevelLogger("load-config-test2");

    URI logId = baseURI.resolve("load-config-test2.log");

    Properties properties = new Properties();
    properties.setProperty("log4j.logger.OpenRemote.load-config-test2", "ERROR");

    log.configure(properties);
    log.addFileLog(logId);

    log.error("log4j test");
    log.warning("secondline");
    log.info("third line");

    File logFile = new File(logId);

    Assert.assertTrue(logFile.exists());

    BufferedReader bin = new BufferedReader(new FileReader(logFile));

    String firstLine = bin.readLine();

    Assert.assertTrue(firstLine.contains("log4j test"));
  }

  /**
   * Test file log size limit configuration.
   *
   * @throws Exception  if any errors in test
   */
  @Test public void shortLogTest() throws Exception
  {
    Log4jLevelLogger shortLog = new Log4jLevelLogger("ten-byte-log");

    URI logId = baseURI.resolve("ten-byte-log.log");

    shortLog.addFileLog(logId, 0, 10, false);

    shortLog.info("1");

    File logFile = new File(logId);

    Assert.assertTrue(logFile.exists());

    BufferedReader bin = new BufferedReader(new FileReader(logFile));

    String firstLine = bin.readLine();

    Assert.assertTrue(firstLine.contains("INFO"));
    Assert.assertTrue(firstLine.contains("1"));

    String secondLine = bin.readLine();

    Assert.assertEquals(secondLine, null);

    shortLog.info("2");  // looks like this one overflows so log4j just drops it, it won't roll
                         // over to new log file...

    shortLog.info("3");

    bin = new BufferedReader(new FileReader(logFile));

    firstLine = bin.readLine();

    Assert.assertTrue(firstLine.contains("INFO"));
    Assert.assertTrue(firstLine.contains("3"));

    secondLine = bin.readLine();

    Assert.assertTrue(secondLine == null);
  }


  /**
   * Test file log backups.
   *
   * @throws Exception  if any errors occur
   */
  @Test public void backupLogTest() throws Exception
  {
    Log4jLevelLogger shortLog = new Log4jLevelLogger("backup-log");

    URI logId = baseURI.resolve("backup-log.log");

    shortLog.addFileLog(logId, 1, 10, false);

    shortLog.info("1");

    File logFile = new File(logId);

    Assert.assertTrue(logFile.exists());

    BufferedReader bin = new BufferedReader(new FileReader(logFile));

    String firstLine = bin.readLine();

    Assert.assertTrue(firstLine.contains("INFO"));
    Assert.assertTrue(firstLine.contains("1"));

    String secondLine = bin.readLine();

    Assert.assertEquals(secondLine, null);

    shortLog.info("2");     // looks like this goes into backup log as overflow
    shortLog.info("3");

    bin = new BufferedReader(new FileReader(logFile));

    firstLine = bin.readLine();


    Assert.assertTrue(firstLine.contains("INFO"));
    Assert.assertTrue(firstLine.contains("3"));


    File backupLogFile = new File(baseURI.resolve("backup-log.log.1"));

    Assert.assertTrue(backupLogFile.exists());

    bin = new BufferedReader(new FileReader(backupLogFile));

    firstLine = bin.readLine();

    Assert.assertTrue(firstLine.contains("INFO"));
    Assert.assertTrue(firstLine.contains("1"));

    secondLine = bin.readLine();

    Assert.assertTrue(secondLine.contains("INFO"));
    Assert.assertTrue(secondLine.contains("2"));
  }




  // Nested Classes -------------------------------------------------------------------------------

  /**
   * Simple log facade configured with log4j provider and simple log() API.
   */
  private static class Log4jLogger extends AbstractLog
  {
    Log4jLogger(final String hierarchy)
    {
      super(
          new Hierarchy()
          {
            @Override public String getCanonicalLogHierarchyName()
            {
              return hierarchy;
            }
          },

          false
      );

      this.setProvider(ProviderType.LOG4J);

      addLogger(this);
    }

    public void log(String msg)
    {
      logDelegate.log(Level.INFO, msg);
    }
  }

  /**
   * Log facade with log4j provider and convenience API for error, warning, info level
   * messages.
   */
  private static class Log4jLevelLogger extends AbstractLog
  {
      Log4jLevelLogger(final String hierarchy)
      {
        super(
            new Hierarchy()
            {
              @Override public String getCanonicalLogHierarchyName()
              {
                return hierarchy;
              }
            },

            false
        );


      this.setProvider(ProviderType.LOG4J);

      addLogger(this);
    }

    public void error(String msg)
    {
      logDelegate.log(Level.SEVERE, msg);
    }

    public void warning(String msg)
    {
      logDelegate.log(Level.WARNING, msg);
    }

    public void info(String msg)
    {
      logDelegate.log(Level.INFO, msg);
    }
  }

}

