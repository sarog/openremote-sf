package org.openremote.irbuilder.xstream;

import org.junit.Test;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver; /**
 * Created by IntelliJ IDEA.
 * User: finalist
 * Date: Mar 13, 2009
 * Time: 1:38:06 PM
 * To change this template use File | Settings | File Templates.
 */

/**
 * @author allen.wei
 */
public class ScreenSerializationTest {

    @Test
    public void testActivityToXml() {
        XStream stream = new XStream();
        stream.autodetectAnnotations(true);
        System.out.println(stream.toXML(TestData.getActivity()));
    }

    @Test
    public void testActivityToJSON() {
        XStream stream = new XStream(new JettisonMappedXmlDriver());
        stream.autodetectAnnotations(true);
        System.out.println(stream.toXML(TestData.getActivity()));
    }
}
