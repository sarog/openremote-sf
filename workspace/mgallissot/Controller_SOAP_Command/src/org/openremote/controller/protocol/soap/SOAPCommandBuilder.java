/**
 * 
 */
package org.openremote.controller.protocol.soap;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.axis.client.Call;
import org.jdom.Element;
import org.openremote.controller.command.Command;
import org.openremote.controller.command.CommandBuilder;

/**
 * @author gallissot
 *
 */
public class SOAPCommandBuilder implements CommandBuilder {

   private static final String SOAP_XMLPROPERTY_WSDL = "wsdl";
   private static final String SOAP_XMLPROPERTY_PORTNAME = "portName";
   private static final String SOAP_XMLPROPERTY_OPERATIONNAME = "operationName";
   private static final String SOAP_XMLPROPERTY_TIMEOUT = "timeout";

   @SuppressWarnings("unchecked")
   @Override
   public Command build(Element element) {
      String portName = null;
      String operationName = null;
      DynamicInvoker dynamicInvoker = null;
      boolean returnsValue = false;
      int timeout = 15;
      HashMap<String, String> soapOperationArguments = new HashMap<String, String>(3);
      
      List<Element> propertyElements = element.getChildren("property", element.getNamespace());
      
      
      for (Element el : propertyElements)
      {
        String soapPropertyName = el.getAttributeValue(XML_ATTRIBUTENAME_NAME);
        String soapPropertyValue = el.getAttributeValue(XML_ATTRIBUTENAME_VALUE);

        if (SOAP_XMLPROPERTY_WSDL.equalsIgnoreCase(soapPropertyName))
        {
          try {
            dynamicInvoker = new DynamicInvoker(soapPropertyValue);
         } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }
        }
        else if (SOAP_XMLPROPERTY_PORTNAME.equalsIgnoreCase(soapPropertyName))
        {
          portName = soapPropertyValue;
        }
        else if (SOAP_XMLPROPERTY_OPERATIONNAME.equalsIgnoreCase(soapPropertyName))
        {
           operationName = soapPropertyValue;
        }
        else if (SOAP_XMLPROPERTY_TIMEOUT.equalsIgnoreCase(soapPropertyName))
        {
           timeout = Integer.parseInt(soapPropertyValue);
        }
        else
        {
          // anything that's not a device or action property will be taken as an operation argument...

          soapOperationArguments.put(soapPropertyName, soapPropertyValue);
          
          //if anything starting with "out", we assume it is a status command
          if(soapPropertyName.startsWith("out"))
          {
             returnsValue = true;
          }
        }
      }
      
      return new SOAPCommand(dynamicInvoker, portName, operationName, soapOperationArguments, returnsValue, timeout);
      
   }

}
