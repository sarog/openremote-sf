//
//  LocalCommandExecutor.h
//  openremote
//
//  Created by Eric Bariaux on 04/05/12.
//  Copyright (c) 2012 OpenRemote, Inc. All rights reserved.
//

#import <Foundation/Foundation.h>

@class LocalCommand;
@class LocalSensor;
@class ORController;

@interface ClientSideRuntime : NSObject

- (id)initWithController:(ORController *)aController;

- (void)executeCommands:(NSArray *)commands;
- (void)executeCommand:(LocalCommand *)command;

- (void)startUpdatingSensor:(LocalSensor *)sensor;
- (void)stopUpdatingSensor:(LocalSensor *)sensor;

@end