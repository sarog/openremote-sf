//
//  NSURL+ORAdditions.m
//  openremote
//
//  Created by Eric Bariaux on 13/07/11.
//  Copyright 2011 OpenRemote, Inc. All rights reserved.
//

#import "NSURLRequest+ORAdditions.h"
#import "CredentialUtil.h"

@implementation NSURLRequest (NSURLRequest_ORAdditions)

+ (NSURLRequest *)or_requestWithURLString:(NSString *)location method:(NSString *)method userName:(NSString *)userName password:(NSString *)password
{   
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] init];
    
    NSURL *url = [[NSURL alloc] initWithString:location];
    [request setURL:url];
    [url release];
    [request setHTTPMethod:method];
    
    [CredentialUtil addCredentialToNSMutableURLRequest:request withUserName:userName password:password];

    return [request autorelease];
}

@end
