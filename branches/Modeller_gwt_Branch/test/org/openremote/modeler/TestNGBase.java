/* OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2009, OpenRemote Inc.
 * 
 * See the contributors.txt file in the distribution for a
 * full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3.0 of
 * the License, or (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.openremote.modeler;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

/**
 * The TestNG Base Class. This Base class helps implement the Open Session In View function.
 * 
 * @author Dan 2009-7-10
 */
public class TestNGBase {
   
   private SessionFactory sessionFactory;
   /**
    * SetUp.
    */
   @BeforeClass
   public void setUp(){
      sessionFactory = (SessionFactory) SpringContext.getInstance().getBean("sessionFactory");
      Session s = sessionFactory.openSession();
      TransactionSynchronizationManager.bindResource(sessionFactory, new SessionHolder(s));
   }
   
   /**
    * Tear down.
    */
   @AfterClass
   public void tearDown(){
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
