package org.openremote.beehive.rest;

import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.mock.MockDispatcherFactory;
import org.jboss.resteasy.plugins.server.resourcefactory.POJOResourceFactory;

/**
 * User: allenwei
 * Date: 2009-2-10
 * Time: 13:29:06
 */
public class RESTTestUtils {

    public static Dispatcher createDispatcher(Class clazz) {
        Dispatcher dispatcher = MockDispatcherFactory.createDispatcher();
        POJOResourceFactory factory = new POJOResourceFactory(clazz);
        dispatcher.getRegistry().addResourceFactory(factory);
        return dispatcher;
    }
}
