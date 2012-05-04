//
//  LocalCommandExecutor.m
//  openremote
//
//  Created by Eric Bariaux on 04/05/12.
//  Copyright (c) 2012 OpenRemote, Inc. All rights reserved.
//

#import "LocalCommandExecutor.h"
#import "ORConsoleSettingsManager.h"
#import "ORConsoleSettings.h"
#import "ORController.h"
#import "Definition.h"
#import "LocalController.h"
#import "LocalCommand.h"
#import "SIPProtocol.h"

@implementation LocalCommandExecutor

+ (void)executeCommands:(NSArray *)commands
{
    for (NSNumber *commandId in commands) {
        LocalCommand *command = [[[ORConsoleSettingsManager sharedORConsoleSettingsManager] consoleSettings].selectedController.definition.localController commandForId:[commandId intValue]];
        if (command) {
            [self executeCommand:command];
        }
    }
}

+ (void)executeCommand:(LocalCommand *)command
{
    
    // TODO Based on a registry, map protocol -> ClientSideProtocol implementation
    if ([@"console_sip" isEqualToString:command.protocol]) {
        
        // TODO: have a mechanism to cache protocols and not always instantiate
        id <ClientSideProtocol> protocol = [[SIPProtocol alloc] init];
        [protocol executeCommand:command];
        [protocol release];
    }
}

@end