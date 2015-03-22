//
//  ORControllerPollingSender.h
//  openremote
//
//  Created by Eric Bariaux on 09/05/11.
//  Copyright 2011 OpenRemote, Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ControllerRequest.h"
#import "UpdateController.h"

@protocol ORControllerPollingSenderDelegate <NSObject>

// Note that this applies to both polling and status request, even if the name would indicate otherwise
- (void)pollingDidFailWithError:(NSError *)error;
- (void)pollingDidSucceed;
- (void)pollingDidTimeout;
- (void)pollingDidReceiveErrorResponse;

@end

@interface ORControllerPollingSender : NSObject <ControllerRequestDelegate, UpdateControllerDelegate> {
    NSString *ids;
    ControllerRequest *controllerRequest;
    
    NSObject <ORControllerPollingSenderDelegate> *delegate;
}

@property (nonatomic, assign) NSObject <ORControllerPollingSenderDelegate> *delegate;

- (id)initWithIds:(NSString *)someIds;
- (void)requestStatus;
- (void)poll;

- (void)cancel;

@end