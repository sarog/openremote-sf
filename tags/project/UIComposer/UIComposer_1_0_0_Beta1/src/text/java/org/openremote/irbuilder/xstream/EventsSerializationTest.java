package org.openremote.irbuilder.xstream;

import org.junit.Test;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;

/**
 * @author allen.wei
 */

public class EventsSerializationTest {

    @Test
    public void testControllerWrapperToXml() {
        XStream stream = new XStream();
        stream.autodetectAnnotations(true);
        System.out.println(stream.toXML(TestData.getControllerWrapper()));
    }

    @Test
    public void testControllerWrapperToJSON() {
        XStream stream = new XStream(new JettisonMappedXmlDriver());
        stream.autodetectAnnotations(true);
        System.out.println(stream.toXML(TestData.getControllerWrapper()));
    }

     @Test
    public void testEmptyControllerWrapperoJSON() {
        XStream stream = new XStream(new JettisonMappedXmlDriver());
        stream.autodetectAnnotations(true);
        System.out.println(stream.toXML(TestData.getEmptyControllerWrapper()));
    }

}
