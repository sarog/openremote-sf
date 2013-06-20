/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2013, OpenRemote Inc.
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
package org.openremote.modeler.shared.dto;

import org.openremote.modeler.client.utils.IDUtil;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author <a href = "mailto:eric@openremote.org">Eric Bariaux</a>
 */
public class DeviceCommandDTOTest {

  @Test
  public void testDeviceCommandDTOsEqual() {
    DeviceCommandDTO deviceCommand1 = new DeviceCommandDTO();
    DeviceCommandDTO deviceCommand2 = new DeviceCommandDTO();
    
    Assert.assertEquals(deviceCommand1, deviceCommand2, "Expected the DeviceCommandDTO to be equal");
    
    deviceCommand1.setOid(IDUtil.nextID());
    deviceCommand2.setOid(deviceCommand1.getOid());

    Assert.assertEquals(deviceCommand1, deviceCommand2, "Expected the DeviceCommandDTO to be equal");

    deviceCommand1.setDisplayName("Name");
    deviceCommand2.setDisplayName("Name");

    Assert.assertEquals(deviceCommand1, deviceCommand2, "Expected the DeviceCommandDTO to be equal");

    deviceCommand1.setFullyQualifiedName("FQN");
    deviceCommand2.setFullyQualifiedName("FQN");
    
    Assert.assertEquals(deviceCommand1, deviceCommand2, "Expected the DeviceCommandDTO to be equal");

    deviceCommand1.setProtocolType("Protocol type");
    deviceCommand2.setProtocolType("Protocol type");
    
    Assert.assertEquals(deviceCommand1, deviceCommand2, "Expected the DeviceCommandDTO to be equal");
  }

  @Test
  public void testDeviceCommandDTOsNotEqual() {
    DeviceCommandDTO deviceCommand1 = new DeviceCommandDTO();
    deviceCommand1.setOid(IDUtil.nextID());
    deviceCommand1.setDisplayName("Name");
    deviceCommand1.setFullyQualifiedName("FQN");
    deviceCommand1.setProtocolType("Protocol type");

    DeviceCommandDTO deviceCommand2 = new DeviceCommandDTO();
    deviceCommand2.setOid(deviceCommand1.getOid());
    deviceCommand2.setDisplayName("Name");
    deviceCommand2.setFullyQualifiedName("FQN");
    deviceCommand2.setProtocolType("Protocol type");

    Assert.assertEquals(deviceCommand1, deviceCommand2, "Expected the DeviceCommandDTO to be equal");
    
    deviceCommand2.setOid(null);
    Assert.assertFalse(deviceCommand1.equals(deviceCommand2), "Expected the DeviceCommandDTO to be different, second id is not set");

    deviceCommand2.setOid(IDUtil.nextID());
    Assert.assertFalse(deviceCommand1.equals(deviceCommand2), "Expected the DeviceCommandDTO to be different, id is different");

    deviceCommand2.setOid(deviceCommand1.getOid());
    Assert.assertEquals(deviceCommand1, deviceCommand2, "Expected the DeviceCommandDTO to be equal");
    
    deviceCommand2.setDisplayName(null);
    Assert.assertFalse(deviceCommand1.equals(deviceCommand2), "Expected the DeviceCommandDTO to be different, second displayName is not set");
    
    deviceCommand2.setDisplayName("Name 2");
    Assert.assertFalse(deviceCommand1.equals(deviceCommand2), "Expected the DeviceCommandDTO to be different, displayName is different");

    deviceCommand2.setDisplayName("Name");
    Assert.assertEquals(deviceCommand1, deviceCommand2, "Expected the DeviceCommandDTO to be equal");
    
    deviceCommand2.setFullyQualifiedName(null);
    Assert.assertFalse(deviceCommand1.equals(deviceCommand2), "Expected the DeviceCommandDTO to be different, second fullyQualifiedName is not set");
    
    deviceCommand2.setFullyQualifiedName("FQN2");
    Assert.assertFalse(deviceCommand1.equals(deviceCommand2), "Expected the DeviceCommandDTO to be different, fullyQualifiedName is different");

    deviceCommand2.setFullyQualifiedName("FQN");
    Assert.assertEquals(deviceCommand1, deviceCommand2, "Expected the DeviceCommandDTO to be equal");

    deviceCommand2.setProtocolType(null);
    Assert.assertFalse(deviceCommand1.equals(deviceCommand2), "Expected the DeviceCommandDTO to be different, second protocolType is not set");
    
    deviceCommand2.setProtocolType("Protocol type 2");
    Assert.assertFalse(deviceCommand1.equals(deviceCommand2), "Expected the DeviceCommandDTO to be different, protocolType is different");
  }
  
  @Test
  public void testConstructor() {
    DeviceCommandDTO deviceCommand1 = new DeviceCommandDTO();
    deviceCommand1.setOid(IDUtil.nextID());
    deviceCommand1.setDisplayName("Name");
    deviceCommand1.setFullyQualifiedName("FQN");
    deviceCommand1.setProtocolType("Protocol type");

    DeviceCommandDTO deviceCommand2 = new DeviceCommandDTO(deviceCommand1.getOid(), "Name", "FQN", "Protocol type");

    Assert.assertEquals(deviceCommand1, deviceCommand2, "Expected the DeviceCommandDTO to be equal");
  }
  
}
