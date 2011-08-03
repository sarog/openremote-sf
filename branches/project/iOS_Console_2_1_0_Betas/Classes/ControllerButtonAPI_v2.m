//
//  ControllerButtonAPI_v2.m
//  openremote
//
//  Created by Eric Bariaux on 03/08/11.
//  Copyright 2011 OpenRemote, Inc. All rights reserved.
//

#import "ControllerButtonAPI_v2.h"

@implementation ControllerButtonAPI_v2

- (void)sendPressCommand:(ButtonView *)sender {
	[sender sendCommandRequest:@"click"];
}

- (void)sendShortReleaseCommand:(ButtonView *)sender {
    // TODO: log not supported
}

- (void)sendLongPressCommand:(ButtonView *)sender {
    // TODO: log not supported
}

- (void)sendLongReleaseCommand:(ButtonView *)sender {
    // TODO: log not supported
}

@end
