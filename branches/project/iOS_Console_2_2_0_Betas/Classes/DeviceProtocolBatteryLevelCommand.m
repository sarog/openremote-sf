//
//  DeviceProtocolBatteryLevelCommand.m
//  openremote
//
//  Created by Eric Bariaux on 09/05/12.
//  Copyright (c) 2012 OpenRemote, Inc. All rights reserved.
//

#import "DeviceProtocolBatteryLevelCommand.h"
#import "ClientSideRuntime.h"
#import "SensorStatusCache.h"
#import "LocalSensor.h"

@interface DeviceProtocolBatteryLevelCommand()

- (void)batteryLevelChanged:(NSNotification *)notification;

@property (nonatomic, retain) NSMutableSet *sensorIds;
@property (nonatomic, retain) ClientSideRuntime *clientSideRuntime;

@end

@implementation DeviceProtocolBatteryLevelCommand

- (id)initWithRuntime:(ClientSideRuntime *)runtime
{
    self = [super init];
    if (self) {
        self.sensorIds = [NSMutableSet set];
        self.clientSideRuntime = runtime;
    }
    return self;
}

- (void)dealloc
{
    self.sensorIds = nil;
    self.clientSideRuntime = nil;
    [super dealloc];
}

- (void)startUpdatingSensor:(LocalSensor *)sensor
{
    if (![self.sensorIds count]) {
        [UIDevice currentDevice].batteryMonitoringEnabled = YES;
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(batteryLevelChanged:) name:UIDeviceBatteryLevelDidChangeNotification object:nil];
    }
    [self.clientSideRuntime.sensorStatusCache publishNewValue:[self sensorValue] forSensorId:sensor.componentId];
    [self.sensorIds addObject:[NSNumber numberWithInt:sensor.componentId]];
}

- (void)stopUpdatingSensor:(LocalSensor *)sensor
{
    [self.sensorIds removeObject:[NSNumber numberWithInt:sensor.componentId]];
    if (![self.sensorIds count]) {
        [[NSNotificationCenter defaultCenter] removeObserver:self];
    }
}

- (void)batteryLevelChanged:(NSNotification *)notification
{
    if ([UIDevice currentDevice].batteryState != UIDeviceBatteryStateUnknown) {
        NSString *batteryLevel = [self sensorValue];
        for (NSNumber *sensorId in self.sensorIds) {
            [self.clientSideRuntime.sensorStatusCache publishNewValue:batteryLevel forSensorId:[sensorId intValue]];
        }
    }
}

- (NSString *)sensorValue
{
    return [NSString stringWithFormat:@"%d", (int)([UIDevice currentDevice].batteryLevel * 100.0)];
}

@synthesize sensorIds;
@synthesize clientSideRuntime;

@end