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

import javax.speech.EngineManager;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerMode;

import org.jdom.Element;
import org.jvoicexml.jsapi2.jse.synthesis.freetts.FreeTTSEngineListFactory;
import org.openremote.controller.Constants;
import org.openremote.controller.command.Command;
import org.openremote.controller.command.CommandBuilder;
import org.openremote.controller.utils.CommandUtil;
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
         // Create a synthesizer
         //System.setProperty("freetts.voices", "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");
         EngineManager.registerEngineListFactory(FreeTTSEngineListFactory.class.getName());
         synth = (Synthesizer) EngineManager.createEngine(SynthesizerMode.DEFAULT);
         synth.allocate();
         synth.resume();
         synth.waitEngineState(Synthesizer.RESUMED);
      } catch (Exception e) {
         logger.error("FreeTTS: Failed to create speech synthesizer");
         e.printStackTrace();
      }
      
      String text = null;
      
      for (Element ele : propertyEles) {
         String elementName = ele.getAttributeValue(CommandBuilder.XML_ATTRIBUTENAME_NAME);
         String elementValue = ele.getAttributeValue(CommandBuilder.XML_ATTRIBUTENAME_VALUE);

         if (STR_ATTRIBUTE_NAME_TEXT.equals(elementName)) {
            text = CommandUtil.parseStringWithParam(element, elementValue);
            logger.debug("FreeTTS command: text = " + text);
         }
      }

      logger.debug("FreeTTS command created successfully");
      return new FreeTTSCommand(synth, text);
   }

}
