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
package org.openremote.beehive.serviceHibernateImpl;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;
import org.openremote.beehive.api.dto.VendorDTO;
import org.openremote.beehive.api.service.VendorService;
import org.openremote.beehive.domain.Vendor;

/**
 * {@inheritDoc}
 * 
 * @author allen 2009-2-17
 */
public class VendorServiceImpl extends BaseAbstractService<Vendor> implements VendorService {

   private static Logger logger = Logger.getLogger(VendorServiceImpl.class.getName());

   /**
    * {@inheritDoc }
    */
   public List<VendorDTO> loadAllVendors() {
      List<VendorDTO> vendorDTOs = new ArrayList<VendorDTO>();
      for (Vendor vendor : loadAll()) {
         VendorDTO vendorDTO = new VendorDTO();
         try {
            BeanUtils.copyProperties(vendorDTO, vendor);
         } catch (IllegalAccessException e) {
            logger.error("error occurs while BeanUtils.copyProperties(vendorDTO, vendor);");
         } catch (InvocationTargetException e) {
            logger.error("error occurs while BeanUtils.copyProperties(vendorDTO, vendor);");
         }
         vendorDTOs.add(vendorDTO);
      }
      return vendorDTOs;
   }

}
