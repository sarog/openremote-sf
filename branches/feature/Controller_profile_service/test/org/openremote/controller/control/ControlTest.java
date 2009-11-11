package org.openremote.controller.control;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openremote.controller.control.gesture.GestureBuilderTest;
import org.openremote.controller.control.label.LabelBuilderTest;
import org.openremote.controller.control.slider.SliderBuilderTest;

public class ControlTest {

   public static Test suite() {

      TestSuite suite = new TestSuite("<------------Test For Control---------------->");
      suite.addTestSuite(GestureBuilderTest.class);
      suite.addTestSuite(SliderBuilderTest.class);
      suite.addTestSuite(LabelBuilderTest.class);
      return suite;
   }

}
