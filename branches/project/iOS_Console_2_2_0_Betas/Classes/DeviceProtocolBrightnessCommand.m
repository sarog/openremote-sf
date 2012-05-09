//
//  DeviceProtocolBrightnessCommand.m
//  openremote
//
//  Created by Eric Bariaux on 09/05/12.
//  Copyright (c) 2012 OpenRemote, Inc. All rights reserved.
//

#import "DeviceProtocolBrightnessCommand.h"

@interface DeviceProtocolBrightnessCommand ()

- (void)brightnessChanged:(NSNotification *)notification;

@end

@implementation DeviceProtocolBrightnessCommand

- (void)brightnessChanged:(NSNotification *)notification
{
    [self publishValue];
}

- (void)startUpdating
{
    if ([[UIScreen mainScreen] respondsToSelector:@selector(setBrightness:)]) {
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(brightnessChanged::) name:UIScreenBrightnessDidChangeNotification object:nil];
    }
}

- (void)stopUpdating
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

- (NSString *)sensorValue
{
    if ([[UIScreen mainScreen] respondsToSelector:@selector(setBrightness:)]) {
        return [NSString stringWithFormat:@"%d", (int)([UIScreen mainScreen].brightness * 100.0)];
    }
    return @"";
}

@end