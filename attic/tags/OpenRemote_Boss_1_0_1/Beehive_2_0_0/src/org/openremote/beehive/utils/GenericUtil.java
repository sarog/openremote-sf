/* OpenRemote, the Home of the Digital Home.
 * Copyright 2008, OpenRemote Inc.
 * 
 * See the contributors.txt file in the distribution for a
 * full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3.0 of
 * the License, or (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.openremote.beehive.utils;

import java.lang.reflect.ParameterizedType;

/**
 * Generic utility class User: allenwei Date: 2009-2-13 Time: 10:57:22
 */
public class GenericUtil {

   private GenericUtil() {
   }

   /**
    * Method for finding out of what type a parameterized generic class is.
    * 
    * @param clazz
    *           The class to get the name of
    * @return classname
    */
   @SuppressWarnings("unchecked")
   public static Class getClassForGenericType(Class<?> clazz) {
      ParameterizedType parameterizedType = (ParameterizedType) clazz.getGenericSuperclass();
      return (Class<?>) (parameterizedType.getActualTypeArguments()[0]);
   }
}
