/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2010, OpenRemote Inc.
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
package org.openremote.controller.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.openremote.controller.Configuration;
import org.openremote.controller.Constants;
import org.openremote.controller.exception.BeehiveNotAvailableException;
import org.openremote.controller.exception.ForbiddenException;
import org.openremote.controller.exception.ResourceNotFoundException;
import org.openremote.controller.service.FileService;
import org.openremote.controller.utils.PathUtil;
import org.openremote.controller.utils.ZipUtil;
import org.springframework.security.providers.encoding.Md5PasswordEncoder;


/**
 * The implementation for FileService interface.
 * 
 * @author Dan 2009-5-14
 */
public class FileServiceImpl implements FileService {
   
   /** The Constant logger. */
   private static final Logger logger = Logger.getLogger(FileServiceImpl.class);
   
   /** The configuration. */
   private Configuration configuration;
   
   /**
    * {@inheritDoc}
    */
   public boolean unzip(InputStream inputStream, String targetDir) {
      return ZipUtil.unzip(inputStream, targetDir);
   }

   /**
    * {@inheritDoc}
    */
   public boolean uploadConfigZip(InputStream inputStream) {
      String resourcePath = configuration.getResourcePath();
      if (!unzip(inputStream, resourcePath)){
         return false; 
      }
      copyLircdConf(resourcePath);
      return true;
   }

   private void copyLircdConf(String resourcePath) {
      File lircdConfFile = new File(resourcePath + Constants.LIRCD_CONF);
      File lircdconfDir = new File(configuration.getLircdconfPath().replaceAll(Constants.LIRCD_CONF, ""));
      try {
         if (lircdconfDir.exists() && lircdConfFile.exists()) {
            // this needs root user to put lircd.conf into /etc.
            // because it's readonly, or it won't be modified.
            if (configuration.isCopyLircdconf()) {
               FileUtils.copyFileToDirectory(lircdConfFile, lircdconfDir);
               logger.info("copy lircd.conf to" + configuration.getLircdconfPath());
            }
         }
      } catch (IOException e) {
         logger.error("Can't copy lircd.conf to " + configuration.getLircdconfPath(), e);
      }
   }

   private boolean writeZipAndUnzip(InputStream inputStream) {
      String resourcePath = configuration.getResourcePath();
      File zip = new File(resourcePath, "openremote.zip");
      FileOutputStream fos = null;
      try {
         if (!zip.getParentFile().exists()) {
            FileUtils.forceMkdir(zip);
         }
         FileUtils.forceDeleteOnExit(zip);
         fos = new FileOutputStream(zip);
         byte[] buffer = new byte[1024];
         int len = 0;
         while ((len = inputStream.read(buffer)) != -1) {
            fos.write(buffer, 0, len);
         }
         if (!ZipUtil.unzip(zip, resourcePath)) {
            return false;
         }
         logger.info("unzip " + zip.getAbsolutePath() + " success.");
         FileUtils.forceDeleteOnExit(zip);
      } catch (IOException e) {
         logger.error("Can't write openremote.zip to " + resourcePath, e);
      } finally {
         if (inputStream != null) {
            try {
               inputStream.close();
            } catch (IOException e) {
               logger.debug("failed to  close input stream of " + zip.getAbsolutePath());
            }
         }
         if (fos != null) {
            try {
               fos.close();
            } catch (IOException e) {
               logger.debug("failed to  close file output stream of " + zip.getAbsolutePath());
            }
         }
      }
      
      copyLircdConf(resourcePath);
      return true;
   }

   /**
    * {@inheritDoc}
    */
   public InputStream findResource(String relativePath) {
      File file = new File(PathUtil.removeSlashSuffix(configuration.getResourcePath()) + relativePath);
      if (file.exists() && file.isFile()) {
         try {
            return new FileInputStream(file);
         } catch (FileNotFoundException e) {
            return null;
         }
      }
      return null;
   }
   
   /**
    * {@inheritDoc}
    */
   public boolean syncConfigurationWithModeler(String username, String password) {
      return downloadOpenremoteZipFromBeehiveAndUnzip(username, password);
   }
   
   private boolean downloadOpenremoteZipFromBeehiveAndUnzip(String username, String password) {
      HttpClient httpClient = new DefaultHttpClient();
      String beehiveRESTRootUrl = configuration.getBeehiveRESTRootUrl();
      beehiveRESTRootUrl = beehiveRESTRootUrl.replace("http:", "https:");
      // Create a self certificate manager.
      TrustManager easyTrustManager = new X509TrustManager() {
         @Override
         public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
         }
         @Override
         public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
         }
         @Override
         public X509Certificate[] getAcceptedIssuers() {
            return null;
         }
      };
      
      try {
         URL uri = new URL(beehiveRESTRootUrl);
         int beehivePort = uri.getPort();
         if (beehivePort != -1) {
            beehiveRESTRootUrl = beehiveRESTRootUrl.replace(":" + beehivePort, ":" + configuration.getBeehiveHttpsPort());
         }
         
         // Register the https scheme. 
         SSLContext sslContext = SSLContext.getInstance("TLS");
         sslContext.init(null, new TrustManager[] {easyTrustManager}, null);
         SSLSocketFactory sf = new SSLSocketFactory(sslContext);
         sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
         Scheme sch = new Scheme("https", sf, 8443);
         httpClient.getConnectionManager().getSchemeRegistry().register(sch);
      } catch (MalformedURLException e1) {
         logger.error("failed to construct url " + beehiveRESTRootUrl);
      } catch (NoSuchAlgorithmException e) {
         logger.error("no TLS algorithm.");
      } catch (KeyManagementException e) {
         logger.error("failed to init SSLContext.");
      }
      
      HttpGet httpGet = new HttpGet(PathUtil.addSlashSuffix(beehiveRESTRootUrl) + "user/" + username
            + "/openremote.zip");
      
      httpGet.setHeader(Constants.HTTP_BASIC_AUTH_HEADER_NAME, Constants.HTTP_BASIC_AUTH_HEADER_VALUE_PREFIX
            + encode(username, password));
      InputStream inputStream = null;
      try {
         HttpResponse response = httpClient.execute(httpGet);
         if (200 == response.getStatusLine().getStatusCode()) {
            logger.info(httpGet.getURI() + " is available.");
            inputStream = response.getEntity().getContent();
            return writeZipAndUnzip(inputStream);
         } else if (401 == response.getStatusLine().getStatusCode()) {
            throw new ForbiddenException();
         } else if (404 == response.getStatusLine().getStatusCode()) {
            throw new ResourceNotFoundException();
         } else {
            throw new BeehiveNotAvailableException("failed to download resources for template, The status code is: "
                  + response.getStatusLine().getStatusCode());
         }
      } catch (IOException e) {
         logger.error("failed to connect to Beehive.");
      } finally {
         if (inputStream != null) {
            try {
               inputStream.close();
            } catch (IOException e) {
               logger.error("failed to close input stream while downloading " + httpGet.getURI());
            }
         }
      }
      return false;

   }
   
   private String encode(String username, String password) {
      Md5PasswordEncoder encoder = new Md5PasswordEncoder();
      String encodedPwd = encoder.encodePassword(password, username);
      if (username == null || encodedPwd == null) {
         return null;
      }
      return new String(Base64.encodeBase64((username + ":" + encodedPwd).getBytes()));
   }

   public void setConfiguration(Configuration configuration) {
      this.configuration = configuration;
   }

}
