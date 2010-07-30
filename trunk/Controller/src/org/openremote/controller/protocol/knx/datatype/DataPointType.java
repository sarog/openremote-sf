/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2010, OpenRemote Inc.
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
package org.openremote.controller.protocol.knx.datatype;

/**
 * TODO
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public interface DataPointType 
{

  enum BooleanDataPointType implements DataPointType
  {
    SWITCH, BOOL, ENABLE, RAMP, ALARM, BINARY_VALUE, STEP, UP_DOWN, OPEN_CLOSE,
    START, STATE, INVERT, DIM_SEND_STYLE, INPUT_SOURCE
  }

  enum Control1BitDataPointType implements DataPointType
  {
    SWITCH_CONTROL, BOOL_CONTROL, ENABLE_CONTROL, RAMP_CONTROL, ALARM_CONTROL,
    BINARY_VALUE_CONTROL, STEP_CONTROL, DIRECTION1_CONTROL, DIRECTION2_CONTROL,
    START_CONTROL, STATE_CONTROL, INVERT_CONTROL
  }

  enum Control3BitDataPointType implements DataPointType
  {
    CONTROL_DIMMING, CONTROL_BLINDS, MODE_BOILER
  }

  enum Unsigned8BitValue implements DataPointType
  {
    SCALING, ANGLE, RELPOS_VALVE, VALUE_1_UCOUNT
  }
}
