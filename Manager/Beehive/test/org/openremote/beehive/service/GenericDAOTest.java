package org.openremote.beehive.service;

import org.openremote.beehive.TestBase;
import org.openremote.beehive.domain.Model;
import org.openremote.beehive.serviceHibernateImpl.GenericDAO;
import org.openremote.beehive.spring.SpringContext;

public class GenericDAOTest extends TestBase {
   private GenericDAO genericDAO = (GenericDAO) SpringContext
   .getInstance().getBean("genericDAO");
   
   public void testGetByNonIdField(){      
      Model model = genericDAO.getByNonIdField(Model.class, "fileName", "TXCD-1240");
      assertNotNull(model);
      
   }
}
