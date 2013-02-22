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
package org.openremote.beehive.repo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * DiffResult contains two version content set in left and right
 * @author Tomsky
 *
 */
public class DiffResult {
   private List<DifferenceLine> leftLines;
   private List<DifferenceLine> rightLines;

   public List<Line> getLeft() {
      List<Line> left = new ArrayList<Line>();
      if (leftLines != null) {
         left = getLines(leftLines);
      }
      return left;
   }

   public List<Line> getRight() {
      List<Line> right = new ArrayList<Line>();
      if (rightLines != null) {
         right = getLines(rightLines);
      }
      return right;
   }

   public void setLeft(List<DifferenceLine> leftLines) {
      this.leftLines = leftLines;
   }

   public void setRight(List<DifferenceLine> rightLines) {
      this.rightLines = rightLines;
   }

   public class Line {
      protected DifferenceLine data;

      public Line(DifferenceLine data) {
         this.data = data;
      }

      public int getChangeType() {
         return this.data.getType();
      }

      public String getNumber() {
         if (this.data.getNumber() == DifferenceLine.EMPTY_NUMBER) {
            return "&nbsp;";
         } else {
            return Integer.toString(this.data.getNumber() + 1);
         }
      }

      public String getLine() {
         return this.data.getLine();
      }

      public String getBackground() {
         if (this.data.getType() == DifferenceLine.ADDED) {
            return "#E0FFE0";
         } else if (this.data.getType() == DifferenceLine.DELETED) {
            return "#FFE3E3";
         } else if (this.data.getType() == DifferenceLine.MODIFIED) {
            return "#FEFFB2";
         } else {
            return "#FFFFFF";
         }
      }
   }

   private List<Line> getLines(List<DifferenceLine> diffLines) {
      List<Line> ret = new ArrayList<Line>();
      for (Iterator<DifferenceLine> i = diffLines.iterator(); i.hasNext();) {
         ret.add(new Line((DifferenceLine) i.next()));
      }
      return ret;
   }
}
