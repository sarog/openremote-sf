package org.openremote.beehive.rest;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

/**
 * Registers all the REST services
 * User: allenwei
 * Date: 2009-2-9
 * Time: 14:50:35
 */
public class BeehiveApplication extends Application {
    private Set<Object> singletons = new HashSet<Object>();
    private Set<Class<?>> empty = new HashSet<Class<?>>();

    public BeehiveApplication() {
        singletons.add(new VendorRESTService());
        singletons.add(new ModelRESTService());
        singletons.add(new LIRCConfigFileRESTService());
    }

    @Override
    public Set<Class<?>> getClasses() {
        return empty;
    }

    @Override
    public Set<Object> getSingletons() {
        return singletons;
    }
}

