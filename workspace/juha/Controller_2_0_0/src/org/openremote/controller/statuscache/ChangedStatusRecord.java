/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2011, OpenRemote Inc.
 *
 * See the contributors.txt file in the distribution for a
 * full listing of individual contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.openremote.controller.statuscache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * TODO:
 *
 *  - See relevant task ORCJAVA-208 (http://jira.openremote.org/browse/ORCJAVA-208)
 *    This should be part of internal implementation of StatusCache
 *
 *  - See relevant task ORCJAVA-165 (http://jira.openremote.org/browse/ORCJAVA-165)
 *
 *
 *
 * A changed status record.
 *
 * This Record is used to record the skipped changed statuses and waited changed statuses .
 * 
 * @author Handy.Wang 2009-10-23
 */
public class ChangedStatusRecord
{
   
  /** A logical identity of panel */
  private String deviceID;

  /** The ids a polling request contains */
  private List<Integer> pollingSensorIDs = new ArrayList<Integer>(1);

  /** The ids whose status had changed in the statusChangedSensorIDs */
  private Set<Integer> statusChangedSensorIDs = new HashSet<Integer>(3);



  // Constructors ---------------------------------------------------------------------------------



  public ChangedStatusRecord(String deviceID, List<Integer> pollingSensorIDs)
  {
    this.deviceID = deviceID;
    this.pollingSensorIDs = pollingSensorIDs;
  }




  public String getDeviceID()
  {
    return deviceID;
  }

  public List<Integer> getPollingSensorIDs()
  {
   return pollingSensorIDs;
  }


  public Set<Integer> getStatusChangedSensorIDs()
  {
     return statusChangedSensorIDs;
  }

  public void setStatusChangedSensorIDs(Set<Integer> statusChangedSensorIDs)
  {
     this.statusChangedSensorIDs = statusChangedSensorIDs;
  }



  @Override public boolean equals(Object obj)
  {
    // TODO : this implementation looks pretty bad  [JPL]

    if (obj == null || !(obj instanceof ChangedStatusRecord))
    {
       return false;
    }

    ChangedStatusRecord timeoutRecord = (ChangedStatusRecord) obj;

    if ("".equals(timeoutRecord.getDeviceID()) || !timeoutRecord.getDeviceID().equals(this.deviceID))
    {
       return false;
    }

    if (timeoutRecord.getPollingSensorIDs().isEmpty()
          || timeoutRecord.getPollingSensorIDs().size() != this.pollingSensorIDs.size())
    {
       return false;
    }

    Collections.sort(this.getPollingSensorIDs(), new PollingSensorIDListComparator());
    Collections.sort(timeoutRecord.getPollingSensorIDs(), new PollingSensorIDListComparator());

    for (int i = 0; i < timeoutRecord.getPollingSensorIDs().size(); i++)
    {
       if (!this.getPollingSensorIDs().get(i).equals(timeoutRecord.getPollingSensorIDs().get(i)))
       {
          return false;
       }
    }

    return true;
  }


  @Override public String toString()
  {
     StringBuffer sb = new StringBuffer();

     sb.append("DEVICEID:" + deviceID);
     sb.append("\tsensorID:" + this.pollingSensorIDs.toString());
     sb.append("statusChangedSensorID:" + this.statusChangedSensorIDs);
     return sb.toString();
  }



}
