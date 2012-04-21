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
#import <Foundation/Foundation.h>
#import "ORControllerCommandSender.h"
#import "ORControllerPollingSender.h"
#import "ORControllerPanelsFetcher.h"
#import "ORControllerCapabilitiesFetcher.h"
#import "ORControllerGroupMembersFetcher.h"

@class Component;
@class ORController;

/**
 * Represents a proxy to an ORController, acts as the communication channel with it.
 */
@interface ORControllerProxy : NSObject {
}

- (ORControllerCapabilitiesFetcher *)fetchCapabilitiesWithDelegate:(NSObject <ORControllerCapabilitiesFetcherDelegate> *)delegate;
- (ORControllerCommandSender *)sendCommand:(NSString *)command forComponent:(Component *)component delegate:(NSObject <ORControllerCommandSenderDelegate> *)delegate;
- (ORControllerPollingSender *)requestStatusForIds:(NSString *)ids delegate:(NSObject <ORControllerPollingSenderDelegate> *)delegate;
- (ORControllerPollingSender *)requestPollingForIds:(NSString *)ids delegate:(NSObject <ORControllerPollingSenderDelegate> *)delegate;
- (ORControllerPanelsFetcher *)fetchPanelsWithDelegate:(NSObject <ORControllerPanelsFetcherDelegate> *)delegate;
- (ORControllerGroupMembersFetcher *)fetchGroupMembersWithDelegate:(NSObject <ORControllerGroupMembersFetcherDelegate> *)delegate;

- (id)initWithController:(ORController *)aController;

@end