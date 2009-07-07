/* OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2009, OpenRemote Inc.
 * 
 * See the contributors.txt file in the distribution for a
 * full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3.0 of
 * the License, or (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.openremote.modeler.protocol;

import java.io.Serializable;

// TODO: Auto-generated Javadoc
/**
 * The Class ProtocolValidator.
 */
public class ProtocolValidator implements Serializable {

   /**
    * The Constant ALLOW_BLANK_TYPE.
    */
   public static final int ALLOW_BLANK_TYPE = 1;

   /**
    * The Constant MAX_LENGTH_TYPE.
    */
   public static final int MAX_LENGTH_TYPE = 2;

   /**
    * The Constant MIN_LENGTH_TYPE.
    */
   public static final int MIN_LENGTH_TYPE = 3;

   /**
    * The Constant REGEX_TYPE.
    */
   public static final int REGEX_TYPE = 4;

   /**
    * The Constant ALLOW_BLANK.
    */
   public static final String ALLOW_BLANK = "allowBlank";

   /**
    * The Constant MAX_LENGTH.
    */
   public static final String MAX_LENGTH = "maxLength";

   /**
    * The Constant MIN_LENGTH.
    */
   public static final String MIN_LENGTH = "minLength";

   /**
    * The Constant REGEX.
    */
   public static final String REGEX = "regex";

   /**
    * The Constant ALLOW_BLANK_MESSAGE.
    */
   public static final String ALLOW_BLANK_MESSAGE = "This field is not allow blank.";

   /**
    * The Constant MAX_LENGTH_MESSAGE.
    */
   public static final String MAX_LENGTH_MESSAGE = "Max length of this field is ";

   /**
    * The Constant MIN_LENGTH_MESSAGE.
    */
   public static final String MIN_LENGTH_MESSAGE = "Min length of this field is ";

   /**
    * The Constant REGEX_MESSAGE.
    */
   public static final String REGEX_MESSAGE = "This field must accord with regex '";

   /**
    * The Constant serialVersionUID.
    */
   private static final long serialVersionUID = 8253342291315353016L;

   /**
    * The _message.
    */
   private String _message = null;

   /**
    * The _value.
    */
   private String _value = null;

   /**
    * The _type.
    */
   private int _type = -1;

   /**
    * Instantiates a new protocol validator.
    */
   public ProtocolValidator() {

   }

   /**
    * Instantiates a new protocol validator.
    *
    * @param type    the type
    * @param value   the value
    * @param message the message
    */
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
               setMessage(REGEX_MESSAGE + value.toString() + "'");
               break;
         }
      }
      setValue(value);
      setType(type);

   }

   /**
    * Gets the type.
    *
    * @return the type
    */
   public int getType() {
      return _type;
   }

   /**
    * Sets the type.
    *
    * @param type the new type
    */
   public void setType(int type) {
      this._type = type;
   }

   /**
    * Gets the message.
    *
    * @return the message
    */
   public String getMessage() {
      return _message;
   }

   /**
    * Sets the message.
    *
    * @param message the new message
    */
   public void setMessage(String message) {
      this._message = message;
   }

   /**
    * Gets the value.
    *
    * @return the value
    */
   public String getValue() {
      return _value;
   }

   /**
    * Sets the value.
    *
    * @param value the new value
    */
   public void setValue(String value) {
      this._value = value;
   }

   /**
    * Validate.
    *
    * @param testData the test data
    * @return true, if successful
    */
   public boolean validate(String testData) {
      return false;

   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      ProtocolValidator that = (ProtocolValidator) o;

      if (_type != that._type) return false;
      if (_message != null ? !_message.equals(that._message) : that._message != null) return false;
      if (_value != null ? !_value.equals(that._value) : that._value != null) return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = _message != null ? _message.hashCode() : 0;
      result = 31 * result + (_value != null ? _value.hashCode() : 0);
      result = 31 * result + _type;
      return result;
   }
}
