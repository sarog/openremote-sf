//
//  DeviceProtocolBatteryLevelCommand.m
//  openremote
//
//  Created by Eric Bariaux on 09/05/12.
//  Copyright (c) 2012 OpenRemote, Inc. All rights reserved.
//

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