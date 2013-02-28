package org.openremote.beehive;

import junit.framework.TestCase;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.openremote.beehive.spring.SpringContext;


public abstract class TestBase extends TestCase {
   private SessionFactory sessionFactory;

   protected void setUp() throws Exception {
      super.setUp();
      sessionFactory = (SessionFactory) SpringContext.getInstance().getBean("sessionFactory");

      Session s = sessionFactory.openSession();
      TransactionSynchronizationManager.bindResource(sessionFactory, new SessionHolder(s));

   }

   protected void tearDown() throws Exception {
      super.tearDown();
      SessionHolder holder = (SessionHolder) TransactionSynchronizationManager.getResource(sessionFactory);
      Session s = holder.getSession();
      try {
         s.flush();
      } catch (Throwable e) {
         e.printStackTrace();
      }

      TransactionSynchronizationManager.unbindResource(sessionFactory);
      SessionFactoryUtils.closeSession(s);
   }

}
