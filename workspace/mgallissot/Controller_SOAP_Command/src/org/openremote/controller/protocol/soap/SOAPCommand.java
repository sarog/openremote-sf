/**
 * 
 */
package org.openremote.controller.protocol.soap;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.openremote.controller.command.ExecutableCommand;
import org.openremote.controller.command.StatusCommand;
import org.openremote.controller.component.EnumSensorType;
import org.w3c.dom.Element;


/**
 * @author gallissot
 *
 */
public class SOAPCommand implements ExecutableCommand, StatusCommand {

   private DynamicInvoker invoker;
   private String operationName;
   private HashMap<String, String> args;
   private String portName;
   private int timeout;

   public SOAPCommand(DynamicInvoker invoker, String portName, String operationName, HashMap args, boolean returnsValue, int timeout) {
      this.invoker = invoker;
      this.operationName = operationName;
      this.args = args;
      this.portName = portName;
      this.timeout = timeout;
   }

   @Override
   public void send() {
      try {
         this.invoker.invokeMethod(operationName, portName, args, this.timeout);
      } catch (Exception e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }

   @Override
   public String read(EnumSensorType sensorType, Map<String, String> stateMap) {      
      try {
         HashMap map = this.invoker.invokeMethod(operationName, portName, args, this.timeout);
         for (Iterator it = map.entrySet().iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            String key = (String) entry.getKey();
            Object value = entry.getValue();
            if (value instanceof Element) {
               return ((Element) value).getNodeValue();
            } else {
                return value.toString();
            }
        }
      } catch (Exception e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      return null;
   }

}
