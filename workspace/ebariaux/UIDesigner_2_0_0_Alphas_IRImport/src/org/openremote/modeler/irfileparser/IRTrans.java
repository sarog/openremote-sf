package org.openremote.modeler.irfileparser;

import com.google.gwt.user.client.rpc.IsSerializable;

public class IRTrans implements IsSerializable{

	private String ipAdress;
	private String udpPort;
	private String irLed;
	


	public IRTrans() {}



	public IRTrans(String ipAdress, String udpPort, String irLed) {
		super();
		this.ipAdress = ipAdress;
		this.udpPort = udpPort;
		this.irLed = irLed;
	}



	public String getIpAdress() {
		return ipAdress;
	}



	public void setIpAdress(String ipAdress) {
		this.ipAdress = ipAdress;
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
