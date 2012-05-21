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

@end

@implementation ORControllerProxy

- (id)initWithController:(ORController *)aController
{
    self = [super init];
    if (self) {
        self.controller = aController;
    }
    return self;
}

#pragma mark -

- (ORControllerCommandSender *)sendCommand:(NSString *)command forComponent:(Component *)component delegate:(NSObject <ORControllerCommandSenderDelegate> *)delegate
{
    ORControllerCommandSender *commandSender = [[ORControllerCommandSender alloc] initWithController:self.controller command:command component:component];
    commandSender.delegate = delegate;
    [commandSender send];
    return [commandSender autorelease];
}

- (ORControllerStatusRequestSender *)requestStatusForIds:(NSString *)ids delegate:(NSObject <ORControllerPollingSenderDelegate> *)delegate
{
    ORControllerStatusRequestSender *statusRequestSender = [[ORControllerStatusRequestSender alloc] initWithController:self.controller ids:ids];
    statusRequestSender.delegate = delegate;
    [statusRequestSender send];
    return [statusRequestSender autorelease];
}

- (ORControllerPollingSender *)requestPollingForIds:(NSString *)ids delegate:(NSObject <ORControllerPollingSenderDelegate> *)delegate
{
    ORControllerPollingSender *pollingSender = [[ORControllerPollingSender alloc] initWithController:self.controller ids:ids];
    pollingSender.delegate = delegate;
    [pollingSender send];
    return [pollingSender autorelease];
}

- (ORControllerPanelsFetcher *)fetchPanelsWithDelegate:(NSObject <ORControllerPanelsFetcherDelegate> *)delegate
{
    ORControllerPanelsFetcher *panelsFetcher = [[ORControllerPanelsFetcher alloc] initWithController:self.controller];
    panelsFetcher.delegate = delegate;
    [panelsFetcher send];
    return [panelsFetcher autorelease];
}

- (ORControllerCapabilitiesFetcher *)fetchCapabilitiesWithDelegate:(NSObject <ORControllerCapabilitiesFetcherDelegate> *)delegate
{
    ORControllerCapabilitiesFetcher *capabilitiesFetcher = [[ORControllerCapabilitiesFetcher alloc] initWithController:self.controller];
    capabilitiesFetcher.delegate = delegate;
    [capabilitiesFetcher send];
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

@end