package org.openremote.beehive.rest;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.openremote.beehive.api.dto.ModelDTO;

/**
 * In order to let  rest service to serialize list of model
 * User: allenwei
 * Date: 2009-2-10
 * Time: 13:57:29
 */
@XmlRootElement(name = "models")
public class ModelListing {

    private List<ModelDTO> Models = new ArrayList<ModelDTO>();

    public ModelListing() {
    }

    public ModelListing(List<ModelDTO> models) {
        Models = models;
    }

    @XmlElement(name = "model")
    public List<ModelDTO> getModels() {
        return Models;
    }

    public void setModels(List<ModelDTO> models) {
        Models = models;
    }
}
