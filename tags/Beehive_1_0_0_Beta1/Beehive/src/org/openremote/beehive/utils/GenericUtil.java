package org.openremote.beehive.utils;

import java.lang.reflect.ParameterizedType;

/**
 * Generic utility class
 * User: allenwei
 * Date: 2009-2-13
 * Time: 10:57:22
 */
public class GenericUtil {

    private GenericUtil() {
    }

    /**
     * Method for finding out of what type a parameterized generic class is.
     *
     * @param clazz The class to get the name of
     * @return classname
     */
    @SuppressWarnings("unchecked")
	public static Class getClassForGenericType(Class<?> clazz) {
        ParameterizedType parameterizedType = (ParameterizedType) clazz
                .getGenericSuperclass();
        return (Class<?>) (parameterizedType.getActualTypeArguments()[0]);
    }
}
