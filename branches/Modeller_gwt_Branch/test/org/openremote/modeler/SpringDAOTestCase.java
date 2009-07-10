package org.openremote.modeler;

import org.springframework.test.AbstractTransactionalDataSourceSpringContextTests;

public abstract class SpringDAOTestCase extends AbstractTransactionalDataSourceSpringContextTests {
   
   protected String[] getConfigLocations() {
      return new String[] {"applicationContext.xml","spring-service-hibernate-impl.xml","datasource-test.xml"};
   }
   
}