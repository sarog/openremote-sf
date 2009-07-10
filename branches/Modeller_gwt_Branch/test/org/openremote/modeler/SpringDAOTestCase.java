package org.openremote.modeler;

import org.springframework.test.AbstractTransactionalDataSourceSpringContextTests;

public abstract class SpringDAOTestCase extends AbstractTransactionalDataSourceSpringContextTests {
   
   protected String[] getConfigLocations() {
      return new String[] {"spring-context.xml","annomvc-servlet.xml"};
   }
   
}