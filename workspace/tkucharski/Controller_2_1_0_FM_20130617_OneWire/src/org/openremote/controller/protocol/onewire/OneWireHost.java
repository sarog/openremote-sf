package org.openremote.controller.protocol.onewire;

/**
 * Simple composite to store owserver host and port number within one object. Used in {@org.openremote.controller.protocol.onewire.OneWireHostFactory}
 * @author Tom Kucharski <kucharski.tom@gmail.com>
 */
public class OneWireHost {

	private String hostname;
	private int port;

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("OneWireHost{");
		sb.append("hostname='").append(hostname).append('\'');
		sb.append(", port=").append(port);
		sb.append('}');
		return sb.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		OneWireHost that = (OneWireHost) o;

		if (port != that.port) return false;
		if (hostname != null ? !hostname.equals(that.hostname) : that.hostname != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = hostname != null ? hostname.hashCode() : 0;
		result = 31 * result + port;
		return result;
	}
}
