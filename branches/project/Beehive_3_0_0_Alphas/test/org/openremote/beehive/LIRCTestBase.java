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
package org.openremote.beehive;

import java.util.ArrayList;
import java.util.List;

import org.openremote.beehive.api.service.impl.GenericDAO;
import org.openremote.beehive.domain.Code;
import org.openremote.beehive.domain.Model;
import org.openremote.beehive.domain.RemoteOption;
import org.openremote.beehive.domain.RemoteSection;
import org.openremote.beehive.domain.Vendor;

/**
 * Test base for lirc-related test
 * 
 * @author Dan Cong
 *
 */

public class LIRCTestBase extends TestBase {
   
   
   protected void setUp() throws Exception {
      super.setUp();
      GenericDAO genericDAO = (GenericDAO) SpringTestContext.getInstance().getBean("genericDAO");
      Vendor v = new Vendor();
      v.setName("3m");
      
      String comment = "#\r\n# This config file has been automatically converted from a device "
            + "file\r\n# found in the 06/26/00 release of the Windows Slink-e software\r\n# "
            + "package.\r\n#\r\n# Many thanks to Colby Boles of Nirvis Systems Inc. for allowing "
            + "us to\r\n# use these files.\r\n#\r\n# The original filename was: \"3m mp8640 3m "
            + "lcd projector.cde\"\r\n#\r\n# The original description for this device was:"
            + "\r\n#\r\n# 3m mp8640 3m lcd projector\r\n#\r\n";
      Model m = new Model();
      m.setComment(comment);
      m.setFileName("MP8640");
      m.setName("MP8640");
      m.setVendor(v);
      List<Model> ms = new ArrayList<Model>();
      ms.add(m);
      v.setModels(ms);
      
      
      RemoteSection rs = new RemoteSection();
      rs.setModel(m);
      rs.setName("MP8640");
      rs.setComment(comment);
      
      RemoteOption ro1 = new RemoteOption();
      ro1.setName("name");
      ro1.setValue("MP8640");
      ro1.setRemoteSection(rs);
      RemoteOption ro2 = new RemoteOption();
      ro2.setName("bits");
      ro2.setValue("32");
      ro2.setRemoteSection(rs);
      RemoteOption ro3 = new RemoteOption();
      ro3.setName("flags");
      ro3.setValue("SPACE_ENC");
      ro3.setRemoteSection(rs);
      
      List<RemoteOption> ros = new ArrayList<RemoteOption>();
      
      ros.add(ro1);
      ros.add(ro2);
      ros.add(ro3);
      rs.setRemoteOptions(ros);
      
      List<RemoteSection> rss = new ArrayList<RemoteSection>();
      rss.add(rs);
      m.setRemoteSections(rss);
      
      Code c1 = new Code();
      c1.setName("STANDBY/ON");
      c1.setValue("0x000000000AF5E817");
      c1.setRemoteSection(rs);
      Code c2 = new Code();
      c2.setName("VOLUME/\\");
      c2.setValue("0x000000000AF548B7");
      c2.setRemoteSection(rs);
      Code c3 = new Code();
      c3.setName("VOLUME\\/");
      c3.setValue("0x000000000AF5A857");
      c2.setRemoteSection(rs);
      
      List<Code> cs = new ArrayList<Code>();
      cs.add(c1);
      cs.add(c2);
      cs.add(c3);
      
      rs.setCodes(cs);
      
      genericDAO.save(v);
      
      genericDAO.save(m);
   }

}
