package org.openremote.controller.protocol.ping;

import org.apache.log4j.Logger;
import org.openremote.controller.model.sensor.Sensor;
import org.openremote.controller.protocol.ReadCommand;

/**
 * PingCommand is a protocol to detect if a device is available.
 * 
 * @author Simon Vincent
 */
public class PingCommand extends ReadCommand {

   /** The logger. */
   private static Logger logger = Logger.getLogger(PingCommandBuilder.class.getName());
   
   /** The IP address of the device to ping */
   private String ipAddr;
   
      
   public static boolean isReachableByPing(String host) {
      try{
              String cmd = "";
              if(System.getProperty("os.name").startsWith("Windows")) {  
                      // For Windows
                      cmd = "ping -n 1 " + host;
              } else {
                      // For Linux and OSX
                      cmd = "ping -c 1 " + host;
              }

              Process myProcess = Runtime.getRuntime().exec(cmd);
              myProcess.waitFor();

              if(myProcess.exitValue() == 0) {

                      return true;
              } else {

                      return false;
              }

      } catch( Exception e ) {

              e.printStackTrace();
              return false;
      }
}
   
   /**
    * Gets the IP address.
    * 
    * @return the IP address
    */
   public String getIPAddr() {
      return ipAddr;
   }

   /**
    * Sets the IP address.
    * 
    * @param ipAddr the new IP address
    */
   public void setIPAddr(String ipAddr) {
      this.ipAddr = ipAddr;
   }

   @Override
   public String read(Sensor sensor) {
      // TODO Auto-generated method stub
      if (isReachableByPing(ipAddr)){
         return "on";
      }
      else{
         return "off";
      }
   }

}
