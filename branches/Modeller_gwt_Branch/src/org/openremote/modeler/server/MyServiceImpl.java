package org.openremote.modeler.server;

import java.util.List;

import org.hibernate.SessionFactory;
import org.openremote.modeler.client.rpc.MyService;
import org.openremote.modeler.domain.Activity;
import org.openremote.modeler.service.MyServiceManager;

public class MyServiceImpl extends BaseGWTSpringController implements MyService {


   /**
    * 
    */
   private static final long serialVersionUID = 8210992567722400148L;
   private SessionFactory sessionFactory;

  
   public void setSessionFactory(SessionFactory sessionFactory) {
      this.sessionFactory = sessionFactory;
   }
   
   private MyServiceManager myServiceManager;


   public void setMyServiceManager(MyServiceManager myServiceManager) {
      this.myServiceManager = myServiceManager;
   }


   public MyServiceImpl() {
   }


   public List<Activity> getString() {
      return myServiceManager.getString();
   }
   
   public void addScreen() {
      myServiceManager.addScreen();
   }
   
}
