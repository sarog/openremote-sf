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
package org.openremote.controller.utils;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.openremote.controller.command.DelayCommand;
import org.openremote.controller.command.ExecutableCommand;
import org.openremote.controller.protocol.infrared.IRCommand;
import org.openremote.controller.protocol.knx.KNXExecutableCommand;

@SuppressWarnings("serial")
/**
 * @author Javen
 */
public class MacrosIrDelayUtilTest extends TestCase {

   private List<ExecutableCommand> HaveNotIrCmd = new ArrayList<ExecutableCommand>(5) {
      {
         add(new IRCommand());
         add(new IRCommand());
         add(new DelayCommand());
         add(new IRCommand());
         add(new KNXExecutableCommand());
      }
   };

   private List<ExecutableCommand> ContainOneOrNoDelayCmdBetweenTwoIrCmd = new ArrayList<ExecutableCommand>(5) {
      {
         add(new IRCommand());
         add(new DelayCommand("200"));
         add(new IRCommand());
         add(new IRCommand());
         add(new DelayCommand("800"));
      }
   };

   private List<ExecutableCommand> ContainMultiDelayCmdBetweenTwoIrCmd = new ArrayList<ExecutableCommand>(5) {
      {
         add(new IRCommand());
         add(new DelayCommand("200"));
         add(new DelayCommand("200"));
         add(new IRCommand());
         add(new DelayCommand("200"));
         add(new DelayCommand("200"));
         add(new DelayCommand("200"));
         add(new IRCommand());
         add(new DelayCommand("800"));
         add(new IRCommand());
      }
   };
   /**
    * used to test a list not only has IrCommand and DelayCommand. 
    * If it is so, the list will not be changed.
    */
   public void testHaveNotOnlyIrcCmdAndDelayCmd() {
      MacrosIrDelayUtil.ensureDelayForIrCommand(HaveNotIrCmd);
      Assert.assertTrue(HaveNotIrCmd.size() == 5);
   }
   /**
    * used to test this case:  
    * There is one DelayCommand between two IrCommand or there is no DelayCommand between the nearest two IrCommand
    * result: a DelayCommand will be added between theses two IrCommand.
    */
   public void testContainOneOrNoDelayCmdBetweenTwoIrCmd() {
      MacrosIrDelayUtil.ensureDelayForIrCommand(ContainOneOrNoDelayCmdBetweenTwoIrCmd);
      Assert.assertTrue(ContainOneOrNoDelayCmdBetweenTwoIrCmd.size() == 7);
      Assert.assertTrue(ContainOneOrNoDelayCmdBetweenTwoIrCmd.get(0) instanceof IRCommand);
      Assert.assertTrue(((DelayCommand) ContainOneOrNoDelayCmdBetweenTwoIrCmd.get(1)).getDelaySeconds() == 200);
      Assert.assertTrue(((DelayCommand) ContainOneOrNoDelayCmdBetweenTwoIrCmd.get(2)).getDelaySeconds() == 300);
      Assert.assertTrue(ContainOneOrNoDelayCmdBetweenTwoIrCmd.get(3) instanceof IRCommand);
      Assert.assertTrue(((DelayCommand) ContainOneOrNoDelayCmdBetweenTwoIrCmd.get(4)).getDelaySeconds() == 500);
      Assert.assertTrue(ContainOneOrNoDelayCmdBetweenTwoIrCmd.get(5) instanceof IRCommand);
      Assert.assertTrue(((DelayCommand) ContainOneOrNoDelayCmdBetweenTwoIrCmd.get(6)).getDelaySeconds() == 800);
   }
   /**
    * used to test this case: 
    * There are more than one DelayCommand between the nearest two IrCommands.
    * result: If the total delay second is small than minimum delay seconds, a new DelayCommand will be added to make sure there be
    * at least minimum seconds between two IrCommand. else the delay will not be changed. 
    */
   public void testContainMultiDelayCmdBetweenTwoIrCmd() {
      MacrosIrDelayUtil.ensureDelayForIrCommand(ContainMultiDelayCmdBetweenTwoIrCmd);
      Assert.assertTrue(ContainMultiDelayCmdBetweenTwoIrCmd.size() == 11);
      Assert.assertTrue(ContainMultiDelayCmdBetweenTwoIrCmd.get(0) instanceof IRCommand);
      Assert.assertTrue(((DelayCommand) ContainMultiDelayCmdBetweenTwoIrCmd.get(1)).getDelaySeconds() == 200);
      Assert.assertTrue(((DelayCommand) ContainMultiDelayCmdBetweenTwoIrCmd.get(2)).getDelaySeconds() == 200);
      Assert.assertTrue(((DelayCommand) ContainMultiDelayCmdBetweenTwoIrCmd.get(3)).getDelaySeconds() == 100);
      Assert.assertTrue(ContainMultiDelayCmdBetweenTwoIrCmd.get(4) instanceof IRCommand);
      Assert.assertTrue(((DelayCommand) ContainMultiDelayCmdBetweenTwoIrCmd.get(5)).getDelaySeconds() == 200);
      Assert.assertTrue(((DelayCommand) ContainMultiDelayCmdBetweenTwoIrCmd.get(6)).getDelaySeconds() == 200);
      Assert.assertTrue(((DelayCommand) ContainMultiDelayCmdBetweenTwoIrCmd.get(7)).getDelaySeconds() == 200);
      Assert.assertTrue(ContainMultiDelayCmdBetweenTwoIrCmd.get(8) instanceof IRCommand);
      Assert.assertTrue(((DelayCommand) ContainMultiDelayCmdBetweenTwoIrCmd.get(9)).getDelaySeconds() == 800);
      Assert.assertTrue(ContainMultiDelayCmdBetweenTwoIrCmd.get(10) instanceof IRCommand);
   }
}
