package org.openremote.controller.control.gesture;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.openremote.controller.command.NoStatusCommand;
import org.openremote.controller.command.StatusCommand;
import org.openremote.controller.spring.SpringContext;
import org.openremote.controller.utils.XMLUtil;

public class GestureBuilderTest extends TestCase {
   private String controllerXMLPath = null;
   private Document doc = null;
   private GestureBuilder builder = (GestureBuilder) SpringContext.getInstance().getBean("gestureBuilder");
   
   protected void setUp() throws Exception {
      controllerXMLPath = this.getClass().getClassLoader().getResource("./fixture/controller.xml").getFile();
      doc = XMLUtil.getDocument(controllerXMLPath);
      super.setUp();
   }

   protected Element getElementByID(String id) throws JDOMException {
      return XMLUtil.getElementByID(doc, id);
   }
   
   public Gesture getGestureByID(String labelID) throws JDOMException{
      Element controlElement = getElementByID(labelID);
      if(! controlElement.getName().equals("gesture")) {
         //throw new NoSuchComponentException();
         return null;
      }
      return (Gesture) builder.build(controlElement, "test");
   }
   
   public void testGetLabelforRealID() throws JDOMException{
      Gesture gesture = getGestureByID("7");
      Assert.assertNotNull(gesture);
   }
   
   public void testGetLabelforNoSuchID() throws JDOMException{
      boolean error = true;
      try{
         Gesture label = getGestureByID("8");
         error = false;
         StatusCommand s = label.getStatus().getStatusCommand();
         Assert.assertTrue(s instanceof NoStatusCommand);
      } catch (Exception e){
      }
      Assert.assertEquals(error, true);
   }
   
   
}
