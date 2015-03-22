//
//  NSString+ORAdditions.h
//  openremote
//
//  Created by Eric Bariaux on 12/04/11.
//  Copyright 2011 OpenRemote, Inc. All rights reserved.
//

#import <Foundation/Foundation.h>


@interface NSString (NSString_ORAdditions)

/**
 * Checks whether or not the receiver string contains a valid IP address.
 */
- (BOOL)isValidIPAddress;

/**
 * Considering the receiver represents a valid URL, returns the the host part of it.
 * Returns nil if the receiver does not represent a valid URL.
 */
- (NSString *)hostOfURL;

/**
 * Considering the receiver represents a valid URL, returns the the port part of it, as a string.
 * Returns nil if the receiver does not represent a valid URL.
 */
- (NSString *)portAsStringOfURL;

@end
