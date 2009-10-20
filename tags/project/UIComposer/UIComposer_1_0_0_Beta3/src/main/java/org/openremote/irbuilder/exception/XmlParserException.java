/**
 * 
 */
package org.openremote.irbuilder.exception;

/**
 * Custom Exception when parser xml faild.
 * 
 * @author Tomsky
 *
 */
public class XmlParserException extends RuntimeException {
   public XmlParserException(String s) {
      super(s);
   }

   public XmlParserException(String s, Throwable throwable) {
      super(s, throwable);
   }
}
