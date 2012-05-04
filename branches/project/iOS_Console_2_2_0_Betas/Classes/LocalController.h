//
//  LocalController.h
//  openremote
//
//  Created by Eric Bariaux on 03/05/12.
//  Copyright (c) 2012 OpenRemote, Inc. All rights reserved.
//

#import <Foundation/Foundation.h>

@class ControllerComponent;
@class LocalCommand;
@class LocalSensor;

@interface LocalController : NSObject

- (void)addComponent:(ControllerComponent *)component;
- (void)addCommand:(LocalCommand *)command;
- (void)addSensor:(LocalSensor *)sensor;

- (ControllerComponent *)componentForId:(NSUInteger)anId;
- (LocalCommand *)commandForId:(NSUInteger)anId;
- (LocalSensor *)sensorForId:(NSUInteger)anId;

- (NSArray *)commandsForComponentId:(NSUInteger)anId action:(NSString *)action;

@end