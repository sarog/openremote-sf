/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2010, OpenRemote Inc.
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
package org.openremote.modeler.client.widget.buildingmodeler.protocol;

import java.util.List;

import org.openremote.modeler.domain.ProtocolAttr;

import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;

/**
 * Base ProtocolFieldSet for creating a FieldSet, which includes some fields to define the protocol attributes. 
 * Must be sub classed to do something useful.
 * 
 */
public abstract class AbstractProtocolFieldSet extends FieldSet {

   /**
    * Instantiates the ProtocolFieldSet's default style.
    * Adds sub fields into the ProtocolFieldSet.
    */
   public AbstractProtocolFieldSet() {
      FormLayout layout = new FormLayout();
      layout.setLabelWidth(80);
      setLayout(layout);
      setHeading(getProtocolDisplayName() + " attributes");
      initFields();
   }
   
   /**
    * Returns the defined protocol type, mustn't be null.
    * 
    */
   protected abstract String getProtocolType();
   
   /**
    * Returns the defined protocol displayName, mustn't be null.
    * 
    */
   protected abstract String getProtocolDisplayName();
   
   /**
    * Instantiates the fields and adds them into the ProtocolFieldSet.
    * 
    * The fields equals the protocol's attributes.
    * Each field must sets name as the attribute's name, and supports 
    * method "getValue().toString()" as the attribute's value.
    * At last, add the field into the ProtocolFieldSet. <p>
    * 
    * e.g:
    * <pre>{@code
    * TextField<String> colorField = new TextField<String>();
    * colorField.setFieldLabel("Color");
    * colorField.setName("color");
    * add(colorField);
    * }</pre>
    */
   protected abstract void initFields();
   
   /**
    * Instantiates the filed values by the stored command's protocol attributes.
    * If protocolAttrs is null, clear all field's value.
    * 
    * <pre>{@code
    *    if (protocolAttrs == null) {
    *       colorField.clear();
    *    } else {
    *       for (ProtocolAttr protocolAttr : protocolAttrs) {
    *          if (colorField.getName().equals(protocolAttr.getName())) {
    *             colorField.setValue(protocolAttr.getValue());
    *          }
    *       }
    *    }
    * </pre>
    */
   public abstract void initFiledValuesByProtocol(List<ProtocolAttr> protocolAttrs);
}
