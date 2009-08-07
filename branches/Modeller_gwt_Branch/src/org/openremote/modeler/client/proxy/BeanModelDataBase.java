/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2009, OpenRemote Inc.
*
* See the contributors.txt file in the distribution for a
* full listing of individual contributors.
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as
* published by the Free Software Foundation, either version 3 of the
* License, or (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Affero General Public License for more details.
*
* You should have received a copy of the GNU Affero General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*/
package org.openremote.modeler.client.proxy;

import java.util.ArrayList;
import java.util.List;

import org.openremote.modeler.client.utils.BeanModelTable;
import org.openremote.modeler.domain.BusinessEntity;
import org.openremote.modeler.domain.DeviceCommandRef;
import org.openremote.modeler.domain.DeviceMacroItem;
import org.openremote.modeler.domain.DeviceMacroRef;

import com.extjs.gxt.ui.client.data.BeanModel;

/**
 * Stores all the UI models here like a database. <br/>
 * 
 * 1.All the operations on the model must use {@link BeanModelTable}. So all the related UI Element can update themselves.<br/>
 * 2.If the UI Element which want to observe the model can add change listener on every {@link BeanModelTable}.
 */
public class BeanModelDataBase {

   /**
    * Not be instantiated.
    */
   private BeanModelDataBase() {
   }
   
   /** Stores all the device models. */
   public static final BeanModelTable deviceTable = new BeanModelTable();

   /** Stores all the DeviceCommand models. */
   public static final BeanModelTable deviceCommandTable = new BeanModelTable();

   /** Store all the DeviceMacro models. */
   public static final BeanModelTable deviceMacroTable = new BeanModelTable();

   /** Store all the DeviceItem models. */
   public static final BeanModelTable deviceMacroItemMap = new BeanModelTable();

   /**
    * Gets the original device macro item bean model,if not find return null.
    * 
    * @param deviceMacroItemBeanModel the device macro item bean model
    * 
    * @return the original device macro item bean model,If not find return null.
    */
   public static BeanModel getOriginalDeviceMacroItemBeanModel(BeanModel deviceMacroItemBeanModel) {
      if (deviceMacroItemBeanModel.getBean() instanceof DeviceMacroItem) {
         DeviceMacroItem deviceMacroItem = (DeviceMacroItem) deviceMacroItemBeanModel.getBean();
         if (deviceMacroItem instanceof DeviceMacroRef) {
            DeviceMacroRef deviceMacroRef = (DeviceMacroRef) deviceMacroItem;
            return deviceMacroTable.get(deviceMacroRef.getTargetDeviceMacro().getOid());
         }
         if (deviceMacroItem instanceof DeviceCommandRef) {
            DeviceCommandRef deviceCommandRef = (DeviceCommandRef) deviceMacroItem;
            return deviceCommandTable.get(deviceCommandRef.getDeviceCommand().getOid());
         }
      }
      return null;
   }

   /**
    * Gets the bean model id,if not find return 0.
    * 
    * @param beanModel the bean model
    * 
    * @return the bean model id,if not find return 0.
    */
   public static long getBeanModelId(BeanModel beanModel) {
      if (beanModel == null) {
         return 0;
      }
      if (beanModel.getBean() instanceof BusinessEntity) {
         BusinessEntity entity = (BusinessEntity) beanModel.getBean();
         return entity.getOid();
      }
      return 0;
   }

   /**
    * Gets the bean models according to domains.
    * 
    * @param businessEntities domain which superclass is {@link BusinessEntity}
    * @param beanModelTable table which stores this model.
    * 
    * @return the all the matched bean models
    */
   public static List<BeanModel> getBeanModelsByBeans(List<? extends BusinessEntity> businessEntities,
         BeanModelTable beanModelTable) {
      List<BeanModel> list = new ArrayList<BeanModel>();
      for (BusinessEntity businessEntity : businessEntities) {
         if (beanModelTable.get(businessEntity.getOid()) != null) {
            list.add(beanModelTable.get(businessEntity.getOid()));
         }
      }
      return list;
   }

}
