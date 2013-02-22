//
//  NSURLHelper.m
//  openremote
//
//  Created by Eric Bariaux on 02/05/12.
//  Copyright (c) 2012 OpenRemote, Inc. All rights reserved.
//

#import "NSURLHelper.h"

@implementation NSURLHelper

/**
 * controller:8080/xyz -> http://controller:8080/xyz
 * controller/xyz -> http://controller/xyz
 * http://controller:8080/xyz -> http://controller:8080/xyz
 * https://controller:8080/xyz -> https://controller:8080/xyz
 * abc://controller:8080/xyz -> error
 */
+ (NSURL *)parseControllerURL:(NSString *)urlString
{
    NSString *url = [urlString stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceAndNewlineCharacterSet]];
    NSURL *nsUrl = [NSURL URLWithString:url];
    
    // TODO: localhost:8080/controller is refused because localhost is considered scheme in this case ???
    // TODO: have the URL validation is specific class, not linked to UI and have unit tests for it
    
    NSLog(@"scheme %@", [nsUrl scheme]);
    if (![nsUrl scheme]) {
        url = [NSString stringWithFormat:@"http://%@", url];
        nsUrl = [NSURL URLWithString:url];
    } else if (![[nsUrl scheme] isEqualToString:@"http"] && ![[nsUrl scheme] isEqualToString:@"https"]) {
        url = [NSString stringWithFormat:@"http://%@", url];
        nsUrl = [NSURL URLWithString:url];
    }
    if (!([nsUrl scheme] && ([[nsUrl scheme] isEqualToString:@"http"] || [[nsUrl scheme] isEqualToString:@"https"]))) {
        return nil;
    }
    NSLog(@"host %@", [nsUrl host]);
    if (!nsUrl || ![nsUrl host] || ![[nsUrl host] length]) {
        return nil;
    }
    return nsUrl;
}

@end
