package org.openremote.modeler.protocol;

import java.io.Serializable;

public class ProtocolValidator implements Serializable {

   public static final int ALLOW_BLANK_TYPE = 1;
   public static final int MAX_LENGTH_TYPE = 2;
   public static final int MIN_LENGTH_TYPE = 3;
   public static final int REGEX_TYPE = 4;

   public static final String ALLOW_BLANK = "allowBlank";
   public static final String MAX_LENGTH = "maxLength";
   public static final String MIN_LENGTH = "minLength";
   public static final String REGEX = "regex";

   public static final String ALLOW_BLANK_MESSAGE = "This field is not allow blank.";
   public static final String MAX_LENGTH_MESSAGE = "Max length of this field is ";
   public static final String MIN_LENGTH_MESSAGE = "Min length of this field is ";
   public static final String REGEX_MESSAGE = "This field must accord with regex '";

   /**
    * 
    */
   private static final long serialVersionUID = 8253342291315353016L;
   private String _message = null;
   private String _value = null;
   private int _type = -1;

   public ProtocolValidator() {

   }

   public ProtocolValidator(int type, String value, String message) {
      if (message != null && message.length() > 0) {
         setMessage(message);
      } else {
         switch (type) {
         case ALLOW_BLANK_TYPE:
            setMessage(ALLOW_BLANK_MESSAGE);
            break;
         case MAX_LENGTH_TYPE:
            setMessage(MAX_LENGTH_MESSAGE + value.toString());
            break;
         case MIN_LENGTH_TYPE:
            setMessage(MIN_LENGTH_MESSAGE + value.toString());
            break;
         case REGEX_TYPE:
            setMessage(REGEX_MESSAGE+ value.toString()+"'");
            break;
         }
      }
      setValue(value);
      setType(type);

   }

   public int getType() {
      return _type;
   }

   public void setType(int type) {
      this._type = type;
   }

   public String getMessage() {
      return _message;
   }

   public void setMessage(String message) {
      this._message = message;
   }

   public String getValue() {
      return _value;
   }

   public void setValue(String value) {
      this._value = value;
   }

   public boolean validate(String testData) {
      return false;

   }

}
