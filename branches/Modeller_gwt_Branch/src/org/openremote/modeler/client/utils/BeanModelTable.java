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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.openremote.modeler.domain.BusinessEntity;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.ChangeEvent;
import com.extjs.gxt.ui.client.data.ChangeEventSource;

/**
 * @author allen.wei
 */
public class BeanModelTable {
   
   private Map<Long,BeanModel> map = new HashMap<Long,BeanModel>();
   // the same as ChangeEventSource
   private static final int ADD = ChangeEventSource.Add;
   private static final int UPDATE = ChangeEventSource.Update;
   private static final int REMOVE = ChangeEventSource.Remove;
   public void insert(BeanModel beanModel) {
      if (beanModel.getBean() == null) {
         throw new IllegalArgumentException("Gets Bean from BeanModel is null");

      }
      if (!(beanModel.getBean() instanceof BusinessEntity)) {
         throw new IllegalArgumentException("Original model must extends from BusinessEntity");

      }
      if (beanModel.getBean() instanceof BusinessEntity) {
         map.put(getIdFromBeanModel(beanModel), beanModel);
      }
   }

   public void insertAll(Collection<BeanModel> beanModels) {
      for (BeanModel beanModel :beanModels) {
         insert(beanModel);
      }
   }

   public BeanModel get(long oid) {
      return (BeanModel) map.get(oid);
   }

   public void delete(long id) {
      BeanModel beanModel = get(id);
      map.remove(id);
      notifyBeanModel(REMOVE,beanModel);
   }

   public void delete(BeanModel beanModel) {
      delete(getIdFromBeanModel(beanModel));
   }

   public void notifyBeanModel(int type,BeanModel beanModel) {
      ChangeEvent changeEvent = new ChangeEvent(type,null,beanModel);
      beanModel.notify(changeEvent);
   }

   public void update(BeanModel beanModel){
      map.put(getIdFromBeanModel(beanModel), beanModel);
      notifyBeanModel(UPDATE,beanModel);
   }
   private long getIdFromBeanModel(BeanModel beanModel) {
      BusinessEntity businessEntity = beanModel.getBean();
      return businessEntity.getOid();
   }
}
