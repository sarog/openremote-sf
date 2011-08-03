//
//  ControllerButtonAPI_v2_1.m
//  openremote
//
//  Created by Eric Bariaux on 03/08/11.
//  Copyright 2011 OpenRemote, Inc. All rights reserved.
//

#import "ControllerButtonAPI_v2_1.h"

@implementation ControllerButtonAPI_v2_1

- (void)sendPressCommand:(ButtonView *)sender {
	[sender sendCommandRequest:@"press"];
}

- (void)sendShortReleaseCommand:(ButtonView *)sender {
    [sender sendCommandRequest:@"shortRelease"];
}

- (void)sendLongPressCommand:(ButtonView *)sender {
    [sender sendCommandRequest:@"longPress"];
}

- (void)sendLongReleaseCommand:(ButtonView *)sender {
    [sender sendCommandRequest:@"longRelease"];
}

@end
