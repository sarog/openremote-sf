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

import java.util.Date;

import org.openremote.beehive.api.service.SyncHistoryService;
import org.openremote.beehive.domain.SyncHistory;

/**
 * The Class SyncHistoryServiceImpl.
 * 
 * @author Tomsky
 */
public class SyncHistoryServiceImpl extends BaseAbstractService<SyncHistory> implements SyncHistoryService {

   /**
    * {@inheritDoc}
    */
   public SyncHistory getLatest() {
      return genericDAO.getByMaxId(SyncHistory.class);
   }

   /**
    * {@inheritDoc}
    */
   public void save(SyncHistory syncHistory) {
      genericDAO.save(syncHistory);
   }

   /**
    * {@inheritDoc}
    */
   public void update(String status, Date endTime) {
      SyncHistory dbSyncHistory = getLatest();
      dbSyncHistory.setStatus(status);
      dbSyncHistory.setEndTime(endTime);
      genericDAO.merge(dbSyncHistory);
   }

   public SyncHistory getLatestByType(String type) {
      return genericDAO.getByMaxNonIdField(SyncHistory.class, "type", type);
   }
   
}
