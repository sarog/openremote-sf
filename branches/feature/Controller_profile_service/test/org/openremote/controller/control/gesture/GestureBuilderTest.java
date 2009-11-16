package org.openremote.controller.control.gesture;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.openremote.controller.exception.NoSuchComponentException;
import org.openremote.controller.utils.SpringContextForTest;
import org.openremote.controller.utils.XMLUtil;

public class GestureBuilderTest extends TestCase {
   private String controllerXMLPath = null;
   private Document doc = null;
   private GestureBuilder builder = (GestureBuilder) SpringContextForTest.getInstance().getBean("gestureBuilder");
   
   protected void setUp() throws Exception {
      controllerXMLPath = this.getClass().getClassLoader().getResource("./fixture/controller.xml").getFile();
      doc = XMLUtil.getDocument(controllerXMLPath);
      super.setUp();
   }

   protected Element getElementByID(String id) throws JDOMException {
      return XMLUtil.getElementByID(doc, id);
   }
   
   private Gesture getGestureByID(String labelID) throws JDOMException{
      Element controlElement = getElementByID(labelID);
      if(! controlElement.getName().equals("gesture")) {
         throw new NoSuchComponentException("Invalid Gesture.");
      }
      return (Gesture) builder.build(controlElement, "test");
   }
   
   public void testGetLabelforRealID() throws JDOMException{
      Gesture gesture = getGestureByID("7");
      Assert.assertNotNull(gesture);
   }
   
   public void testGetLabelforInvalidGesture() throws JDOMException{
      try{
         getGestureByID("8");
         fail();
      } catch (NoSuchComponentException e){
      }
   }
   
   public void testGetLabelforNoSuchID() throws JDOMException{
      try{
         getGestureByID("200");
         fail();
      } catch (NoSuchComponentException e){
      }
   }
   
   
}
