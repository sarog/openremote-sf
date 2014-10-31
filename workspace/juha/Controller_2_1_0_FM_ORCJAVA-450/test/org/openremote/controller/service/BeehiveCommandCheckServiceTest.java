/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2014, OpenRemote Inc.
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
package org.openremote.controller.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import junit.framework.Assert;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import flexjson.JSONSerializer;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import org.openremote.controller.ControllerConfiguration;
import org.openremote.controller.HttpReceiver;
import org.openremote.controller.SecureTCPTestServer;
import org.openremote.controller.TCPTestServer;
import org.openremote.controllercommand.domain.ControllerCommandDTO;
import org.openremote.rest.GenericResourceResultWithErrorMessage;
import org.openremote.security.PasswordManager;
import org.openremote.security.X509CertificateBuilder;
import org.openremote.security.provider.BouncyCastleX509CertificateBuilder;
import org.openremote.useraccount.domain.ControllerDTO;


/**
 * Unit tests for class {@link org.openremote.controller.service.BeehiveCommandCheckService}.
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class BeehiveCommandCheckServiceTest
{

  private static PrivateKey privateKey;
  private static Certificate certificate;
  private static HostnameVerifier oldVerifier;


  // Test Lifecycle -------------------------------------------------------------------------------

  /**
   * Set up the encryption related infrastructure: install bouncycastle security provider,
   * create a key pair and corresponding certificate for the tests, configure the client trust
   * store in the JVM, and set up a host name verifier for 'localhost' because of the non-CA
   * certified key pair.
   *
   * @throws Exception    if anything fails
   */
  @BeforeClass public static void setup() throws Exception
  {
    // Set up BC provider, create a key pair and a self-signed certificate for the keypair,
    // store the certificate in a trust store and establish a hostname verifier that accepts
    // localhost connections regardless of the (self-signed) certificate used...

    Security.addProvider(new BouncyCastleProvider());

    KeyPair keyPair = createKeyPair();

    certificate = createCertificate(keyPair);
    privateKey = keyPair.getPrivate();

    setSystemTrustStore(certificate);

    oldVerifier = HttpsURLConnection.getDefaultHostnameVerifier();

    HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier()
    {
      @Override public boolean verify(String hostname, SSLSession sslSession)
      {
        return hostname.equalsIgnoreCase("localhost");
      }
    });
  }

  /**
   * Attempt to clean up the modifications we've made after the tests: remove truststore
   * property configuration, remove BouncyCastle security provider and remove our relaxed
   * host name verification.
   */
  @AfterClass public static void cleanup()
  {
    clearSystemTrustStore();

    Security.removeProvider("BC");

    HttpsURLConnection.setDefaultHostnameVerifier(oldVerifier);
  }



  // Unit Tests -----------------------------------------------------------------------------------


  /**
   * Basic test to check that the controller thread responsible for checking remote commands
   * starts and generates the expected HTTP GET request. <p>
   *
   * This test uses locally configured controller ID.
   *
   * @throws Exception    if test fails
   */
  @Test public void testRemoteCommandRequest() throws Exception
  {
    SecureTCPTestServer s = null;

    final Long CONTROLLER_ID = 1L;
    final String HOSTNAME = "localhost";
    final int PORT = 18888;
    final String USERNAME = "randomname";

    BeehiveCommandCheckService cs = null;

    try
    {
      // create a HTTP response...

      ArrayList<ControllerCommandDTO> list = new ArrayList<ControllerCommandDTO>();
      GenericResourceResultWithErrorMessage garbage = new GenericResourceResultWithErrorMessage(null, list);
      String response = new JSONSerializer().include("result").serialize(garbage);

      // create a secure TCP server with a HTTP receiver component...

      HttpReceiver receiver = new HttpReceiver();

      // respond to HTTP GET to Host: https://[HOSTNAME]:[PORT]/commands/[CONTROLLER_ID]...

      receiver.addResponse(HttpReceiver.Method.GET, "/commands/1", response);
      s = new SecureTCPTestServer(PORT, privateKey, certificate, receiver);

      s.start();


      // Create a deployer configuration for the CCS service...

      ControllerConfiguration config = new ControllerConfiguration();
      config.setRemoteCommandRequestInterval("60s");
      config.setBeehiveAccountServiceRESTRootUrl("::loopback," + CONTROLLER_ID + "::");
      config.setRemoteCommandServiceURI("https://" + HOSTNAME + ":" + PORT);
      config.setResourcePath(System.getProperty("user.dir")); // TODO

      // We need a resource dir for the controller's keystore...

      createUserResourceDir(config, USERNAME);


      // Start the deployer and give it 2 seconds to start up (this is brittle but requires
      // a component lifecycle interface to be added in order to make robust)...

      cs = new BeehiveCommandCheckService(config);
      cs.start(DeployerTest.createDeployer(config));

      Thread.sleep(2000);


      Assert.assertTrue(receiver.getMethod() == HttpReceiver.Method.GET);
      Assert.assertTrue(receiver.getPath().equalsIgnoreCase("/commands/" + CONTROLLER_ID));
      Assert.assertTrue(receiver.getHost(), receiver.getHost().equalsIgnoreCase(HOSTNAME));
      Assert.assertTrue(receiver.getPort().toString(), receiver.getPort() == PORT);
      Assert.assertTrue(receiver.getUserName().equals(USERNAME));

      Assert.assertTrue(receiver.getHeader("User-Agent").toLowerCase().contains("openremote"));
      Assert.assertTrue(receiver.getHeader("Accept").toLowerCase().contains("application/json"));
    }

    finally
    {
      if (cs != null)
      {
        cs.stop();
      }

      if (s != null)
      {
        s.stop();
      }
    }
  }


  // Helper Methods -------------------------------------------------------------------------------


  private void createUserResourceDir(ControllerConfiguration config, String username) throws Exception
  {
    File dir = new File(config.getResourcePath());

    // Where controller expects to find the currently verified user name...

    File f = new File(dir, ".user");

    f.deleteOnExit();

    FileWriter out = new FileWriter(f);
    out.write(username);
    out.close();

    // And the keystore...

    File keyfile = new File(dir, ".keystore");
    keyfile.deleteOnExit();

    // And the credentials convention used to access the store...

    String storeKey = System.getProperty("user.name") + ".key";

    PasswordManager pw = new PasswordManager(keyfile.toURI(), storeKey.toCharArray());
    pw.addPassword("randomname", "foo".getBytes(), storeKey.toCharArray());
  }

  private Thread getRemoteCommandThread()
  {
    Thread[] threads = new Thread[Thread.activeCount() * 2];
    Thread.enumerate(threads);

    for (Thread t : threads)
    {
      if (t == null || t.getName() == null)
      {
        continue;
      }

      else if (t.getName().equals(BeehiveCommandCheckService.REMOTE_COMMAND_THREAD_NAME))
      {
        return t;
      }
    }

    return null;
  }

  private static KeyPair createKeyPair() throws NoSuchAlgorithmException
  {
    KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");

    return generator.generateKeyPair();
  }

  private static Certificate createCertificate(KeyPair keyPair)
      throws X509CertificateBuilder.CertificateBuilderException
  {
    X509CertificateBuilder builder = new BouncyCastleX509CertificateBuilder();

    return builder.createSelfSignedCertificate(
        keyPair,
        new X509CertificateBuilder.Configuration(
            X509CertificateBuilder.SignatureAlgorithm.SHA512_WITH_RSA,
            "testissuer"
        )
    );
  }

  private static void clearSystemTrustStore()
  {
    System.clearProperty("javax.net.ssl.trustStore");
    System.clearProperty("javax.net.ssl.trustStorePassword");
  }


  private static File setSystemTrustStore(Certificate cert) throws Exception
  {
    File ddir = new File(System.getProperty("user.dir"));
    File truststore = new File(ddir, ".truststore");

    truststore.deleteOnExit();

    KeyStore trust = KeyStore.getInstance("JKS");

    trust.load(null, null);
    trust.setCertificateEntry("cert", cert);
    trust.store(new FileOutputStream(truststore), "foo".toCharArray());

    Assert.assertTrue(truststore.exists());

    System.setProperty("javax.net.ssl.trustStore", truststore.getAbsoluteFile().toString());
    System.setProperty("javax.net.ssl.trustStorePassword", "foo");

    return truststore;
  }



  // Nested Classes -------------------------------------------------------------------------------

  private static class CountingHttpReceiver extends HttpReceiver
  {
    private int count = 0;

    @Override protected void respond(TCPTestServer.Response response)
    {
      super.respond(response);

      count++;
    }
  }

  private static class MuteHttpReceiver extends HttpReceiver
  {
    private int count = 0;

    @Override protected void respond(TCPTestServer.Response response)
    {
      // don't respond...

      count++;
    }
  }
}

