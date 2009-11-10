package org.openremote.controller.label;

import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;
import org.openremote.controller.Constants;
import org.openremote.controller.command.NoStatusCommand;
import org.openremote.controller.command.StatusCommand;
import org.openremote.controller.control.label.Label;
import org.openremote.controller.control.label.LabelBuilder;
import org.openremote.controller.exception.NoSuchComponentException;
import org.openremote.controller.spring.SpringContext;

public class LabelBuilderTest extends TestCase {
   private String controllerXMLPath = null;
   private Document doc = null;
   private LabelBuilder builder = (LabelBuilder) SpringContext.getInstance().getBean("labelBuilder");
   
   protected void setUp() throws Exception {
      controllerXMLPath = this.getClass().getClassLoader().getResource("./fixture/controller.xml").getFile();
      SAXBuilder builder = new SAXBuilder();
      doc = builder.build(controllerXMLPath);
      super.setUp();
   }

   @SuppressWarnings("unchecked")
   protected Element getElementByID(String id) throws JDOMException {
      String xpath = "//" + Constants.OPENREMOTE_NAMESPACE + ":*[@id='" + id + "']";
      XPath xPath = XPath.newInstance(xpath);
      xPath.addNamespace(Constants.OPENREMOTE_NAMESPACE, Constants.OPENREMOTE_WEBSITE);
      List<Element> elements = xPath.selectNodes(doc);
      if (elements.size() > 1) {
         throw new RuntimeException("duplicated id :" + id);
      } else if (elements.size() == 0) {
         throw new NoSuchComponentException();
      }
      return elements.get(0);
   }
   
   public Label getLabelByID(String labelID) throws JDOMException{
      Element controlElement = getElementByID(labelID);
      return (Label) builder.build(controlElement, "test");
   }
   
   public void testGetLabelforRealID() throws JDOMException{
      Label label = getLabelByID("6");
      Assert.assertNotNull(label);
   }
   
   public void testGetLabelforNoSuchID() throws JDOMException{
      boolean error = true;
      try{
         Label label = getLabelByID("8");
         error = false;
         StatusCommand s = label.getStatus().getStatusCommand();
         Assert.assertTrue(s instanceof NoStatusCommand);
      } catch (Exception e){
      }
      Assert.assertEquals(error, true);
   }
}
