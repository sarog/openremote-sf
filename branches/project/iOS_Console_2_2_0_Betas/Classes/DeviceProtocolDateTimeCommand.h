//
//  DeviceProtocolDateTimeCommand.h
//  openremote
//
//  Created by Eric Bariaux on 08/05/12.
//  Copyright (c) 2012 OpenRemote, Inc. All rights reserved.
//

#import <Foundation/Foundation.h>

@class LocalSensor;
@class ClientSideRuntime;

@interface DeviceProtocolDateTimeCommand : NSObject

- (id)initWithRuntime:(ClientSideRuntime *)runtime;

- (void)startUpdatingSensor:(LocalSensor *)sensor;
- (void)stopUpdatingSensor:(LocalSensor *)sensor;

@end