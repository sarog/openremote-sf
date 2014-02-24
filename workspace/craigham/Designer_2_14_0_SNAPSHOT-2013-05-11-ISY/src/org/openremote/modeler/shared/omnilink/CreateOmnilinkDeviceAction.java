package org.openremote.modeler.shared.omnilink;

import net.customware.gwt.dispatch.shared.Action;

public class CreateOmnilinkDeviceAction implements Action<CreateOmnilinkDeviceResult> {

	private String host;
	private int port;
	private String key1;
	private String key2;

	public CreateOmnilinkDeviceAction() {
		super();
	}

	public CreateOmnilinkDeviceAction(String host, int port, String key1,
			String key2) {
		super();
		this.host = host;
		this.port = port;
		this.key1 = key1;
		this.key2 = key2;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getKey1() {
		return key1;
	}

	public void setKey1(String key1) {
		this.key1 = key1;
	}

	public String getKey2() {
		return key2;
	}

	public void setKey2(String key2) {
		this.key2 = key2;
	}
}
