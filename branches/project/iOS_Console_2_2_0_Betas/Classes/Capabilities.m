//
//  Capabilities.m
//  openremote
//
//  Created by Eric Bariaux on 19/04/12.
//  Copyright (c) 2012 OpenRemote, Inc. All rights reserved.
//

#import "Capabilities.h"

@interface Capabilities ()

@property (nonatomic, copy, readwrite) NSArray *supportedVersions;

@end

static NSArray *iosConsoleSupportedVersions;

@implementation Capabilities

+ (void)initialize
{
    iosConsoleSupportedVersions = [[NSArray arrayWithObjects:[NSDecimalNumber decimalNumberWithString:@"2.1"], [NSDecimalNumber decimalNumberWithString:@"2.0"], nil] retain];
}

- (id)initWithSupportedVersions:(NSArray *)versions
{
    self = [super init];
    if (self) {
        self.supportedVersions = versions;
    }
    return self;
}

- (void)dealloc
{
    self.supportedVersions = nil;
    [super dealloc];
}

+ (NSArray *)iosConsoleSupportedVersions
{
    return iosConsoleSupportedVersions;
}

@synthesize supportedVersions;

@end