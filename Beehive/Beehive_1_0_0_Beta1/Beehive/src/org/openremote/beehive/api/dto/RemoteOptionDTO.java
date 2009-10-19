package org.openremote.beehive.api.dto;

import org.apache.commons.lang.StringUtils;

/**
 * <p>Infrared Options (prefixed name-value pairs)</p>
 * <p>There are various option fields in the configuration files. These are interpreted by the
 * specific IR transmitter LIRC device drivers to generate the proper bit sequences for
 * the low level device API.</p>
 * <p>Some options are obvious and used pretty much by all LIRC config files:</p>
 * <ul>
 * <li>name</li>
 * <li>flags</li>
 * <li>header</li>
 * <li>one</li>
 * <li>zero</li>
 * <li>bits</li>
 * <li>eps</li>
 * <li>aeps</li>
 * <li>gap</li>
 * </ul>
 *
 * @author Dan 2009-2-6
 * @author allen.wei 2009-2-18
 */
@SuppressWarnings("serial")
public class RemoteOptionDTO extends BusinessEntityDTO {

    private String name;

    private String value;

    private String comment;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
    
      public boolean isBlankComment() {
        return StringUtils.isBlank(getComment());
    }
    
}