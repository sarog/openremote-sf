//
//  NSURL+ORAdditions.h
//  openremote
//
//  Created by Eric Bariaux on 13/07/11.
//  Copyright 2011 OpenRemote, Inc. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface NSURLRequest (NSURLRequest_ORAdditions)

+ (NSURLRequest *)or_requestWithURLString:(NSString *)location method:(NSString *)method userName:(NSString *)userName password:(NSString *)password;

@end
