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

- (void)publishNewValue:(NSString *)status forSensorId:(NSString *)sensorId
{
    [self.statusCache setObject:status forKey:sensorId];    
    [self.notificationCenter postNotificationName:[NSString stringWithFormat:NotificationPollingStatusIdFormat, [sensorId intValue]] object:self];
}

- (NSString *)valueForSensorId:(NSString *)sensorId
{
    return [self.statusCache objectForKey:sensorId];
}

- (void)clearStatusCacheRemovingObservers:(BOOL)removeObservers
{
    [self.statusCache removeAllObjects];
    if (removeObservers) {
        // TODO, not sure we can do this
    }
}

@synthesize statusCache;
@synthesize notificationCenter;

@end