package org.openremote.beehive.api.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import org.openremote.beehive.api.dto.ModelDTO;

/**
 * Business service for <code>ModelDTO</code>
 *
 * @author allen.wei 2009-2-17
 */
public interface ModelService {
    /**
     * Gets all <code>ModelDTOs</code> belongs to certain <code>VendorDTO</code> according to it's id
     *
     * @param vendorName vendor name
     * @return list of ModelDTOs
     */
    List<ModelDTO> findModelsByVendorName(String vendorName);

    /**
     * Gets all <code>ModelDTOs</code> belongs to certain <code>VendorDTO</code> according to it's name
     *
     * @param vendorId vendor id
     * @return list of ModelDTOs
     */
    List<ModelDTO> findModelsByVendorId(long vendorId);

    /**
     * loads <code>ModelDTO</code> by <code>VendorDTO</code> name and <code>ModelDTO</code> name
     *
     * @param vendorName name of VendorDTO
     * @param modelName  name of  ModelDTO
     * @return ModelDTO
     */
    ModelDTO loadByVendorNameAndModelName(String vendorName, String modelName);

    /**
     *  loads <code>ModelDTO</code> by id 
     * @param modelId
     * @return
     */
    ModelDTO loadModelById(long modelId);
    
    /**
     * Allows to import a LIRC Configuration file
     *
     * @param fis        FileInputStream of the LIRC Configuration file
     * @param vendorName its vendor name
     * @param modelName  its model name
     */
    void add(FileInputStream fis, String vendorName, String modelName);

     /**
     * Allows to export the content text of a LIRC Configuration. This will NOT
     * lead to disk writes.
     *
     * @param id the target LIRC Configuration id
     * @return the content text
     */
    String exportText(long id);


    /**
     * Allows to export the <code>File</code> of a LIRC Configuration. This will lead to
     * disk writes.
     *
     * @param id the target LIRC Configuration id
     * @return the file
     */
    File exportFile(long id);

     /**
     * Allows to export the file of a LIRC Configuration. This will lead to disk
     * writes.
     *
     * @param id the target LIRC Configuration id
     * @return the file URL to be downloaded
     */
    String downloadFile(long id);

     /**
     * Allows to export the file of a LIRC Configuration. This will NOT lead to disk
     * writes.
     *
     * @param id the target LIRC Configuration id
     * @return the file OutputStream to be downloaded
     */
    InputStream exportStream(long id);
}