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

package org.openremote.modeler.client.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.openremote.modeler.domain.BusinessEntity;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.ChangeEvent;
import com.extjs.gxt.ui.client.data.ChangeEventSource;
import com.extjs.gxt.ui.client.data.ChangeEventSupport;
import com.extjs.gxt.ui.client.data.ChangeListener;

/**
 * For store BeanModel in Frontend.
 *
 * @author allen.wei
 */
public class BeanModelTable {

   /**
    * The map.
    */
   private Map<Long, BeanModel> map = new HashMap<Long, BeanModel>();
   // the same as ChangeEventSource
   /**
    * The Constant ADD.
    */
   private static final int ADD = ChangeEventSource.Add;

   /**
    * The Constant UPDATE.
    */
   private static final int UPDATE = ChangeEventSource.Update;

   /**
    * The Constant REMOVE.
    */
   private static final int REMOVE = ChangeEventSource.Remove;


   private ChangeEventSupport changeEventSupport = null;

   public BeanModelTable() {
      changeEventSupport = new ChangeEventSupport();
   }

   public void addChangeListener(ChangeListener... listener) {
      changeEventSupport.addChangeListener(listener);
   }

   public void notify(ChangeEvent evt) {
      changeEventSupport.notify(evt);
   }

   public void removeChangeListener(ChangeListener... listener) {
      changeEventSupport.removeChangeListener(listener);
   }

   /**
    * Insert.
    *
    * @param beanModel the bean model
    */
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

   /**
    * Insert all.
    *
    * @param beanModels the bean models
    */
   public void insertAll(Collection<BeanModel> beanModels) {
      for (BeanModel beanModel : beanModels) {
         insert(beanModel);
      }
   }

   /**
    * Gets the.
    *
    * @param oid the oid
    * @return the bean model
    */
   public BeanModel get(long oid) {
      return (BeanModel) map.get(oid);
   }

   /**
    * Delete.
    *
    * @param id the id
    */
   public void delete(long id) {
      BeanModel beanModel = get(id);
      notifyBeanModel(REMOVE, beanModel);
      map.remove(id);
      
   }

   /**
    * Delete.
    *
    * @param beanModel the bean model
    */
   public void delete(BeanModel beanModel) {
      delete(getIdFromBeanModel(beanModel));
   }

   /**
    * Notify bean model.
    * 
    * @param type      the type
    * @param beanModel the bean model
    */
   public void notifyBeanModel(int type, BeanModel beanModel) {
      ChangeEvent changeEvent = new ChangeEvent(type, null, beanModel);
      notify(changeEvent);
   }

   /**
    * Update.
    *
    * @param newBeanModel the new bean model
    */
   public void update(BeanModel newBeanModel) {
      notifyBeanModel(UPDATE, newBeanModel);
      map.put(getIdFromBeanModel(newBeanModel), newBeanModel);
   }

   /**
    * Gets the id from bean model.
    *
    * @param beanModel the bean model
    * @return the id from bean model
    */
   private long getIdFromBeanModel(BeanModel beanModel) {
      BusinessEntity businessEntity = beanModel.getBean();
      return businessEntity.getOid();
   }
}
