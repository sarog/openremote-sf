/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2009, OpenRemote Inc.
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
package org.openremote.modeler.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name = "range")
public class Range extends BusinessEntity {

   private int min;
   private int max;
   private Sensor sensor;
   
   public Range() {
   }
   public Range(int min, int max) {
      this.min = min;
      this.max = max;
   }
   
   public int getMin() {
      return min;
   }
   public int getMax() {
      return max;
   }
   public void setMin(int min) {
      this.min = min;
   }
   public void setMax(int max) {
      this.max = max;
   }
   
   @OneToOne(fetch = FetchType.LAZY)
   @JoinColumn(nullable = false)
   public Sensor getSensor() {
      return sensor;
   }
   public void setSensor(Sensor sensor) {
      this.sensor = sensor;
   }
   
}
