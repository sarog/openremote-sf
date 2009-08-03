/*
 * OpenRemote, the Home of the Digital Home. Copyright 2008-2009, OpenRemote Inc.
 * 
 * See the contributors.txt file in the distribution for a full listing of individual contributors.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
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
 * For store BeanModel in Frontend.<br/>
 * Every operation to the table will notify the listener on it.<br/>
 * 
 * <br/>
 * 
 * The notification only support add,update,remove. <br/>
 * Use {@link ChangeListener} to listen the state of this table.
 * Use changeEvent.getItem() to get the beanmodel which had changed.
 * 
 * @author allen.wei
 */
public class BeanModelTable {

   /** The map store BeanModel. */
   private Map<Long, BeanModel> map = new HashMap<Long, BeanModel>();
   
   // the same as ChangeEventSource
   /** The Constant ADD. */
   public static final int ADD = ChangeEventSource.Add;

   /** The Constant UPDATE. */
   public static final int UPDATE = ChangeEventSource.Update;

   /** The Constant REMOVE. */
   public static final int REMOVE = ChangeEventSource.Remove;

   /** The change event support. */
   private ChangeEventSupport changeEventSupport = null;

   /**
    * Instantiates a new bean model table.
    */
   public BeanModelTable() {
      changeEventSupport = new ChangeEventSupport();
   }

   /**
    * Adds the change listener.When a BeanModel of this table add,update,delete. 
    * It will create {@link ChangeEvent} send to all the listeners. <br/>
    * User can use changeEvent.getType() to judge the event type.
    * Event type can be {@link #ADD},{@link #UPDATE},{@link #REMOVE}
    * 
    * @param listener
    *           the {@link ChangeListener}
    */
   public void addChangeListener(ChangeListener... listener) {
      changeEventSupport.addChangeListener(listener);
   }

   /**
    * Notify all the {@link ChangeListener}.
    * 
    * @param evt
    *           the {@link ChangeEvent}
    */
   public void notify(ChangeEvent evt) {
      changeEventSupport.notify(evt);
   }

   /**
    * Removes the {@link ChangeListener}.
    * 
    * @param listener
    *           the listener
    */
   public void removeChangeListener(ChangeListener... listener) {
      changeEventSupport.removeChangeListener(listener);
   }

   /**
    * Insert a BeanModel into table. <br/>
    * 
    * If beanModel equal null, it will throw {@link IllegalArgumentException}.<br/>
    * If beanModel's bean isn't inherit from {@link BusinessEntity}, it will throw {@link IllegalArgumentException}.<br/>
    * 
    * @param beanModel
    *           the bean model
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
    * Insert all BeanModel. It will call {@link #insert(BeanModel)} recursively
    * 
    * @param beanModels
    *           the bean models
    */
   public void insertAll(Collection<BeanModel> beanModels) {
      for (BeanModel beanModel : beanModels) {
         insert(beanModel);
      }
   }

   /**
    * Gets the BeanModel by id.
    * 
    * @param oid
    *           the id of model
    * 
    * @return the bean model
    */
   public BeanModel get(long oid) {
      return (BeanModel) map.get(oid);
   }

   /**
    * Delete a BeanModel from table. <br/>
    * 
    * It will send {@link #REMOVE} {@link ChangeEvent} to change listeners.
    * 
    * @param id
    *           the id
    */
   public void delete(long id) {
      BeanModel beanModel = get(id);
      notifyBeanModel(REMOVE, beanModel);
      map.remove(id);

   }

   /**
    * Delete BeanModel from table according BeanModel. <br/>
    * Actually it call {@link #delete(long)} method.
    * 
    * @param beanModel
    *           the bean model
    */
   public void delete(BeanModel beanModel) {
      delete(getIdFromBeanModel(beanModel));
   }

   /**
    * Notify bean model.
    * 
    * @param type
    *           the type
    * @param beanModel
    *           the bean model
    */
   public void notifyBeanModel(int type, BeanModel beanModel) {
      ChangeEvent changeEvent = new ChangeEvent(type, null, beanModel);
      notify(changeEvent);
   }

   /**
    * Update BeanModel. It use id to judge whether two object equals.
    * 
    * It will send {@link #UPDATE} {@link ChangeEvent} to change listeners.
    * 
    * @param newBeanModel
    *           the new bean model
    */
   public void update(BeanModel newBeanModel) {
      notifyBeanModel(UPDATE, newBeanModel);
      map.put(getIdFromBeanModel(newBeanModel), newBeanModel);
   }

   /**
    * Gets the id from bean model.
    * 
    * @param beanModel
    *           the bean model
    * 
    * @return the id from bean model
    */
   private long getIdFromBeanModel(BeanModel beanModel) {
      BusinessEntity businessEntity = beanModel.getBean();
      return businessEntity.getOid();
   }
}
