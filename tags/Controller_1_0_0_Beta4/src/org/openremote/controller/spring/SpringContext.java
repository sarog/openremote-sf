/* OpenRemote, the Home of the Digital Home.
 * Copyright 2008, OpenRemote Inc.
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
package org.openremote.controller.spring;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.interceptor.TransactionProxyFactoryBean;

/**
 * ApplicationContext for Spring container
 * 
 * @author Dan 2009-2-16
 */
public class SpringContext {

   private static SpringContext m_instance;

   private static String[] contextFiles = new String[] { "spring-context.xml" };

   private ApplicationContext ctx;

   public SpringContext() {
      ctx = new ClassPathXmlApplicationContext(contextFiles);
   }

   public SpringContext(String[] setting) {
      ctx = new ClassPathXmlApplicationContext(setting);
   }

   /**
    * Gets a instance of <code>SpringContext</code>
    * 
    * @return the instance of <code>SpringContext</code>
    */
   public synchronized static SpringContext getInstance() {
      if (m_instance == null) {
         m_instance = new SpringContext(contextFiles);
      }
      return m_instance;
   }

   /**
    * Gets a bean instance with the given bean identifier
    * 
    * @param beanId
    *           the given bean identifier
    * @return a bean instance
    */
   public Object getBean(String beanId) {
      Object o = ctx.getBean(beanId);
      if (o instanceof TransactionProxyFactoryBean) {
         TransactionProxyFactoryBean factoryBean = (TransactionProxyFactoryBean) o;
         o = factoryBean.getObject();
      }
      return o;
   }

}
