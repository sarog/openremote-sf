package org.openremote.android.console.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.scheme.LayeredSocketFactory;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

/**
 * This socket factory will create ssl socket that accepts self signed certificate
 * 
 */
public class SelfCertificateSSLSocketFactory implements LayeredSocketFactory {

   private SSLContext sslcontext = null;

   private static SSLContext createEasySSLContext() throws IOException {
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
         SSLContext context = SSLContext.getInstance("TLS");
         context.init(null, new TrustManager[] { easyTrustManager }, null);
         return context;
      } catch (Exception e) {
         throw new IOException(e.getMessage());
      }
   }

   private SSLContext getSSLContext() throws IOException {
      if (this.sslcontext == null) {
         this.sslcontext = createEasySSLContext();
      }
      return this.sslcontext;
   }

   /**
    * @see org.apache.http.conn.scheme.SocketFactory#connectSocket(java.net.Socket, java.lang.String, int,
    *      java.net.InetAddress, int, org.apache.http.params.HttpParams)
    */
   public Socket connectSocket(Socket sock, String host, int port, InetAddress localAddress, int localPort,
         HttpParams params) throws IOException, UnknownHostException, ConnectTimeoutException {
      int connTimeout = HttpConnectionParams.getConnectionTimeout(params);
      int soTimeout = HttpConnectionParams.getSoTimeout(params);
      InetSocketAddress remoteAddress = new InetSocketAddress(host, port);
      SSLSocket sslsock = (SSLSocket) ((sock != null) ? sock : createSocket());

      if ((localAddress != null) || (localPort > 0)) {
         // we need to bind explicitly
         if (localPort < 0) {
            localPort = 0; // indicates "any"
         }
         InetSocketAddress isa = new InetSocketAddress(localAddress, localPort);
         sslsock.bind(isa);
      }

      sslsock.connect(remoteAddress, connTimeout);
      sslsock.setSoTimeout(soTimeout);
      return sslsock;

   }

   /**
    * @see org.apache.http.conn.scheme.SocketFactory#createSocket()
    */
   public Socket createSocket() throws IOException {
      return getSSLContext().getSocketFactory().createSocket();
   }

   /**
    * @see org.apache.http.conn.scheme.SocketFactory#isSecure(java.net.Socket)
    */
   public boolean isSecure(Socket socket) throws IllegalArgumentException {
      return true;
   }

   /**
    * @see org.apache.http.conn.scheme.LayeredSocketFactory#createSocket(java.net.Socket, java.lang.String, int,
    *      boolean)
    */
   public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException,
         UnknownHostException {
      return getSSLContext().getSocketFactory().createSocket(socket, host, port, autoClose);
   }

}
