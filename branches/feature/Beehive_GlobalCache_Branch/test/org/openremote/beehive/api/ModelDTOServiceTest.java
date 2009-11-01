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
package org.openremote.beehive.api;

import junit.framework.TestCase;

import org.openremote.beehive.api.service.ModelService;
import org.openremote.beehive.spring.SpringContext;

/**
 * @author allen.wei 2009-2-17
 */
public class ModelDTOServiceTest extends TestCase {

    private ModelService service = (ModelService) SpringContext
            .getInstance().getBean("modelService");

    public void testExportTest() {
        System.out.println(service.exportText(1));
    }
}
