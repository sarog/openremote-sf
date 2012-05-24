//
//  APISecurity.m
//  openremote
//
//  Created by Eric Bariaux on 24/05/12.
//  Copyright (c) 2012 OpenRemote, Inc. All rights reserved.
//

#import "APISecurity.h"

@interface APISecurity ()

@property (nonatomic, copy, readwrite) NSString *path;
@property (nonatomic, copy, readwrite) NSString *security; // TODO: replace with enemuration
@property (nonatomic, assign, readwrite) BOOL sslEnabled;

@end

@implementation APISecurity

- (id)initWithPath:(NSString *)aPath security:(NSString *)aSecurity sslEnabled:(BOOL)flag
{
    self = [super init];
    if (self) {
        self.path = aPath;
        self.security = aSecurity;
        self.sslEnabled = flag;
    }
    return self;
}

- (void)dealloc
{
    self.path = nil;
    self.security = nil;
    [super dealloc];
}

@synthesize path;
@synthesize security;
@synthesize sslEnabled;

@end