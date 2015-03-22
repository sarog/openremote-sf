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
#import "LocalLogicParser.h"
#import "LocalLogic.h"
#import "SensorParser.h"
#import "CommandParser.h"
#import "TaskParser.h"
#import "XMLEntity.h"

@interface LocalLogicParser ()

@property (nonatomic, retain, readwrite) LocalLogic *localLogic;

@end
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
@implementation LocalLogicParser

- (void)dealloc
{
    self.localLogic = nil;
    [super dealloc];
}

- (id)initWithRegister:(DefinitionElementParserRegister *)aRegister attributes:(NSDictionary *)attributeDict;
{
    self = [super initWithRegister:aRegister attributes:attributeDict];
    if (self) {
        [self addKnownTag:SENSOR];
        [self addKnownTag:COMMAND];
        [self addKnownTag:TASK];
        LocalLogic *tmp = [[LocalLogic alloc] init];
        self.localLogic = tmp;
        [tmp release];
    }
    return self;
}

- (void)endSensorElement:(SensorParser *)parser
{
    [self.localLogic addSensor:parser.sensor];
}

- (void)endCommandElement:(CommandParser *)parser
{
    [self.localLogic addCommand:parser.command];
}

- (void)endTaskElement:(TaskParser *)parser
{
    [self.localLogic addTask:parser.task];
}

@synthesize localLogic;

@end