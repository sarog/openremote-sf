package org.openremote.controller.protocol.knx;

import java.util.Map;

import org.apache.log4j.Logger;
import org.openremote.controller.command.ExecutableCommand;
import org.openremote.controller.command.StatusCommand;
import org.openremote.controller.component.EnumSensorType;

public class KNXCommand implements ExecutableCommand, StatusCommand {
    
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
    

    /**
     * {@inheritDoc}
     */
    @Override public void send() {
      try
      {
        KNXConnection connection = getConnectionManager().getConnection();    
        connection.send(getGroupAddress(), getKnxCommandType());
      }
      catch (ConnectionException e)
      {
        log.error(e);   // TODO
      }
    }
    
    /**
     * {@inheritDoc}
     */
    public String read(EnumSensorType sensorType, Map<String, String> statusMap) {
       //TODO
       return null;
    }

}
