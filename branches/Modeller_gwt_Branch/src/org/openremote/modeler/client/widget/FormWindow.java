/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2009, OpenRemote Inc.
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
package org.openremote.modeler.client.widget;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;

/**
 * The Class FormWindow.
 */
public class FormWindow extends Window {

   /** The form. */
   protected FormPanel form = new FormPanel();

   /**
    * Instantiates a new form window.
    */
   public FormWindow() {
      setLayout(new FillLayout());
      setModal(true);
      setBodyBorder(false);
      form.setFrame(true);
      form.setHeaderVisible(false);
      form.setBorders(false);
      form.setButtonAlign(HorizontalAlignment.CENTER);
   }

   /*
    * (non-Javadoc)
    * 
    * @see com.extjs.gxt.ui.client.widget.Window#show()
    */
   @Override
   public void show() {
      setFocusWidget(form.getWidget(0));
      super.show();
   }

}
