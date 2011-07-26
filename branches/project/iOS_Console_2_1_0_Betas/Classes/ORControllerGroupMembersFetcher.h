//
//  ORControllerGroupMembersFetcher.h
//  openremote
//
//  Created by Eric Bariaux on 13/07/11.
//  Copyright 2011 OpenRemote, Inc. All rights reserved.
//

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