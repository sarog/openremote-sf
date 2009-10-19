package org.openremote.beehive.rest;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.openremote.beehive.api.dto.VendorDTO;

/**
 * In order to let  rest service to serialize list of vendor
 * User: allenwei
 * Date: 2009-2-10
 * Time: 13:48:42
 */
@XmlRootElement(name = "vendors")
public class VendorListing {

    private List<VendorDTO> vendors = new ArrayList<VendorDTO>();

    public VendorListing() {
    }

    public VendorListing(List<VendorDTO> vendors) {
        this.vendors.addAll(vendors);
    }

    @XmlElement(name = "vendor")
    public List<VendorDTO> getVendors() {
        return vendors;
    }

    public void setVendors(List<VendorDTO> vendors) {
        this.vendors = vendors;
    }
}
