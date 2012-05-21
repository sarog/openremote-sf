//
//  ORControllerSender.h
//  openremote
//
//  Created by Eric Bariaux on 21/05/12.
//  Copyright (c) 2012 OpenRemote, Inc. All rights reserved.
//

#import <Foundation/Foundation.h>

/**
 * Parent class for all "commands" towards the controller
 */
@interface ORControllerSender : NSObject

- (void)send;
- (void)cancel;

@end