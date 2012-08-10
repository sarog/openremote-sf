/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2012, OpenRemote Inc.
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
package org.openremote.modeler.client.widget.propertyform;

/**
 * A PropertyFormExtension can be registered with a PropertyForm in order to provide additional behavior,
 * mostly by inserting fields in the form.
 * 
 * @author <a href = "mailto:eric@openremote.org">Eric Bariaux</a>
 */
public interface PropertyFormExtension {

  /**
   * Called by the form to install install itself.
   */
  void install(PropertyForm form);
  
  /**
   * Called after the form has been removed from the form panel.
   * The extension should perform all necessary clean up so it can be safely disposed (e.g. removing itself as listener from some objects).
   */
  void cleanup(PropertyForm form);
  
}
