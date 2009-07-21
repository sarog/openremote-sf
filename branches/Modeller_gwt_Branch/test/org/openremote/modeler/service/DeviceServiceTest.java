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
package org.openremote.modeler.service;

import junit.framework.Assert;
import org.hibernate.ObjectNotFoundException;
import org.openremote.modeler.SpringContext;
import org.openremote.modeler.TestNGBase;
import org.openremote.modeler.client.rpc.DeviceRPCService;
import org.openremote.modeler.client.rpc.UserRPCService;
import org.openremote.modeler.domain.Account;
import org.openremote.modeler.domain.Device;
import org.openremote.modeler.domain.User;
import org.testng.annotations.Test;

/**
 * The Class DeviceServiceTest.
 * 
 * @author Tomsky,Dan 2009-7-10
 */
public class DeviceServiceTest extends TestNGBase{
   
   /** The device service. */
   private DeviceRPCService deviceRPCService =
      (DeviceRPCService) SpringContext.getInstance().getBean("deviceRPCService");
   private UserRPCService userRPCService =
      (UserRPCService) SpringContext.getInstance().getBean("userRPCService");

    /**
    * Test save device.
    */
   @Test
   public void save(){
      Device device = new Device();
      device.setName("tv");
      device.setModel("tv");
      device.setVendor("sony");
      deviceRPCService.saveDevice(device);
      Device deviceInDB = deviceRPCService.loadById(device.getOid());
      Assert.assertEquals(deviceInDB.getName(), device.getName());
      
      device.setName("xxx");
      device.setModel("MP8640");
      device.setVendor("3m");
      deviceRPCService.saveDevice(device);
      deviceInDB = deviceRPCService.loadById(device.getOid());
      Assert.assertEquals(deviceInDB.getName(), device.getName());
   }
   
   @Test(dependsOnMethods="save",expectedExceptions={ObjectNotFoundException.class})
   public void delete(){
      Device device = new Device();
      device.setName("xxx");
      device.setModel("MP8640");
      device.setVendor("3m");
      deviceRPCService.saveDevice(device);
      deviceRPCService.deleteDevice(device.getOid());
      Device deviceInDB = deviceRPCService.loadById(device.getOid());
      deviceInDB.getName();//throws ObjectNotFoundException
   }
   
   @Test(dependsOnMethods="save")
   public void loadAll(){
      
      User user = new User();
      Account account = new Account();
      user.setAccount(account);
      Device device = new Device();
      device.setName("tv");
      device.setModel("tv");
      device.setVendor("sony");
      account.addDevice(device);
      account.addDevice(device);
      account.addDevice(device);
      account.addDevice(device);
      account.addDevice(device);
      account.addDevice(device);
      userRPCService.saveUser(user);
      Assert.assertEquals(6, deviceRPCService.loadAll(account).size());
      
   }
   
}
