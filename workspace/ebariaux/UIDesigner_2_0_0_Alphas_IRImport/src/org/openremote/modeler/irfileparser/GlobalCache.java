package org.openremote.modeler.irfileparser;

import com.google.gwt.user.client.rpc.IsSerializable;

public class GlobalCache implements IsSerializable{
	
	private String ipAddress;
	private String tcpPort;
	private String connector;
	public GlobalCache() {
	}
	
	public GlobalCache(String ipAddress, String tcpPort, String connector) {
		this.ipAddress = ipAddress;
		this.tcpPort = tcpPort;
		this.connector = connector;
	}

	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	public String getTcpPort() {
		return tcpPort;
	}
	public void setTcpPort(String tcpPort) {
		this.tcpPort = tcpPort;
	}
	public String getConnector() {
		return connector;
	}
	public void setConnector(String connector) {
		this.connector = connector;
	}

}
