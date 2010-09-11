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
package org.openremote.beehive.serviceHibernateImpl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.openremote.beehive.domain.BusinessEntity;
import org.openremote.beehive.utils.GenericUtil;

/**
 * The common service for all the services.
 * <p/>
 * User: allenwei Date: 2009-2-13 Time: 10:49:20
 */
public abstract class BaseAbstractService<T extends BusinessEntity> {

   protected GenericDAO genericDAO;

   public void setGenericDAO(GenericDAO genericDAO) {
      this.genericDAO = genericDAO;
   }

   /**
    * Return the persistent instance of the given entity class with the given identifier
    * 
    * @param id
    *           the identifier of the persistent instance
    * @return the persistent instance
    */
   @SuppressWarnings("unchecked")
   public T loadById(long id) {
      return (T) genericDAO.loadById(GenericUtil.getClassForGenericType(this.getClass()), id);
   }

   /**
    * Return all persistent instances of the given entity class. Note: Use queries or criteria for retrieving a specific
    * subset.
    * 
    * @return List containing 0 or more persistent instances
    */
   @SuppressWarnings("unchecked")
   public List<T> loadAll() {
      return genericDAO.loadAll(GenericUtil.getClassForGenericType(this.getClass()));
   }

   public DetachedCriteria getDetachedCriteria() {
      return DetachedCriteria.forClass(GenericUtil.getClassForGenericType(this.getClass()));
   }

}
