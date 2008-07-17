/*
 * OpenRemote, the Internet-enabled Home.
 *
 * This work is licensed under Creative Commons Attribution-Noncommercial-Share Alike 3.0
 * United States, http://creativecommons.org/licenses/by-nc-sa/3.0/us/
 */
package org.openremote.controller.core;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.UnknownServiceException;
import java.security.AccessController;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivilegedAction;
import java.security.Provider;
import java.security.Security;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.bouncycastle.asn1.x509.X509Name;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.x509.X509V3CertificateGenerator;
import org.jboss.kernel.Kernel;
import org.jboss.logging.Logger;
import org.jboss.util.naming.NonSerializableFactory;

/**
 * Bootstraps kernel and exports kernel related properties and references to JNDI for consumption
 * to other Java services deployed on the home box. Will also attempt to initialize the box with
 * default profile and private/public key pair in case this is the first time the box is started,
 * or it has not been successfully initialized yet. <p>
 *
 * This bean should be deployed before any other home box services to ensure exported variables
 * are always available.  <p>
 *
 * After successful deployment of this bean the JNDI will be initialized as follows:   <p>
 *
 * <pre>
 * /kernel                -- non-serializable reference to the microkernel itself
 * /serialnumber          -- String containing the serial number of this box
 * /filesystem/root       -- java.io.File representing root configuration directory of the server
 * /filesystem/downloads  -- java.io.File representing the download directory of the server
 * </pre>
 *
 * If the box is currently not initialized, the private key will be stored in the filesystem with
 * a key store name ".keystore" under the JNDI bound "/filesystem/root" location.  <p>
 *
 * If registration is successful, a default profile will be stored in JNDI bound
 * "/filesystem/downloads" location.
 *
 * @see #JNDI_FILESYSTEM_CONTEXT
 * @see #JNDI_FILESYSTEM_DOWNLOADS
 * @see # JNDI_FILESYSTEM_DOWNLOADS
 * @see #JNDI_KERNEL
 * @see #JNDI_SERIALNUMBER
 *
 * @author <a href="mailto:juha@juhalindfors.com">Juha Lindfors</a>
 * @version $Id: $
 */
public class Bootstrap
{

  // Constants ------------------------------------------------------------------------------------

  /**
   * The name of the filesystem subcontext in JNDI.
   */
  public final static String JNDI_FILESYSTEM_CONTEXT = "/filesystem";

  /**
   * The name of java.io.File bound to JNDI representing the root directory of the filesystem.  <p>
   *
   * Notice that this is not the physical root of the OS level filesystem but only an abstraction
   * to the Java services.
   */
  public final static String JNDI_FILESYSTEM_ROOT = JNDI_FILESYSTEM_CONTEXT + "/root";

  /**
   * The name of java.io.File bound to JNDI representing the download directory of the filesystem.
   */
  public final static String JNDI_FILESYSTEM_DOWNLOADS = JNDI_FILESYSTEM_CONTEXT + "/downloads";

  /**
   * JNDI lookup name for org.jboss.kernel.Kernel reference bound to JNDI.
   */
  public final static String JNDI_KERNEL = "/kernel";

  /**
   * JNDI lookup name for the controller serial number string.
   */
  public final static String JNDI_SERIALNUMBER = "/serialnumber";

  /**
   * TODO
   */
  public final static String ROOT_LOG_CATEGORY = "OpenRemote.Controller";

  /**
   * Directory name where downloaded device bundles will be saved.
   */
  private final static String DOWNLOAD_DIRECTORY = "downloads";

  /**
   * TODO
   */
  private final static String DEFAULT_PROFILE_PACKAGE = "default-profile.jar";

  /**
   * TODO
   */
  private final static String KEYSTORE_FILENAME = ".keystore";

  /**
   * TODO
   */
  private final static String KEY_ALGORITHM = "RSA";


  // Instance Fields ------------------------------------------------------------------------------

  /**
   * Reference to JNDI established in the constructor.
   */
  private Context naming = null;

  /**
   * Bootstrap logger
   */
  private Logger log = Logger.getLogger(ROOT_LOG_CATEGORY + ".BOOTSTRAP");

  
  // Constructors ---------------------------------------------------------------------------------

  /**
   * Will initialize the controller environment and export properties to JNDI
   * for consumption to Java services deployed on the controller.  <p>
   *
   * Successful instantiation will initialize JNDI server with a root level subcontext
   * {@link #JNDI_FILESYSTEM_CONTEXT} under which {@link #JNDI_FILESYSTEM_ROOT} and
   * {@link #JNDI_FILESYSTEM_DOWNLOADS} names will be bound. Note that this constructor
   * will also create a downloads directory on the home box filesystem if it is missing. <p>
   *
   * All objects bound under FILESYSTEM_CONTEXT should be of java.io.File type.   <p>
   *
   * Successful lookup from the JNDI name FILESYSTEM_ROOT will yield a reference
   * to java.io.File representing the root directory of the controller server configuration.   <p>
   *
   * Succcessful lookup from the JNDI name FILESYSTEM_DOWNLOADS will yield a
   * reference to java.io.File representing the controller download directory.  <p>
   *
   * Note that additionally the {@link #setKernel(org.jboss.kernel.Kernel)} method will export
   * the kernel reference to JNDI.   <p>
   *
   * If the controller has not been initialized yet or the registration is not complete, this
   * constructor will trigger the registration process. TODO : ref to registration use case
   *
   * @see #setKernel(org.jboss.kernel.Kernel)
   * @see #JNDI_FILESYSTEM_CONTEXT
   * @see #JNDI_FILESYSTEM_ROOT
   * @see #JNDI_FILESYSTEM_DOWNLOADS
   *
   * @throws Error  if security manager denies access to the system properties set by the
   *                microkernel, or to the download directory of the home box; if there are
   *                any problems accessing the download directory; or if binding variables
   *                to JNDI fails
   */
  public Bootstrap()
  {
    // TODO:
    //   -  store MAC and report network interfaces -- will come useful in diagnosing
    //      controller issues

    try
    {
      // establish a default security provider...

      setSecurityProvider();

      // Initialize JNDI entries...

      naming = new InitialContext();

      // bind the serial number to JNDI...
      // TODO : SN will come from hardware eventually

      naming.bind(JNDI_SERIALNUMBER, "123456789");

      // create the '/filesystem' subcontext in JNDI...

      Context filesystem = naming.createSubcontext(JNDI_FILESYSTEM_CONTEXT);

      // Bind the variables... Note that getDownloadDirectory() will attempt to create
      // the directory if it is missing.

      filesystem.bind(JNDI_FILESYSTEM_ROOT, getServerRoot());
      filesystem.bind(JNDI_FILESYSTEM_DOWNLOADS, getDownloadDirectory());

      // Announce....

      log.info("Reference to controller root directory bound to '" + JNDI_FILESYSTEM_ROOT + "'.");
      log.info("Reference to controller downloads directory bound to '" + JNDI_FILESYSTEM_DOWNLOADS + "'.");

      // initialize box if default profile is not present...

      if (!checkDefaultProfile())
      {
        initializeDefaultProfile();
      }
    }
    catch (NamingException e)
    {
      throw new Error("Cannot initialize naming context: " + e.toString(), e);
    }
  }


  // JavaBean Properties --------------------------------------------------------------------------

  /**
   * This property is injected by the kernel when this bean is deployed. The injected kernel
   * reference will be bound to JNDI naming context under name {@link #JNDI_KERNEL} as
   * a non-serializable reference.
   *
   * @param kernel    reference to the kernel injected by the kernel itself on deploying this bean
   *
   * @throws Error    if the kernel reference could not be bound to JNDI
   */
  public void setKernel(Kernel kernel)
  {
    try
    {
      // NSF is JBoss API...

      NonSerializableFactory.rebind(naming, JNDI_KERNEL, kernel);

      log.info("Kernel bound to JNDI name '" + JNDI_KERNEL + "'.");
    }
    catch (NamingException e)
    {
      throw new Error("Unable to bind kernel reference to JNDI: " + e.toString(), e);
    }
  }


  // Private Instance Methods ---------------------------------------------------------------------

  /**
   * TODO
   *
   * @return
   *
   * @throws Error
   */
  private boolean checkDefaultProfile()
  {
    File downloadDir = getDownloadDirectory();

    final File defaultProfile = new File(downloadDir, DEFAULT_PROFILE_PACKAGE);

    return AccessController.doPrivileged(

        new PrivilegedAction<Boolean>()
        {
          public Boolean run()
          {
            try
            {
              return defaultProfile.exists();
            }
            catch (SecurityException e)
            {
              throw new Error(
                  "Security manager has denied read access to " + defaultProfile +
                  ": " + e.toString(), e
              );
            }
          }
        }
    );
  }

  /**
   * TODO
   */
  private void initializeDefaultProfile()
  {
    log.info("#################################################################################");
    log.info("  Default profile not found. Initiating registration process...");
    log.info("#################################################################################");

    Certificate certificate = null;

    if (!keyExists())
    {
      KeyPair keyPair = generateKeyPair();
      KeyStore keystore = createKeyStore();
      certificate = createCertificate(keyPair);
      storeKey(keystore, keyPair, certificate);

      log.info("Key generated and stored.");
      log.info("----- Public Certificate -----");
      log.info("\n" + certificate);
    }
    else
    {
      certificate = getPublicCertificate();
    }

    registerCertificate(certificate);

    log.info("#################################################################################");
    log.info("  Registration Complete.");
    log.info("#################################################################################");
  }


  /**
   * TODO
   *
   * @return
   *
   * @throws Error    TODO
   */
  private boolean keyExists()
  {

    // TODO : check keystore content

    File serverRoot = getServerRoot();

    final File keystore = new File(serverRoot, KEYSTORE_FILENAME);

    return AccessController.doPrivileged(

        new PrivilegedAction<Boolean>()
        {
          public Boolean run()
          {
            try
            {
              return keystore.exists();
            }
            catch (SecurityException e)
            {
              throw new Error(
                  "Security manager has denied read access to " + keystore + ": " + e.toString(), e
              );
            }
          }
        }
    );
  }

  private Certificate getPublicCertificate()
  {
    // TODO

    throw new Error("NYI");
  }

  /**
   * TODO
   *
   * @return
   *
   * @throws Error TODO
   */
  private KeyPair generateKeyPair()
  {
    final int KEY_SIZE = 2048;

    try
    {
      KeyPairGenerator keyGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);

      try
      {
        keyGen.initialize(KEY_SIZE);
      }
      catch (InvalidParameterException e)
      {
        log.warn("Security provider '" + keyGen.getProvider().getName() + "' does not support " +
                 KEY_SIZE + " bit keysize. Falling back to default keysize.", e);
      }

      log.info("Generating key...");

      return keyGen.generateKeyPair();
    }
    catch (NoSuchAlgorithmException e)
    {
      throw new Error("No security provider found for " + KEY_ALGORITHM + " algorithm.");
    }
  }


  /**
   * TODO
   *
   * @param keystore
   * @param keyPair
   * @param certificate
   */
  private void storeKey(KeyStore keystore, KeyPair keyPair, Certificate certificate)
  {
    try
    {
      Context naming = new InitialContext();

      String serial = (String)naming.lookup(JNDI_SERIALNUMBER);

      File root = (File)naming.lookup(JNDI_FILESYSTEM_ROOT);
      File keystoreFile = new File(root, KEYSTORE_FILENAME);

      BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(keystoreFile));

      try
      {
        KeyStore.PrivateKeyEntry privateKeyEntry = new KeyStore.PrivateKeyEntry(
            keyPair.getPrivate(), new java.security.cert.Certificate[] { certificate }
        );

        keystore.setEntry("ControllerPrivateKey", privateKeyEntry, new KeyStore.PasswordProtection(serial.toCharArray()));
        keystore.store(out, serial.toCharArray());
      }
      finally
      {
        out.flush();
        out.close();
      }
    }
    catch (FileNotFoundException e)
    {
      System.out.println(e);

      // TODO

      throw new Error(e.toString(), e);
    }
    catch (IOException e)
    {
      System.out.println(e);

      // TODO

      throw new Error(e.toString(), e);
    }
    catch (KeyStoreException e)
    {
      System.out.println(e);

      // TODO

      throw new Error(e.toString(), e);
    }
    catch (NoSuchAlgorithmException e)
    {
      System.out.println(e);

      // TODO

      throw new Error(e.toString(), e);
    }
    catch (CertificateException e)
    {
      System.out.println(e);

      // TODO

      throw new Error(e.toString(), e);
    }
    catch (NamingException e)
    {
      // TODO
      throw new Error(e);
    }
  }


  /**
   * TODO
   *
   * @param keyPair
   *
   * @return
   */
  private Certificate createCertificate(KeyPair keyPair)
  {
    final String CERTIFICATE_ALGORITHM = "SHA1with" + KEY_ALGORITHM;

    final long SECOND = 1000;
    final long MINUTE = 60 * SECOND;
    final long HOUR   = 60 * MINUTE;
    final long DAY    = 24 * HOUR;
    final long YEAR   = 365 * DAY;

    try
    {
      String serial = (String)new InitialContext().lookup(JNDI_SERIALNUMBER);

      // TODO : could use the X509Principal with site certificate for issuer instead

      X509V3CertificateGenerator certGenerator = new X509V3CertificateGenerator();
      X509Name issuerName = new X509Name("O=www.openremote.org, CN=Open Remote Controller");
      X509Name subjectName = new X509Name("CN=Open Remote Controller SN/" + serial);

      certGenerator.setPublicKey(keyPair.getPublic());
      certGenerator.setSubjectDN(subjectName);
      certGenerator.setIssuerDN(issuerName);
      certGenerator.setSerialNumber(new BigInteger(serial));

      long time = System.currentTimeMillis();
      certGenerator.setNotBefore(new Date(0));
      certGenerator.setNotAfter(new Date(time + 10*YEAR));
      certGenerator.setSignatureAlgorithm(CERTIFICATE_ALGORITHM);
  
      return certGenerator.generate(keyPair.getPrivate());
    }
    catch (NamingException e)
    {
      // TODO
      throw new Error(e);
    }
    catch (CertificateEncodingException e)
    {
      // TODO
      throw new Error(e);
    }
    catch (NoSuchAlgorithmException e)
    {
      // TODO
      throw new Error(e);
    }
    catch (SignatureException e)
    {
      // TODO
      throw new Error(e);
    }
    catch (InvalidKeyException e)
    {
      // TODO
      throw new Error(e);
    }
  }


  /**
   * TODO
   *
   * @return
   */
  private KeyStore createKeyStore()
  {
    final String KEYSTORE_TYPE = KeyStore.getDefaultType();

    try
    {
      String serial = (String)new InitialContext().lookup(JNDI_SERIALNUMBER);
      KeyStore keystore = KeyStore.getInstance(KEYSTORE_TYPE);
      keystore.load(null, serial.toCharArray());

      return keystore;
    }
    catch (KeyStoreException e)
    {
      throw new Error(
          "No security provider found for " + KEYSTORE_TYPE + " keystore type (" +
          e.toString() + ").", e
      );
    }
    catch (SecurityException e)
    {
        // TODO
      throw new Error(e);
    }
    catch (IOException e)
    {
      throw new Error("Can't create new keystore: " + e.toString(), e);
    }
    catch (NoSuchAlgorithmException e)
    {
      // We're using default type above, so this *shouldn't* be a problem, unless the code
      // above has been changed... ;-)

      throw new Error(
          "Required keystore algorithm '" + KEYSTORE_TYPE + "' not found: " + e.toString(), e
      );
    }
    catch (CertificateException e)
    {
      // currently not using CA either...

      throw new Error("Keystore certificate could not be loaded: " + e.toString(), e);
    }
    catch (NamingException e)
    {
      throw new Error("Failed environment name lookup: " + e.toString(), e);
    }
  }

  /**
   * TODO
   *
   * @param certificate
   */
  private void registerCertificate(Certificate certificate)
  {
    List<URL> homeURLs = getHomeURLs();

    try
    {
      byte[] encodedCertificate = certificate.getEncoded();

      Connection con = getControllerRegistrationConnection(encodedCertificate.length);
      
      try
      {
        con.getOutput().write(encodedCertificate);
      }
      finally
      {
        con.getOutput().flush();
        con.getOutput().close();
      }

      int response = con.getConnection().getResponseCode();

      // TODO ....

      if (response != HttpURLConnection.HTTP_OK)
        throw new Error("got response " + response);

    }
    catch (CertificateEncodingException e)
    {
      // TODO
      throw new Error("Implementation Error: " + e, e);
    }
    catch (ConnectException e)
    {
      // TODO
      throw new Error(e);
    }
    catch (UnknownServiceException e)
    {

    }
    catch (IOException e)
    {
      // TODO
      throw new Error("Is this where it hits??", e);
    }

  }


  /**
   * TODO
   *
   * @param contentLength
   *
   * @return
   */
  private Connection getControllerRegistrationConnection(int contentLength)
  {
    // TODO : this should be externalized to configuration
    final String CONTROLLER_REGISTRATION_URN = "ControllerRegistration";

    List<URL> homeURLs = getHomeURLs();

    while (true)
    {
      for (URL homeURL : homeURLs)
      {
        try
        {
          URL url = new URL(homeURL, CONTROLLER_REGISTRATION_URN);

          log.info("Attempting to connect to " + url);

          // TODO: connect via proxy

          HttpURLConnection connection = (HttpURLConnection)url.openConnection();

          connection.setDoOutput(true);
          connection.setDoInput(true);
          connection.setAllowUserInteraction(false);
          connection.setUseCaches(false);
          connection.setRequestMethod("PUT");
          connection.setRequestProperty("Content-Type", "application/octet-stream");
          connection.setRequestProperty("Content-Length", Integer.toString(contentLength));
          connection.setRequestProperty("User-Agent", getUserAgent());

          Connection con = new Connection();
          con.httpConnection = connection;
          con.output = new BufferedOutputStream(connection.getOutputStream());

          return con;
        }
        catch (MalformedURLException e)
        {
          log.warn("Configuration error: " + homeURL + " is not valid URL.");
        }
        catch (IOException e)
        {
          log.debug("Failed to connect to " + homeURL + ": " + e.toString(), e);
        }
        catch (ClassCastException e)
        {
          // TODO
          log.warn(e);
        }
      }

      try
      {
        log.info("Failed to connect to controller registration service. Will try again in 60 seconds...");

        Thread.sleep(1000*60 /* 60 seconds */);
      }
      catch (InterruptedException ignored) {}
    }
  }

  /**
   * TODO
   *
   * @return
   */
  private String getUserAgent()
  {
    // If the locale doesn't specify a language, this will be the empty string.
    // Otherwise, this will be a lowercase ISO 639-2/T language code.
    //
    // The ISO 639-2 language codes: http://www.loc.gov/standards/iso639-2/englangn.html.

    String language = "";

    try
    {
      language = Locale.getDefault().getISO3Language();
    }
    catch (MissingResourceException e)
    {
      // TODO
    }

    return "OpenRemote Controller/1.0 [" + language + "]";
  }


  /**
   * TODO : externalize configuration
   *
   * @return
   */
  private List<URL> getHomeURLs()
  {
    /*
     * This method is a stub. Essentially it should provide a list of locations
     * the home box can register with. This configuration can come from a system
     * property set as JVM arguments or from external configuration file (making it
     * updateable). Probably something for the existing deployers and configuration
     * files.
     */

    URL url1 = null;

    try
    {
      url1 = new URL("http://localhost:8080");
    }
    catch (MalformedURLException e)
    {
      throw new Error(e);
    }

    return Arrays.asList(url1);
  }

  /**
   * Returns the jm3 server root directory. The environment variable is setup by the JBoss
   * AS that should point to [ROOT]/server/jm3 directory. <p>
   *
   * @throws Error  if cannot access the JBoss system property.
   *
   * @return  java.io.File instance representing server root directory path
   */
  private File getServerRoot()
  {
    return AccessController.doPrivileged(

      new PrivilegedAction<File>()
      {
        public File run()
        {
          try
          {
            return new File(System.getProperty("jboss.server.home.dir"));
          }
          catch (SecurityException e)
          {
            throw new Error("Cannot access property 'jboss.server.home.dir': " + e.toString(), e);
          }
        }
      }
    );
  }


  /**
   * Returns the location of download directory. The path will be relative to
   * {@link #getServerRoot()}. The name of the download directory is specified in
   * {@link #DOWNLOAD_DIRECTORY}.  <p>
   *
   * If 'ServerRoot/DOWNLOAD_DIRECTORY' does not exist, an attempt is made to
   * create it. <p>
   *
   * @return file reference to 'ServerRoot/DOWNLOAD_DIRECTORY'.
   *
   * @throws Error              if 'ServerRoot/DOWNLOAD_DIRECTORY' exists but is not a directory,
   *                            or the directory could not be created, or the security manager
   *                            has denied access to downloads directory
   */
  private File getDownloadDirectory()
  {
    final File downloads = new File(getServerRoot(), DOWNLOAD_DIRECTORY);

    // Try to create downloads dir if it doesn't already exist...

    AccessController.doPrivileged(

      new PrivilegedAction<Void>()
      {
        public Void run()
        {
          try
          {
            if (!downloads.exists())
            {
              boolean created = downloads.mkdir();

              if (!created)
              {
                throw new Error(
                    "Cannot create '" + getServerRoot() + File.separator + DOWNLOAD_DIRECTORY + "'. "
                );
              }
            }

            else if (!downloads.isDirectory())
            {
              throw new Error(
                  getServerRoot() + File.separator + DOWNLOAD_DIRECTORY + " is not " +
                  "a directory. Rename or move file '" + DOWNLOAD_DIRECTORY + "' and restart " +
                  "the server."
              );
            }

            return null;
          }
          catch (SecurityException e)
          {
            throw new Error(
                "Security manager has denied access to download directory (" +
                DOWNLOAD_DIRECTORY + "): " + e.toString(), e
            );
          }
        }
      }
    );

    return downloads;
  }


  /**
   * TODO
   *
   *
   */
  public void setSecurityProvider()
  {
    final Provider provider = new BouncyCastleProvider();
    int providerPosition = -1;

    try
    {
      providerPosition = AccessController.doPrivileged(new PrivilegedAction<Integer>()
        {
          public Integer run()
          {
            return Security.insertProviderAt(provider, 1 /* most preferred */);
          }
        }
      );
    }
    catch (SecurityException e)
    {
      log.warn("Cannot install security provider due to security manager restrictions.", e);
    }

    if (providerPosition == -1)
    {
      log.debug("Provider '" + provider.getName() + "' already installed.");
    }
  }


  // Nested Classes -------------------------------------------------------------------------------

  /**
   * Simple holder
   */
  private class Connection
  {
    HttpURLConnection httpConnection;
    BufferedOutputStream output;

    HttpURLConnection getConnection()
    {
      return httpConnection;
    }

    BufferedOutputStream getOutput()
    {
      return output;
    }
  }
}