/*
 * OpenRemote, the Home of the Digital Home. Copyright 2008, OpenRemote Inc.
 * 
 * See the contributors.txt file in the distribution for a full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3.0 of the License, or (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * You should have received a copy of the GNU General Public License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF site:
 * http://www.fsf.org.
 */

package org.openremote.modeler.service;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.openremote.modeler.exception.ParseProtocolException;
import org.openremote.modeler.protocol.ProtocolAttrDefinition;
import org.openremote.modeler.protocol.ProtocolDefinition;
import org.openremote.modeler.protocol.ProtocolValidator;
import org.springframework.util.Assert;

/**
 * @author <a href="mailto:allen.wei@finalist.cn">allen.wei</a>
 */
@SuppressWarnings("unchecked")
public class ProtocolParser {
   private static Logger logger = Logger.getLogger(ProtocolParser.class);

   private static final String PROTOCOLS_DIR = "/protocols";
   private static final String MESSAGE = "message";
   private static final String VALIDATOR_CLASS_NAME_SUFFIX = "Validator";
   private static final String VALIDATIONS = "validations";
   private static final String LABEL = "label";
   private static final String ATTR = "attr";
   private static final String NAME = "name";

   public HashMap<String, ProtocolDefinition> parseXmls() {
      HashMap<String, ProtocolDefinition> definitionHashMap = new HashMap<String, ProtocolDefinition>();
      System.out.println(getPath());
      File dir = new File(getPath());
      for (File file : dir.listFiles(new FileFilter() {

         public boolean accept(File pathname) {
            if (pathname.getName().lastIndexOf(".xml") > 0) {
               return true;
            }
            return false;
         }
      })) {
         ProtocolDefinition definition = parse(file);
         definitionHashMap.put(definition.getName(), definition);
      }
      return definitionHashMap;
   }

   private ProtocolDefinition parse(File file) {
      ProtocolDefinition protocolDefinition = new ProtocolDefinition();
      Document protocolDoc = readXmlFromFile(file);

      Assert.notNull(protocolDoc);

      Element protocol = protocolDoc.getRootElement();

      // set protocol name
      protocolDefinition.setName(protocol.attributeValue(NAME));

      // parse attr element start
      protocolDefinition.getAttrs().addAll(parseAttributs(protocol));
      return protocolDefinition;
   }

   private List<ProtocolAttrDefinition> parseAttributs(Element protocol) {
      List<ProtocolAttrDefinition> attrs = new ArrayList<ProtocolAttrDefinition>();
      Iterator<Element> attrItr = protocol.elementIterator(ATTR);
      while (attrItr.hasNext()) {
         Element attr = attrItr.next();
         ProtocolAttrDefinition attrDefinition = new ProtocolAttrDefinition();
         attrDefinition.setLabel(attr.attributeValue(LABEL));
         attrDefinition.setName(attr.attributeValue(NAME));
         Element validationsElement = attr.element(VALIDATIONS);

         // parse validators start
         attrDefinition.getValidators().addAll(parseValidators(validationsElement));

         attrs.add(attrDefinition);

      }

      return attrs;
   }

   private List<ProtocolValidator> parseValidators(Element validationsElement) {
      List<ProtocolValidator> validators = new ArrayList<ProtocolValidator>();
      
      for (Iterator<Element> validationsItr = validationsElement.elementIterator(); validationsItr.hasNext();) {
         Element validatorElement = validationsItr.next();
         if (getValidatorType(validatorElement.getName()) == -1) {
            logger.error("Can't find validator "+validatorElement.getName());
            throw new ParseProtocolException("Can't find validator "+validatorElement.getName());
         }
         ProtocolValidator protocolValidator = new ProtocolValidator(getValidatorType(validatorElement.getName()),validatorElement.getTextTrim(),validatorElement.attributeValue("message"));
         validators.add(protocolValidator);
      }
      return validators;
   }
   
   private int getValidatorType(String elementName) {
      if (ProtocolValidator.ALLOW_BLANK.equals(elementName)) {
         return ProtocolValidator.ALLOW_BLANK_TYPE;
      } else if (ProtocolValidator.MAX_LENGTH.equals(elementName)) {
         return ProtocolValidator.MAX_LENGTH_TYPE;
      } else if (ProtocolValidator.MAX_LENGTH.equals(elementName)) {
         return ProtocolValidator.MAX_LENGTH_TYPE;
      } else if (ProtocolValidator.REGEX.equals(elementName)) {
         return ProtocolValidator.REGEX_TYPE;
      } else {
         return -1;
      }
   }

   private Document readXmlFromFile(File file) {
      Document protocolDoc = null;
      SAXReader reader = new SAXReader();
      try {
         protocolDoc = reader.read(file);
      } catch (DocumentException e) {
         logger.error("Read xml From File " + file.getAbsolutePath() + " occur DocumentException.", e);
         throw new ParseProtocolException("Read xml From File " + file.getAbsolutePath() + " occur DocumentException.",
               e);
      }
      return protocolDoc;
   }

   private String getPath() {
      return this.getClass().getResource(PROTOCOLS_DIR).getPath();

   }
}
