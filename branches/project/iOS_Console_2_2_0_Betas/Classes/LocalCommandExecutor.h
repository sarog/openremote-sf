//
//  LocalCommandExecutor.h
//  openremote
//
//  Created by Eric Bariaux on 04/05/12.
//  Copyright (c) 2012 OpenRemote, Inc. All rights reserved.
//

#import <Foundation/Foundation.h>

@class LocalCommand;

@interface LocalCommandExecutor : NSObject

+ (void)executeCommands:(NSArray *)commands;
+ (void)executeCommand:(LocalCommand *)command;

@end