/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2012, OpenRemote Inc.
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
package org.openremote.modeler.service.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.openremote.modeler.domain.Account;
import org.openremote.modeler.domain.CommandDelay;
import org.openremote.modeler.domain.Device;
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.domain.DeviceCommandRef;
import org.openremote.modeler.domain.DeviceMacro;
import org.openremote.modeler.domain.DeviceMacroItem;
import org.openremote.modeler.domain.DeviceMacroRef;
import org.openremote.modeler.service.BaseAbstractService;
import org.openremote.modeler.service.DeviceCommandService;
import org.openremote.modeler.service.DeviceMacroItemService;
import org.openremote.modeler.service.DeviceMacroService;
import org.openremote.modeler.service.UserService;
import org.openremote.modeler.shared.dto.DTOReference;
import org.openremote.modeler.shared.dto.MacroDTO;
import org.openremote.modeler.shared.dto.MacroDetailsDTO;
import org.openremote.modeler.shared.dto.MacroItemDetailsDTO;
import org.openremote.modeler.shared.dto.MacroItemType;
import org.springframework.transaction.annotation.Transactional;


/**
 * Default implements of {@link DeviceMacroService}.
 */
public class DeviceMacroServiceImpl extends BaseAbstractService<DeviceMacro> implements DeviceMacroService {

   /** The device macro item service. */
   private DeviceMacroItemService deviceMacroItemService;
   
   private DeviceCommandService deviceCommandService;
   
   /** The user service. */
   private UserService userService;


   /**
    * For spring IOC.
    * 
    * @param deviceMacroItemService the new device macro item service
    */
   public void setDeviceMacroItemService(DeviceMacroItemService deviceMacroItemService) {
      this.deviceMacroItemService = deviceMacroItemService;
   }
   
  public void setDeviceCommandService(DeviceCommandService deviceCommandService) {
    this.deviceCommandService = deviceCommandService;
  }

  /**
    * For spring IOC.
    * 
    * @param userService the new user service
    */
   public void setUserService(UserService userService) {
      this.userService = userService;
   }

   /**
    * {@inheritDoc}
    * @see org.openremote.modeler.service.BaseAbstractService#loadAll()
    */
   public List<DeviceMacroItem> loadByDeviceMacro(long id) {
      return loadById(id).getDeviceMacroItems();
   }

   /**
    * {@inheritDoc}
    * @see org.openremote.modeler.service.BaseAbstractService#loadAll()
    * @see org.openremote.modeler.service.DeviceMacroService#loadAll(org.openremote.modeler.domain.Account)
    */
   public List<DeviceMacro> loadAll(Account account) {
     DetachedCriteria criteria = DetachedCriteria.forClass(DeviceMacro.class);
     criteria.add(Restrictions.eq("account", account));
     @SuppressWarnings("unchecked")
      List<DeviceMacro> list = genericDAO.getHibernateTemplate().findByCriteria(criteria, 0, 1);
      for (DeviceMacro deviceMacro : list) {
         Hibernate.initialize(deviceMacro.getDeviceMacroItems());
      }
      return list;
   }

   /**
    * {@inheritDoc}
    * @see org.openremote.modeler.service.DeviceMacroService#saveDeviceMacro(org.openremote.modeler.domain.DeviceMacro)
    */
   @Transactional
   public DeviceMacro saveDeviceMacro(DeviceMacro deviceMacro) {
      deviceMacro.setAccount(userService.getAccount());
      genericDAO.save(deviceMacro);
      return deviceMacro;
   }

   /**
    * {@inheritDoc}
    * @see org.openremote.modeler.service.DeviceMacroService#updateDeviceMacro(org.openremote.modeler.domain.DeviceMacro, List<org.openremote.modeler.domain.DeviceMacroItem> items)
    */
   @Transactional
   public DeviceMacro updateDeviceMacro(DeviceMacro deviceMacro, List<DeviceMacroItem> items) {
      DeviceMacro old = genericDAO.loadById(DeviceMacro.class, deviceMacro.getOid());
      if (old.getAccount() == null) {
         old.setAccount(userService.getAccount());
      }
      genericDAO.deleteAll(old.getDeviceMacroItems());
      old.getDeviceMacroItems().clear();

      old.setName(deviceMacro.getName());
      for (DeviceMacroItem deviceMacroItem : items) {
         deviceMacroItem.setOid(0);
         deviceMacroItem.setParentDeviceMacro(old);
      }
      old.getDeviceMacroItems().addAll(items);
      return old;
   }

   /**
    * {@inheritDoc}
    * @see org.openremote.modeler.service.DeviceMacroService#deleteDeviceMacro(long)
    */
   @Transactional
   public void deleteDeviceMacro(long id) {
      DeviceMacro deviceMacro = loadById(id);
      deviceMacroItemService.deleteByDeviceMacro(deviceMacro);
      genericDAO.delete(deviceMacro);
   }


   public List<DeviceMacro> loadSameMacro(DeviceMacro macro) {
      List<DeviceMacro> results = null;
      DetachedCriteria critera = DetachedCriteria.forClass(DeviceMacro.class);
      critera.add(Restrictions.eq("account.oid", macro.getAccount().getOid()));
      critera.add(Restrictions.eq("name", macro.getName()));
      results = genericDAO.findByDetachedCriteria(critera);
      if (results != null && results.size() >0) {
         for (Iterator<DeviceMacro> iterator = results.iterator();iterator.hasNext(); ) {
            DeviceMacro m = iterator.next();
            if (! m.equalsWitoutCompareOid(macro)) {
               iterator.remove();
            }
         }
      }
      return results;
   }
   
   /**
    * {@inheritDoc}
    */
   @Override
   public ArrayList<MacroDetailsDTO> loadAllMacroDetailsDTOs(Account account) {
     ArrayList<MacroDetailsDTO> dtos = new ArrayList<MacroDetailsDTO>();
     for (DeviceMacro dm : loadAll(account)) {
       dtos.add(createMacroDetailsDTO(dm));
     }
     return dtos;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public MacroDetailsDTO loadMacroDetails(long id) {    
     return createMacroDetailsDTO(loadById(id));
   }

   private MacroDetailsDTO createMacroDetailsDTO(DeviceMacro macroBean) {
     ArrayList<MacroItemDetailsDTO> items = new ArrayList<MacroItemDetailsDTO>();
      for (DeviceMacroItem dmi : macroBean.getDeviceMacroItems()) {
        if (dmi instanceof DeviceMacroRef) {
          DeviceMacroRef macroRef = ((DeviceMacroRef)dmi);
          items.add(new MacroItemDetailsDTO(macroRef.getOid(), MacroItemType.Macro, macroRef.getTargetDeviceMacro().getDisplayName(), new DTOReference(macroRef.getTargetDeviceMacro().getOid())));
        } else if (dmi instanceof DeviceCommandRef) {
          DeviceCommandRef commandRef = ((DeviceCommandRef)dmi);
          items.add(new MacroItemDetailsDTO(commandRef.getOid(), MacroItemType.Command, commandRef.getDeviceCommand().getDisplayName(), new DTOReference(commandRef.getDeviceCommand().getOid())));
        } else if (dmi instanceof CommandDelay) {
          items.add(new MacroItemDetailsDTO(dmi.getOid(), Integer.parseInt(((CommandDelay)dmi).getDelaySecond())));
        }
      }
      return new MacroDetailsDTO(macroBean.getOid(), macroBean.getName(), items);
   }
    
   @Override
   public MacroDTO saveNewMacro(MacroDetailsDTO macro) {
     DeviceMacro macroBean = new DeviceMacro();
     macroBean.setName(macro.getName());
     macroBean.setAccount(userService.getAccount());
     
     List<DeviceMacroItem> macroItemBeans = createDeviceMacroItems(macro, macroBean);
     
     macroBean.setDeviceMacroItems(macroItemBeans);
     return saveDeviceMacro(macroBean).getMacroDTO();
   }
   
   @Override
   public MacroDTO updateMacroWithDTO(MacroDetailsDTO macro) {
     DeviceMacro macroBean = loadById(macro.getOid());
     macroBean.setName(macro.getName());

     List<DeviceMacroItem> macroItemBeans = createDeviceMacroItems(macro, macroBean);     
     return updateDeviceMacro(macroBean, macroItemBeans).getMacroDTO();
   }

   private List<DeviceMacroItem> createDeviceMacroItems(MacroDetailsDTO macro, DeviceMacro macroBean) {
     List<DeviceMacroItem> macroItemBeans = new ArrayList<DeviceMacroItem>();
      for (MacroItemDetailsDTO item : macro.getItems()) {
        DeviceMacroItem itemBean = null;
        switch(item.getType()) {
          case Command:
            DeviceCommand dc = deviceCommandService.loadById(item.getDto().getId());
            itemBean = new DeviceCommandRef(dc);
            break;
          case Macro:
            DeviceMacro dm = loadById(item.getDto().getId());
            itemBean = new DeviceMacroRef(dm);
            break;
          case Delay:
            itemBean = new CommandDelay(Integer.toString(item.getDelay()));
            break;
        }
        if (itemBean != null) {
          macroItemBeans.add(itemBean);
          itemBean.setParentDeviceMacro(macroBean);
        }
      }
     return macroItemBeans;
   }
   
}
