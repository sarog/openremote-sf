//
//  APISecurity.h
//  openremote
//
//  Created by Eric Bariaux on 24/05/12.
//  Copyright (c) 2012 OpenRemote, Inc. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface APISecurity : NSObject

- (id)initWithPath:(NSString *)aPath security:(NSString *)aSecurity sslEnabled:(BOOL)flag;

@property (nonatomic, copy, readonly) NSString *path;
@property (nonatomic, copy, readonly) NSString *security; // TODO: replace with enemuration
@property (nonatomic, assign, readonly) BOOL sslEnabled;

@end