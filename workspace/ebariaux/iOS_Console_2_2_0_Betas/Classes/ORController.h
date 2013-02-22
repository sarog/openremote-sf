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
#import <CoreData/CoreData.h>
#import "ORControllerGroupMembersFetcher.h"
#import "ORControllerCapabilitiesFetcher.h"
#import "ORControllerPanelsFetcher.h"

@class ORConsoleSettings;
@class ORGroupMember;
@class ORControllerProxy;
@class Definition;
@class SensorStatusCache;
@class ClientSideRuntime;
@class Capabilities;

// TODO: do we really need to have those different notifications, can't we just pass the status as param to the notification

extern NSString *kORControllerGroupMembersFetchingNotification;
extern NSString *kORControllerGroupMembersFetchSucceededNotification;
extern NSString *kORControllerGroupMembersFetchFailedNotification;
extern NSString *kORControllerGroupMembersFetchRequiresAuthenticationNotification;

extern NSString *kORControllerCapabilitiesFetchStatusChange;
extern NSString *kORControllerPanelIdentitiesFetchStatusChange;

enum {
    FetchStatusUnknown = 0,
	Fetching,
    FetchSucceeded,
    FetchFailed,
    FetchRequiresAuthentication
};
typedef NSInteger ORControllerFetchStatus;


@interface ORController : NSManagedObject <ORControllerGroupMembersFetcherDelegate, ORControllerCapabilitiesFetcherDelegate, ORControllerPanelsFetcherDelegate> {
@private
    ORGroupMember *activeGroupMember;
    ORControllerProxy *proxy;
    ORControllerFetchStatus groupMembersFetchStatus;
}

@property (nonatomic, retain) NSString * primaryURL;
@property (nonatomic, retain) NSString *selectedPanelIdentity;
@property (nonatomic, retain) NSNumber * index;
@property (nonatomic, retain) NSString *userName;
@property (nonatomic, retain) NSString *password;
@property (nonatomic, retain) NSSet* groupMembers;
@property (nonatomic, retain) ORConsoleSettings * settingsForControllers;
@property (nonatomic, retain) ORConsoleSettings * settingsForSelectedController;

@property (nonatomic, readonly) NSString *selectedPanelIdentityDisplayString;

@property (nonatomic, assign) ORGroupMember *activeGroupMember;

@property (nonatomic, retain) Capabilities *capabilities;
@property (nonatomic, retain) NSString *controllerAPIVersion;
@property (nonatomic, retain) NSArray *panelIdentities;

@property (nonatomic, readonly, retain) ORControllerProxy *proxy;

@property (nonatomic, readonly) ORControllerFetchStatus groupMembersFetchStatus;
@property (nonatomic, readonly) ORControllerFetchStatus capabilitiesFetchStatus;
@property (nonatomic, readonly) ORControllerFetchStatus panelIdentitiesFetchStatus;

- (void)fetchGroupMembers;
- (void)cancelGroupMembersFetch;

- (void)fetchCapabilities;

- (void)fetchPanels;

- (void)addGroupMemberForURL:(NSString *)url;

- (BOOL)hasGroupMembers;
- (BOOL)hasCapabilities;
- (BOOL)hasPanelIdentities;

// TODO: re-check in model vs property
// TODO: this is not persisted but should be lazy loaded (! parsing is required -> we want to be able to notifiy user of progress and let him cancel)
@property (nonatomic, retain) Definition *definition;

@property (nonatomic, retain, readonly) SensorStatusCache *sensorStatusCache;
@property (nonatomic, retain, readonly) ClientSideRuntime *clientSideRuntime;

@end