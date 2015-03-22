//
//  SensorStatusCache.m
//  openremote
//
//  Created by Eric Bariaux on 04/05/12.
//  Copyright (c) 2012 OpenRemote, Inc. All rights reserved.
//

#import "SensorStatusCache.h"
#import "NotificationConstant.h"

@interface SensorStatusCache()

@property (nonatomic, retain) NSMutableDictionary *statusCache;
@property (nonatomic, retain) NSNotificationCenter *notificationCenter;
@end

@implementation SensorStatusCache

- (id)initWithNotificationCenter:(NSNotificationCenter *)aNotificationCenter
{
    self = [super init];
    if (self) {
        self.statusCache = [NSMutableDictionary dictionary];
        self.notificationCenter = aNotificationCenter;
    }
    return self;
}

- (void)dealloc
{
    self.notificationCenter = nil;
    self.statusCache = nil;
    [super dealloc];
}

- (void)publishNewValue:(NSString *)status forSensorId:(NSUInteger)sensorId
{
    [self.statusCache setObject:status forKey:[NSString stringWithFormat:@"%d", sensorId]];    
    [self.notificationCenter postNotificationName:[NSString stringWithFormat:NotificationPollingStatusIdFormat, sensorId] object:self];
}

- (NSString *)valueForSensorId:(NSUInteger)sensorId
{
    return [self.statusCache objectForKey:[NSString stringWithFormat:@"%d", sensorId]];
}

- (void)clearStatusCache
{
    [self.statusCache removeAllObjects];
}

@synthesize statusCache;
@synthesize notificationCenter;

@end