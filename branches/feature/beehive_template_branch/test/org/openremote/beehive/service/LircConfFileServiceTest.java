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
package org.openremote.beehive.service;

import org.openremote.beehive.SpringTestContext;
import org.openremote.beehive.TestBase;
import org.openremote.beehive.api.service.ModelService;

public class LircConfFileServiceTest extends TestBase {

    private ModelService service = (ModelService) SpringTestContext.getInstance().getBean("modelService");

//	public void testAddLircConfFile() {
//		StopWatch watch = new StopWatch();
//		watch.start();
//		service.add(FileUtil.readStream("C:\\remotes\\3m\\MP8640"),"3m","MP8640");//single section
//		service.add(FileUtil.readStream("C:\\remotes\\sigma_designs\\lircd.conf.realmagic"),"sigma_designs","lircd.conf.realmagic");//multi section
//		service.add(FileUtil.readStream("C:\\remotes\\zenith\\ZN110"),"zenith","ZN110");//raw code
//		watch.stop();
//		System.out.println(watch.getTime());
//	}

    public void testScrap() {
        assertTrue(true);
//		LircConfFileScraper.scrapDir("C:\\remotes");
    }



}
