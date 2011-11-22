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
#import "DataCapturingNSURLConnectionDelegate.h"

@class ORController;

@protocol ORControllerGroupMembersFetcherDelegate

/**
 */
- (void)controller:(ORController *)aController fetchGroupMembersDidSucceedWithMembers:(NSArray *)theMembers;

@optional

/**
 */
- (void)controller:(ORController *)aController fetchGroupMembersDidFailWithError:(NSError *)error;

/**
 */
- (void)fetchGroupMembersRequiresAuthenticationForController:(ORController *)aController;

@end

@interface ORControllerGroupMembersFetcher : NSObject <NSXMLParserDelegate, DataCapturingNSURLConnectionDelegateDelegate> {
    NSMutableArray *members;
}

@property (nonatomic, assign) NSObject <ORControllerGroupMembersFetcherDelegate> *delegate;

- (id)initWithController:(ORController *)aController;
- (void)fetch;
- (void)cancelFetch;

@end