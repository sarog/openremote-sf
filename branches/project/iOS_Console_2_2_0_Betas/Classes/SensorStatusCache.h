//
//  SensorStatusCache.h
//  openremote
//
//  Created by Eric Bariaux on 04/05/12.
//  Copyright (c) 2012 OpenRemote, Inc. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface SensorStatusCache : NSObject

- (id)initWithNotificationCenter:(NSNotificationCenter *)aNotificationCenter;

- (void)publishNewValue:(NSString *)status forSensorId:(NSString *)sensorId;
- (NSString *)valueForSensorId:(NSString *)sensorId;
- (void)clearStatusCacheRemovingObservers:(BOOL)removeObservers;

@end