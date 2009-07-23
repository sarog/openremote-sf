/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2009, OpenRemote Inc.
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

package org.openremote.modeler.client.utils;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.ChangeEvent;
import com.extjs.gxt.ui.client.data.ChangeEventSource;

import java.util.HashMap;
import java.util.Collection;

import org.openremote.modeler.domain.BusinessEntity;

/**
 * @author allen.wei
 */
public class BeanModelHashMap extends HashMap<Long,BeanModel> {

   // the same as ChangeEventSource
   private static final int ADD = 10;
   private static final int UPDATE = 40;
   private static final int DELETE = 30;
   public void put(BeanModel beanModel) {
      if (beanModel.getBean() == null) {
         throw new IllegalArgumentException("Gets Bean from BeanModel is null");

      }
      if (!(beanModel.getBean() instanceof BusinessEntity)) {
         throw new IllegalArgumentException("Original model must extends from BusinessEntity");

      }
      if (beanModel.getBean() instanceof BusinessEntity) {
         super.put(getIdFromBeanModel(beanModel), beanModel);
      }
   }

   public void putAll(Collection<BeanModel> beanModels) {
      for (BeanModel beanModel :beanModels) {
         put(beanModel);
      }
   }

   public BeanModel getByOid(long oid) {
      return (BeanModel) super.get(oid);
   }

   public void remove(long id) {
      BeanModel beanModel = getByOid(id);
      super.remove(id);
      notifyBeanModel(DELETE,beanModel);
   }

   public void remove(BeanModel beanModel) {
      super.remove(getIdFromBeanModel(beanModel));
      notifyBeanModel(DELETE,beanModel);
   }


   public void notifyBeanModel(int type,BeanModel beanModel) {
      ChangeEvent changeEvent = new ChangeEvent(type,null,beanModel);
      beanModel.notify(changeEvent);
   }


   private long getIdFromBeanModel(BeanModel beanModel) {
      BusinessEntity businessEntity = beanModel.getBean();
      return businessEntity.getOid();
   }
}
