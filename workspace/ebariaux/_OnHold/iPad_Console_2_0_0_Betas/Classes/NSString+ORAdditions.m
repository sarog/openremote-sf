//
//  NSString+ORAdditions.m
//  openremote
//
//  Created by Eric Bariaux on 12/04/11.
//  Copyright 2011 OpenRemote, Inc. All rights reserved.
//

#import "NSString+ORAdditions.h"
#include <arpa/inet.h>

@implementation NSString (NSString_ORAdditions)

- (BOOL)isValidIPAddress {
    struct in_addr pin;
    return (inet_aton([self UTF8String], &pin) == 1);
}

- (NSString *)hostOfURL {
    return [[NSURL URLWithString:self] host];
}

- (NSString *)portAsStringOfURL {
    return [[[NSURL URLWithString:self] port] stringValue];
}

@end
