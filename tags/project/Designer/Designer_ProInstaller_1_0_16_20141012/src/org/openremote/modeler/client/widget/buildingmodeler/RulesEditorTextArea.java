/* OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2013, OpenRemote Inc.
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
package org.openremote.modeler.client.widget.buildingmodeler;

import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.google.gwt.user.client.Element;

/**
 * Subclass of TextArea formatting it as desired for use as the rules editor field.
 * 
 * @author <a href="mailto:eric@openremote.org">Eric Bariaux</a>
 */
public class RulesEditorTextArea extends TextArea {

  @Override
  protected void onRender(Element target, int index) {
    super.onRender(target, index);
    El el = this.getInputEl();
    el.setElementAttribute("wrap", "off");
  }

  @Override
  protected void onResize(int width, int height) {
    super.onResize(width, height);
    // Super class does re-set width attribute on each call, so we overwrite here to ensure it always occupies full width.
    El el = this.getInputEl();
    el.setStyleAttribute("width", "100%");
  }
  
}