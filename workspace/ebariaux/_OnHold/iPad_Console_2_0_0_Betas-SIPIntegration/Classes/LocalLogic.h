/* OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2009, OpenRemote Inc.
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

#import <Foundation/Foundation.h>
#import "XMLEntity.h"

@class LocalSensor;
@class LocalCommand;
@class LocalTask;

/**
 * Example of panel.xml config for locallogic part
 *  <locallogic>
 *   <sensors>
 *     <sensor id="35" class="TimeManager" method="getDateTime" refreshRate="1000"/>
 *   </sensors>
 *
 *   <commands>
 *     <command id="21" class="ThermostatManager" method="increaseSetPointTemperature"/>
 *   </commands>
 *
 *   <tasks>
 *     <task id="1001" class="TemperatureManager" method="readTemperature" frequency="8000"/>
 *   </tasks>
 * </locallogic>
 */
@interface LocalLogic : XMLEntity <NSXMLParserDelegate> {
	NSMutableDictionary *sensors;
	NSMutableDictionary *commands;
	NSMutableDictionary *tasks;
}

- (LocalSensor *)sensorForId:(NSUInteger)anId;
- (LocalCommand *)commandForId:(NSUInteger)anId;
- (LocalTask *)taskForId:(NSUInteger)anId;

@end
