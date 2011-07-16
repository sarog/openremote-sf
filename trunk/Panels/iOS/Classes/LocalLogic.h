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
