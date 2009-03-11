package org.openremote.beehive.api.dto;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * This is the second level hierarchy, for example what is shown in
 * http://lirc.sourceforge.net/remotes/sony.
 *
 * @author allen.wei 2009-2-17
 */
@SuppressWarnings("serial")
@XmlRootElement(name = "model")
public class ModelDTO extends BusinessEntityDTO {
    private String name;
    private String fileName;

    @XmlElement
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
