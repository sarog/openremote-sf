/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2011, OpenRemote Inc.
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
package org.openremote.modeler.service;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.SAXValidator;
import org.dom4j.io.XMLWriter;
import org.dom4j.util.XMLErrorHandler;
import org.openremote.modeler.exception.ParseOpenWebNetException;
import org.openremote.modeler.openwebnet.OpenWebNetCommandType;
import org.openremote.modeler.openwebnet.OpenWebNetField;
import org.openremote.modeler.openwebnet.OpenWebNetWho;
import org.openremote.modeler.protocol.ProtocolValidator;
import org.xml.sax.SAXException;

/**
 * Parses the OpenWebNet definition xml file in classpath/openwebnet.
 *
 * @author Marco Miccini
 */
@SuppressWarnings("unchecked")
public class OpenWebNetParser
{
   /** The Constant WHO_ELEMENT_NAME. */
   private static final String WHO_ELEMENT_NAME = "who";

   /** The Constant OPENWEBNET_DIR. */
   private static final String OPENWEBNET_DIR = "/openwebnet/";

   /** The Constant OPENWEBNET_FILE. */
   private static final String OPENWEBNET_FILE = "OpenWebNetWindow.xml";

   /** The Constant OPENWEBNET_XSD_FILE_NAME. */
   private static final String OPENWEBNET_XSD_FILE_NAME = "openwebnet.xsd";

   /** The Constant COMMAND_TYPE_ELEMENT_NAME. */
   private static final String COMMAND_TYPE_ELEMENT_NAME = "commandtype";

   /** The Constant FIELD_ELEMENT_NAME. */
   private static final String FIELD_ELEMENT_NAME = "field";

   /** The Constant VALUE_ELEMENT_NAME. */
   private static final String VALUE_ELEMENT_NAME = "value";

   /** The Constant VALUE_ATTR_NAME. */
   private static final String VALUE_ATTR_NAME = "value";

   /** The Constant NAME_ATTR_NAME. */
   private static final String NAME_ATTR_NAME = "name";

   /** The Constant OPTIONAL_ATTR_NAME. */
   private static final String OPTIONAL_ATTR_NAME = "optional";

   /** The xml path. */
   private String xmlPath = "";

   /**
    * Parses the xml.
    *
    * @return the hash map <string, OpenWebNet who>
    */
   public HashMap<String, OpenWebNetWho> parseXml()
   {
      HashMap<String, OpenWebNetWho> definitionHashMap = new HashMap<String, OpenWebNetWho>();
      File file = new File(getPath());

      Document protocolDoc = readXmlFromFile(file);

      Element openremoteElement = protocolDoc.getRootElement();
      Iterator<Element> whoItr = openremoteElement.elementIterator(WHO_ELEMENT_NAME);
      while (whoItr.hasNext())
      {
         Element whoElement = whoItr.next();
         OpenWebNetWho who = new OpenWebNetWho();
         // set who value
         who.setValue(whoElement.attributeValue(VALUE_ATTR_NAME));

         // parse command type element start
         who.getCommandTypes().addAll(parseCommandTypes(whoElement));
         definitionHashMap.put(who.getValue(), who);
      }

      return definitionHashMap;
   }

   /**
    * Parses the command types.
    *
    * @param who the who
    *
    * @return the list<OWN who definition>
    */
   private List<OpenWebNetCommandType> parseCommandTypes(Element who)
   {
      List<OpenWebNetCommandType> commandTypes = new ArrayList<OpenWebNetCommandType>();
      Iterator<Element> commandTypeItr = who.elementIterator(COMMAND_TYPE_ELEMENT_NAME);
      while (commandTypeItr.hasNext())
      {
         Element commandType = commandTypeItr.next();
         OpenWebNetCommandType commandTypeDefinition = new OpenWebNetCommandType();
         commandTypeDefinition.setName(commandType.attributeValue(NAME_ATTR_NAME));

         // parse fields start
         commandTypeDefinition.getFields().addAll(parseFields(commandType));
         commandTypes.add(commandTypeDefinition);
      }

      return commandTypes;
   }

   /**
    * Parses the fields.
    *
    * @param fieldElement the field element
    *
    * @return the list<field>
    */
   private List<OpenWebNetField> parseFields(Element fieldElement)
   {
      List<OpenWebNetField> fields = new ArrayList<OpenWebNetField>();
      Iterator<Element> fieldItr = fieldElement.elementIterator(FIELD_ELEMENT_NAME);
      while (fieldItr.hasNext())
      {
         Element field = fieldItr.next();
         OpenWebNetField fieldDefinition = new OpenWebNetField();
         fieldDefinition.setName(field.attributeValue(NAME_ATTR_NAME));
         fieldDefinition.setOptional(field.attributeValue(OPTIONAL_ATTR_NAME));

         // parse values start
         fieldDefinition.getValues().addAll(parseValues(field));
         fields.add(fieldDefinition);
      }
      return fields;
   }

   /**
    * Parses the values.
    *
    * @param valueElement the value element
    *
    * @return the list<value>
    */
   private List<ProtocolValidator> parseValues(Element valueElement)
   {
      List<ProtocolValidator> values = new ArrayList<ProtocolValidator>();
      Iterator<Element> valueItr = valueElement.elementIterator(VALUE_ELEMENT_NAME);
      while (valueItr.hasNext())
      {
         Element value = valueItr.next();
         ProtocolValidator valueDefinition = new ProtocolValidator(ProtocolValidator.REGEX_TYPE, value.getTextTrim().replace(" ", ""), null);
         values.add(valueDefinition);
      }
      return values;
   }

   /**
    * Read xml from file.
    *
    * @param file the file
    *
    * @return the document
    */
   private Document readXmlFromFile(File file)
   {
      Document protocolDoc = null;
      SAXReader reader = new SAXReader();
      XMLErrorHandler errorHandler = new XMLErrorHandler();
      SAXParserFactory factory = SAXParserFactory.newInstance();
      factory.setValidating(true);
      factory.setNamespaceAware(true);
      try
      {
         protocolDoc = reader.read(file);
         SAXParser parser = factory.newSAXParser();
         parser.setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema");
         parser.setProperty("http://java.sun.com/xml/jaxp/properties/schemaSource", "file:" + this.getClass().getResource("/").getPath().toString() + OPENWEBNET_XSD_FILE_NAME);
         SAXValidator validator = new SAXValidator(parser.getXMLReader());
         validator.setErrorHandler(errorHandler);

         validator.validate(protocolDoc);

         XMLWriter writer = new XMLWriter(OutputFormat.createPrettyPrint());

         if (errorHandler.getErrors().hasContent())
         {
            writer.write(errorHandler.getErrors());
            throw new ParseOpenWebNetException("validate xml schema on File " + file.getAbsolutePath() + " fail.");
         }
      }
      catch (ParserConfigurationException e)
      {
         throw new ParseOpenWebNetException("Read xml from File " + file.getAbsolutePath() + " occur ParserConfigurationException.", e);
      }
      catch (SAXException e)
      {
         throw new ParseOpenWebNetException("Read xml from File " + file.getAbsolutePath() + " occur SAXException.", e);
      }
      catch (UnsupportedEncodingException e)
      {
         throw new ParseOpenWebNetException("Read xml from File " + file.getAbsolutePath() + " occur UnsupportedEncodingException.", e);
      }
      catch (IOException e)
      {
         throw new ParseOpenWebNetException("Read xml from File " + file.getAbsolutePath() + " occur IOException.", e);
      }
      catch (DocumentException e)
      {
         throw new ParseOpenWebNetException("Read xml from File " + file.getAbsolutePath() + " occur DocumentException.", e);
      }
      return protocolDoc;
   }

   /**
    * Gets the path.
    *
    * @return the path
    */
   private String getPath()
   {
      if (xmlPath != null && xmlPath.length() > 0)
         return xmlPath;
      else
         return this.getClass().getResource(OPENWEBNET_DIR + OPENWEBNET_FILE).getPath();
   }

   /**
    * Sets the path.
    *
    * @param path the new path
    */
   public void setPath(String path)
   {
      xmlPath = path;
   }
}
