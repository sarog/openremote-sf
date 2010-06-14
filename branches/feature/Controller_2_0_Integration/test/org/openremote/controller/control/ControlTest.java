package org.openremote.controller.control;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openremote.controller.control.gesture.GestureBuilderTest;
import org.openremote.controller.control.slider.SliderBuilderTest;
import org.openremote.controller.label.LabelBuilderTest;

public class ControlTest {

   public static Test suite() {

      TestSuite suite = new TestSuite("<------------Test For Control---------------->");
      suite.addTestSuite(GestureBuilderTest.class);
      suite.addTestSuite(SliderBuilderTest.class);
      suite.addTestSuite(LabelBuilderTest.class);
      return suite;
   }

}
