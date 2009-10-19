package org.openremote.beehive.api.service;

import java.util.List;

import org.openremote.beehive.api.dto.VendorDTO;

/**
 * Business service for <code>VendorDTO</code>
 *
 * @author allen.wei 2009-2-17
 */
public interface VendorService {

    /**
     * Gets all <code>VendorDTO</code> from beehive databse
     *
     * @return a list of VendorDTOs
     */
    List<VendorDTO> loadAllVendors();
}