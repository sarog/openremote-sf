package org.openremote.controller.protocol.onewire;

import java.util.HashMap;
import java.util.Map;
import org.owfs.jowfsclient.OwfsConnectionFactory;

/**
 * Storage for 1-wire servers, Factory Method that returns single OwfsConnectionFactory per server configuration.
 *
 * @author Tom Kucharski <kucharski.tom@gmail.com>
 */
public class OneWireHostFactory {

	private Map<OneWireHost, OwfsConnectionFactory> commands = new HashMap<OneWireHost, OwfsConnectionFactory>();

	public OwfsConnectionFactory loadOrCreate(OneWireHost key) {
		OwfsConnectionFactory object = commands.get(key);
		if (object == null) {
			object = new OwfsConnectionFactory(key.getHostname(), key.getPort());
			commands.put(key,object);
		}
		return object;
	}
}
