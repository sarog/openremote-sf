package org.openremote.controller.protocol.knx;

import org.apache.log4j.Logger;

public class KNXCommand {
    
    protected final static Logger log = Logger.getLogger(KNXCommandBuilder.KNX_LOG_CATEGORY);
    
    private String groupAddress = null;
    private KNXConnectionManager connectionManager = null;
    private KNXCommandType knxCommandType = null;
    
    public KNXCommand() {
        super();
    }

    public KNXCommand(KNXConnectionManager connectionManager, String groupAddress, KNXCommandType knxCommandType) {
        super();
        this.knxCommandType = knxCommandType;
        this.connectionManager = connectionManager;
        this.groupAddress = groupAddress;
    }

    public String getGroupAddress() {
        return groupAddress;
    }

    public void setGroupAddress(String groupAddress) {
        this.groupAddress = groupAddress;
    }

    public KNXConnectionManager getConnectionManager() {
        return connectionManager;
    }

    public void setConnectionManager(KNXConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }
    
    public KNXCommandType getKnxCommandType() {
        return knxCommandType;
    }

    public void setKnxCommandType(KNXCommandType knxCommandType) {
        this.knxCommandType = knxCommandType;
    }

}
