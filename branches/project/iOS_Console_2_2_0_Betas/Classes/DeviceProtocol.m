/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2012, OpenRemote Inc.
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
#import "DeviceProtocol.h"
#import "DeviceProtocolDateTimeCommand.h"
#import "DeviceProtocolBatteryLevelCommand.h"
#import "DeviceProtocolBrightnessCommand.h"
#import "DeviceProtocolSetBrightnessCommand.h"
#import "ClientSideBeanManager.h"
#import "ClientSideRuntime.h"

@interface DeviceProtocol()

@property (nonatomic, retain) ClientSideBeanManager *beanManager;

@end

@implementation DeviceProtocol

- (id)initWithRuntime:(ClientSideRuntime *)runtime
{
    self = [super initWithRuntime:runtime];
    if (self) {
        [self.beanManager registerClass:[DeviceProtocolDateTimeCommand class] forKey:@"DATE_TIME"];
        [self.beanManager registerClass:[DeviceProtocolBatteryLevelCommand class] forKey:@"BATTERY_LEVEL"];
        [self.beanManager registerClass:[DeviceProtocolBrightnessCommand class] forKey:@"SCREEN_BRIGHTNESS"];
        [self.beanManager registerClass:[DeviceProtocolSetBrightnessCommand class] forKey:@"SET_SCREEN_BRIGHTNESS"];
    }
    return self;
}

@synthesize beanManager;

@end