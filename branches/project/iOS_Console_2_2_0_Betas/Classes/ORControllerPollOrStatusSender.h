//
//  ORControllerPollOrStatusSender.h
//  openremote
//
//  Created by Eric Bariaux on 21/05/12.
//  Copyright (c) 2012 OpenRemote, Inc. All rights reserved.
//

#import "ORControllerSender.h"
#import "ControllerRequest.h"

@class ORController;

@protocol ORControllerPollingSenderDelegate <NSObject>

// Note that this applies to both polling and status request, even if the name would indicate otherwise
- (void)pollingDidFailWithError:(NSError *)error;
- (void)pollingDidSucceed;
- (void)pollingDidTimeout;
- (void)pollingDidReceiveErrorResponse;

- (void)controllerConfigurationUpdated:(ORController *)aController;

@end

@interface ORControllerPollOrStatusSender : ORControllerSender <ControllerRequestDelegate>

@property (nonatomic, assign) NSObject <ORControllerPollingSenderDelegate> *delegate;

- (id)initWithController:(ORController *)aController ids:(NSString *)someIds;

@end