//
//  ORControllerStatusRequestSender.m
//  openremote
//
//  Created by Eric Bariaux on 21/05/12.
//  Copyright (c) 2012 OpenRemote, Inc. All rights reserved.
//

#import "ORControllerStatusRequestSender.h"
#import "ServerDefinition.h"

@interface ORControllerStatusRequestSender()

@property (nonatomic, retain) ORController *controller;
@property (nonatomic, retain) NSString *ids;
@property (nonatomic, retain) ControllerRequest *controllerRequest;

@end

@implementation ORControllerStatusRequestSender

- (void)send
{
    NSAssert(!controllerRequest, @"ORControllerPollingSender can only be used to send a request once");
    
    NSString *urlPath = [[ServerDefinition controllerStatusPathForController:self.controller] stringByAppendingFormat:@"/%@", ids];
    controllerRequest = [[ControllerRequest alloc] initWithController:self.controller];
    controllerRequest.delegate = self;
    [controllerRequest getRequestWithPath:urlPath];
}

@synthesize controller;
@synthesize ids;
@synthesize controllerRequest;

@end