/*
 * OpenRemote, the Home of the Digital Home. Copyright 2008-2013, OpenRemote Inc.
 * 
 * See the contributors.txt file in the distribution for a full listing of individual contributors.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package org.openremote.controller.protocol.freetts;

import java.util.List;
import java.util.Locale;

import javax.speech.Central;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerModeDesc;

import org.jdom.Element;
import org.openremote.controller.Constants;
import org.openremote.controller.command.Command;
import org.openremote.controller.command.CommandBuilder;
import org.openremote.controller.utils.Logger;

/**
 * Command builder for text to speech command.
 * The only parameter is the text to speak.
 * 
 * @author Lawrie Griffiths
 *
 */
public class FreeTTSCommandBuilder implements CommandBuilder {
   
   // Constants ------------------------------------------------------------------------------------
   
   public final static String FREETTS_PROTOCOL_LOG_CATEGORY = Constants.CONTROLLER_PROTOCOL_LOG_CATEGORY + "freetts";
   
   private final static String STR_ATTRIBUTE_NAME_TEXT = "text";
   
   // Class Members --------------------------------------------------------------------------------
   
   private final static Logger logger = Logger.getLogger(FREETTS_PROTOCOL_LOG_CATEGORY);
   
   // Instance Fields
   // ----------------------------------------------------------
   
   private Synthesizer synth;
   
   // Implements CommandBuilder --------------------------------------------------------------------

   @SuppressWarnings("unchecked")
   @Override
   public Command build(Element element) {
      logger.debug("Building FreeTTS command");
      List<Element> propertyEles = element.getChildren("property", element.getNamespace());
      
      // Create a speech synthesizer and start it
      try {
         // Create a synthesizer for English
         System.setProperty("freetts.voices", "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");
         synth = Central.createSynthesizer(new SynthesizerModeDesc(Locale.ENGLISH));
         synth.allocate();
         synth.resume();
      } catch (Exception e) {
         logger.error("Failed to create speak synthesizer");
      }
      
      String text = null;
      
      for (Element ele : propertyEles) {
         String elementName = ele.getAttributeValue(CommandBuilder.XML_ATTRIBUTENAME_NAME);
         String elementValue = ele.getAttributeValue(CommandBuilder.XML_ATTRIBUTENAME_VALUE);

         if (STR_ATTRIBUTE_NAME_TEXT.equals(elementName)) {
            text = elementValue;
            logger.debug("FreeTTS command: text = " + text);
         }
      }

      logger.debug("FreeTTS command created successfully");
      return new FreeTTSCommand(synth, text);
   }

}
