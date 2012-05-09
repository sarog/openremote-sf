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
#import "DeviceProtocolBatteryLevelCommand.h"

@interface DeviceProtocolBatteryLevelCommand()

- (void)batteryLevelChanged:(NSNotification *)notification;

@end

@implementation DeviceProtocolBatteryLevelCommand

- (void)batteryLevelChanged:(NSNotification *)notification
{
    if ([UIDevice currentDevice].batteryState != UIDeviceBatteryStateUnknown) {
        [self publishValue];
    }
}

- (void)startUpdating
{
    [UIDevice currentDevice].batteryMonitoringEnabled = YES;
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(batteryLevelChanged:) name:UIDeviceBatteryLevelDidChangeNotification object:nil];
}

- (void)stopUpdating
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

- (NSString *)sensorValue
{
    return [NSString stringWithFormat:@"%d", (int)([UIDevice currentDevice].batteryLevel * 100.0)];
}

@end