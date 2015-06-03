/*
 * OpenRemote, the Home of the Digital Home.
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
package org.openremote.controller;

import org.openremote.controller.service.ServiceContext;

/**
 * File for ICT Protege configuration.
 *
 * @author Tomas Morton
 */
public class ICTProtegeConfiguration extends Configuration
{

  // Constants ------------------------------------------------------------------------------------
    public final static String ICT_PROTEGE_IP_ADDRESS = "ict_protege.address";
    public final static String ICT_PROTEGE_MONITORING_PIN = "ict_protege.monitoring_pin";
    public final static String ICT_PROTEGE_ENCRYPTION_TYPE = "ict_protege.encryption_type";
    public final static String ICT_PROTEGE_ENCRYPTION_KEY = "ict_protege.encryption_key";

  // Instance Fields ------------------------------------------------------------------------------
    
    //IP:Port address list of the Protege controllers.
    private String address;
    //PIN number of the Protege user that has been set up for monitoring only. 
    private String monitoringPIN;
    //Encryption type that the ACP service is using.
    private String encryptionType;
    //Encryption key for the ACP service packets.
    private String encryptionKey;

  // Class Members --------------------------------------------------------------------------------
    
    public static ICTProtegeConfiguration readXML()
    {
        ICTProtegeConfiguration config = ServiceContext.getICTProtegeConfiguration();

        return (ICTProtegeConfiguration) Configuration.updateWithControllerXMLConfiguration(config);
    }

  // Public Instance Methods ----------------------------------------------------------------------
    
    public String getAddress()
    {
        return preferAttrCustomValue(ICT_PROTEGE_IP_ADDRESS, address);
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public String getMonitoringPIN()
    {
        return preferAttrCustomValue(ICT_PROTEGE_MONITORING_PIN, address);
    }

    public void setMonitoringPIN(String monitoringPIN)
    {
        this.monitoringPIN = monitoringPIN;
    }

    public String getEncryptionType()
    {
        return preferAttrCustomValue(ICT_PROTEGE_ENCRYPTION_TYPE, encryptionType);
    }

    public void setEncryptionType(String encryptionType)
    {
        this.encryptionType = encryptionType;
    }

    public String getEncryptionKey()
    {
        return preferAttrCustomValue(ICT_PROTEGE_ENCRYPTION_KEY, encryptionKey);
    }

    public void setEncryptionKey(String encryptionKey)
    {
        this.encryptionKey = encryptionKey;
    }

}
