package org.openremote.beehive.service;

import java.util.Date;

import org.openremote.beehive.TestBase;
import org.openremote.beehive.domain.Model;
import org.openremote.beehive.domain.SyncHistory;
import org.openremote.beehive.serviceHibernateImpl.GenericDAO;
import org.openremote.beehive.spring.SpringContext;

public class GenericDAOTest extends TestBase {
   private GenericDAO genericDAO = (GenericDAO) SpringContext
   .getInstance().getBean("genericDAO");
   
   public void _testGetByNonIdField(){      
      Model model = genericDAO.getByNonIdField(Model.class, "fileName", "MP8640");
      assertNotNull(model);
      
   }
   public void testGetByMaxId(){
      SyncHistory model = genericDAO.getByMaxId(SyncHistory.class);
      assertNotNull(model);
      Date date  = model.getEndTime();
      System.out.println(date.toString());
      
   }
}
