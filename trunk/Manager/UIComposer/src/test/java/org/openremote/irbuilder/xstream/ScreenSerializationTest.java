/* OpenRemote, the Home of the Digital Home.
 * Copyright 2008, OpenRemote Inc.
 *
 * See the contributors.txt file in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3.0 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * You should have received a copy of the GNU General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.openremote.irbuilder.xstream;

import org.junit.Test;
import org.junit.Assert;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver; 
/**
 * @author allen.wei
 */
public class ScreenSerializationTest {

    @Test
    public void testActivityToXml() {
        XStream stream = new XStream();
        stream.autodetectAnnotations(true);
       Assert.assertNotSame("",stream.toXML(MockData.getActivity()),"ActivityToXml");
    }

    @Test
    public void testActivityToJSON() {
        XStream stream = new XStream(new JettisonMappedXmlDriver());
        stream.autodetectAnnotations(true);
       Assert.assertNotSame("",stream.toXML(MockData.getActivity()),"ActivityToJSON");

    }
}
