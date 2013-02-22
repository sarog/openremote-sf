/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2012, OpenRemote Inc.
 *
 * See the contributors.txt file in the distribution for a
 * full listing of individual contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
#import "ORControllerProxy.h"
#import "ORController.h"
#import "Component.h"

@interface ORControllerProxy ()

@property (nonatomic, assign) ORController *controller;
@property (nonatomic, retain) NSMutableArray *commandsQueue;

- (void)queueCommand:(ORControllerSender *)command;
- (void)processQueue;

@end

@implementation ORControllerProxy

- (id)initWithController:(ORController *)aController
{
    self = [super init];
    if (self) {
        self.controller = aController;
        self.commandsQueue = [NSMutableArray array];
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(controllerFetchStatusChanged:) name:kORControllerGroupMembersFetchingNotification object:self.controller];
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(controllerFetchStatusChanged:) name:kORControllerGroupMembersFetchSucceededNotification object:self.controller];
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(controllerFetchStatusChanged:) name:kORControllerGroupMembersFetchFailedNotification object:self.controller];
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(controllerFetchStatusChanged:) name:kORControllerCapabilitiesFetchStatusChange object:self.controller];
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(controllerFetchStatusChanged:) name:kORControllerPanelIdentitiesFetchStatusChange object:self.controller];
    }
    return self;
}

- (void)dealloc
{
    self.commandsQueue = nil;
    self.controller = nil;
    [super dealloc];
}

#pragma mark -

- (void)controllerFetchStatusChanged:(NSNotification *)notification
{
    [self processQueue];
}

#pragma mark -

/**
 * Stores a command to controller for later execution.
 */
- (void)queueCommand:(ORControllerSender *)command
{
    [self.commandsQueue addObject:command];
}

/**
 * Processes all stored commands to controller.
 * This first implementation just sends them all to the controller.
 */
- (void)processQueue
{
    NSMutableArray *commandsToRemove = [NSMutableArray array];
    for (ORControllerSender *command in self.commandsQueue) {
        if ([command shouldExecuteNow]) {
            [command send];
            [commandsToRemove addObject:command];
        }
    }
    [self.commandsQueue removeObjectsInArray:commandsToRemove];
}

#pragma mark -

- (ORControllerCommandSender *)sendCommand:(NSString *)command forComponent:(Component *)component delegate:(NSObject <ORControllerCommandSenderDelegate> *)delegate
{
    ORControllerCommandSender *commandSender = [[ORControllerCommandSender alloc] initWithController:self.controller command:command component:component];
    commandSender.delegate = delegate;
    [self queueCommand:commandSender];
    [self processQueue];
    return [commandSender autorelease];
}

- (ORControllerStatusRequestSender *)requestStatusForIds:(NSString *)ids delegate:(NSObject <ORControllerPollingSenderDelegate> *)delegate
{
    ORControllerStatusRequestSender *statusRequestSender = [[ORControllerStatusRequestSender alloc] initWithController:self.controller ids:ids];
    statusRequestSender.delegate = delegate;
    [self queueCommand:statusRequestSender];
    [self processQueue];
    return [statusRequestSender autorelease];
}

- (ORControllerPollingSender *)requestPollingForIds:(NSString *)ids delegate:(NSObject <ORControllerPollingSenderDelegate> *)delegate
{
    ORControllerPollingSender *pollingSender = [[ORControllerPollingSender alloc] initWithController:self.controller ids:ids];
    pollingSender.delegate = delegate;
    [self queueCommand:pollingSender];
    [self processQueue];
    return [pollingSender autorelease];
}

- (ORControllerPanelsFetcher *)fetchPanelsWithDelegate:(NSObject <ORControllerPanelsFetcherDelegate> *)delegate
{
    ORControllerPanelsFetcher *panelsFetcher = [[ORControllerPanelsFetcher alloc] initWithController:self.controller];
    panelsFetcher.delegate = delegate;
    [self queueCommand:panelsFetcher];
    [self processQueue];
    return [panelsFetcher autorelease];
}

- (ORControllerCapabilitiesFetcher *)fetchCapabilitiesWithDelegate:(NSObject <ORControllerCapabilitiesFetcherDelegate> *)delegate
{
    ORControllerCapabilitiesFetcher *capabilitiesFetcher = [[ORControllerCapabilitiesFetcher alloc] initWithController:self.controller];
    capabilitiesFetcher.delegate = delegate;
    [self queueCommand:capabilitiesFetcher];
    [self processQueue];
    return [capabilitiesFetcher autorelease];
}






- (ORControllerGroupMembersFetcher *)fetchGroupMembersWithDelegate:(NSObject <ORControllerGroupMembersFetcherDelegate> *)delegate
{
    ORControllerGroupMembersFetcher *groupMembersFetcher = [[ORControllerGroupMembersFetcher alloc] initWithController:self.controller];
    groupMembersFetcher.delegate = delegate;
    [groupMembersFetcher fetch];
    return [groupMembersFetcher autorelease];
}

@synthesize controller;
@synthesize commandsQueue;

@end