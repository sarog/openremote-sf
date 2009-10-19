package org.openremote.beehive.api.dto;

import org.apache.commons.lang.StringUtils;

/**
 * Infrared Codes (arbitrary name-value pairs).
 * Typically consists of string names mapped to infrared code hex values.
 * <pre>
 * For example:
 * 	play	0x20
 * 	plus	0xD0
 * 	ffwd	0xE0
 * 	rev	0x10
 * 	minus	0xB0
 * 	menu	0x40
 * </pre>
 *
 * @author Dan 2009-2-6
 * @author allen.wei 2009-2-18
 */
@SuppressWarnings("serial")
public class CodeDTO extends BusinessEntityDTO {

    private String name;

    private String value;

    private String comment;


    public CodeDTO() {
        value = "";
        comment = "";
    }

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