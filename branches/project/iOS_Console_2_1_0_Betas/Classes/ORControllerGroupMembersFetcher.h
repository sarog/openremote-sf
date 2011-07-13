//
//  ORControllerGroupMembersFetcher.h
//  openremote
//
//  Created by Eric Bariaux on 13/07/11.
//  Copyright 2011 OpenRemote, Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "DataCapturingNSURLConnectionDelegate.h"

@protocol ORControllerGroupMembersFetcherDelegate

- (void)fetchGroupMembersDidSucceedWithMembers:(NSArray *)theMembers;

@optional
- (void)fetchGroupMembersDidFailWithError:(NSError *)error;
@end

@interface ORControllerGroupMembersFetcher : NSObject <NSXMLParserDelegate, DataCapturingNSURLConnectionDelegateDelegate> {
    NSMutableArray *members;
}

@property (nonatomic, assign) NSObject <ORControllerGroupMembersFetcherDelegate> *delegate;

- (void)fetch;

@end
