package org.openremote.modeler.irfileparser;

import com.google.gwt.user.client.rpc.IsSerializable;

public class IRTrans implements IsSerializable{
	private String ip;
	private String udpPort;
	private String irLed;
	
	public IRTrans() {
	}

	public IRTrans(String ip, String udpPort, String irLed) {
		this.ip = ip;
		this.udpPort = udpPort;
		this.irLed = irLed;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getUdpPort() {
		return udpPort;
	}

	public void setUdpPort(String udpPort) {
		this.udpPort = udpPort;
	}

	public String getIrLed() {
		return irLed;
	}

	public void setIrLed(String irLed) {
		this.irLed = irLed;
	}
	
}
