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

package org.openremote.irbuilder.domain;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.ArrayList;

/**
 * @author <a href="mailto:allen.wei@finalist.cn">allen.wei</a>
 */
@XStreamAlias("openremote")
public class IPhoneWrapper {
    @XStreamImplicit(itemFieldName = "activity")
    public ArrayList<Activity> activities = new ArrayList<Activity>();

    public ArrayList<Activity> getActivities() {
        return activities;
    }

    public void setActivities(ArrayList<Activity> activities) {
        this.activities = activities;
    }
}
