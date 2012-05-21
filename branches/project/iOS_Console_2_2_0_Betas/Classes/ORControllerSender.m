//
//  ORControllerSender.m
//  openremote
//
//  Created by Eric Bariaux on 21/05/12.
//  Copyright (c) 2012 OpenRemote, Inc. All rights reserved.
//

#import "ORControllerSender.h"
#import "ControllerRequest.h"

@interface ORControllerSender()

@property (nonatomic, retain) ControllerRequest *controllerRequest;

@end

@implementation ORControllerSender

- (void)dealloc
{
    self.controllerRequest = nil;
    [super dealloc];
}

- (void)cancel
{
    [self.controllerRequest cancel];
}

- (void)send
{
    // Don't do anything in this class, subclasses implement as appropriate
}

@synthesize controllerRequest;

@end