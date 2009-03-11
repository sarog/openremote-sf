package org.openremote.beehive.utils;

import java.util.Properties;

/**
 * @author allen.wei 2009-2-18
 */
@SuppressWarnings("serial")
public class HibernateProperties extends Properties {
     /**
     * For Spring IoC, set the properties that this object wraps.
     * @param originalProperties The properties to wrap.
     */
    public void setOriginalProperties(Properties originalProperties) {
        for (Object propertyKey : originalProperties.keySet()) {
            put(propertyKey, originalProperties.get(propertyKey));
        }
    }
}
