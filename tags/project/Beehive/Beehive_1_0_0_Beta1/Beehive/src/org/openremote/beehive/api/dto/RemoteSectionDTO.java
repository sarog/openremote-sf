package org.openremote.beehive.api.dto;


/**
 * A configuration section in a LIRC configuration file linked with a {@link Model}.
 * It is possible to have more than one remote configuration in a configuration file.
 * Each remote configuration in a file should go to the database as a separate remote record.
 * User can later combine multiple sections into a single configuration file which can support multiple remote devices.
 *
 * @author Dan 2009-2-6
 * @author allen.wei 2009-2-18
 */
@SuppressWarnings("serial")
public class RemoteSectionDTO extends BusinessEntityDTO {

    private boolean raw;

    private String comment;

    private String name = "UNKNOWN";


    public RemoteSectionDTO() {
        comment = "";
    }

    public boolean isRaw() {
        return raw;
    }

    public void setRaw(boolean raw) {
        this.raw = raw;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}