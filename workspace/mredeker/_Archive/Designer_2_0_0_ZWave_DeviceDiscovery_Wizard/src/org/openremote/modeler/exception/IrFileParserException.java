package org.openremote.modeler.exception;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Throws exceptions related to IR file parsing and processing
 * @author wbalcaen
 *
 */
public class IrFileParserException extends Exception implements IsSerializable {

   private static final long serialVersionUID = 1L;
   private String message = "";

   /**
    * mandatory for gWT
    */
   public IrFileParserException() {

   }

   /** create a new Exception with a message
    * @param message
    */
   public IrFileParserException(String message) {
      super(message);
      this.message = message;
   }

   public IrFileParserException(Throwable cause) {
      super(cause);
   }

   /* (non-Javadoc)
    * @see java.lang.Throwable#getMessage()
    */
   @Override
   public String getMessage() {
      return message;
   }

   /** Sets the exception message
    * @param message
    */
   public void setMessage(String message) {
      this.message = message;
   }

}