/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2011, OpenRemote Inc.
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

@class ORConsoleSettings;
@class ORGroupMember;
@class ORControllerProxy;

#define kORControllerGroupMembersFetchingNotification @"kORControllerGroupMembersFetchingNotification"
#define kORControllerGroupMembersFetchSucceededNotification @"kORControllerGroupMembersFetchSucceededNotification"
#define kORControllerGroupMembersFetchFailedNotification @"kORControllerGroupMembersFetchFailedNotification"
#define kORControllerGroupMembersFetchRequiresAuthenticationNotification @"kORControllerGroupMembersFetchRequiresAuthenticationNotification"

@interface ORController : NSManagedObject <ORControllerGroupMembersFetcherDelegate> {
@private
    ORGroupMember *activeGroupMember;
    ORControllerProxy *proxy;
}

@property (nonatomic, retain) NSString * primaryURL;
@property (nonatomic, retain) NSString *selectedPanelIdentity;
@property (nonatomic, retain) NSNumber * index;
@property (nonatomic, retain) NSString *userName;
@property (nonatomic, retain) NSString *password;
@property (nonatomic, retain) NSSet* groupMembers;
@property (nonatomic, retain) ORConsoleSettings * settingsForAutoDiscoveredControllers;
@property (nonatomic, retain) ORConsoleSettings * settingsForConfiguredControllers;
@property (nonatomic, retain) ORConsoleSettings * settingsForSelectedDiscoveredController;
@property (nonatomic, retain) ORConsoleSettings * settingsForSelectedConfiguredController;

@property (nonatomic, readonly) NSString *selectedPanelIdentityDisplayString;

@property (nonatomic, assign) ORGroupMember *activeGroupMember;

@property (nonatomic, readonly, retain) ORControllerProxy *proxy;

- (void)fetchGroupMembers;
- (void)addGroupMemberForURL:(NSString *)url;

@end
