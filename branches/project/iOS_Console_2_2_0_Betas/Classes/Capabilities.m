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
@property (nonatomic, copy, readwrite) NSArray *apiSecurities;
@property (nonatomic, copy, readwrite) NSArray *capabilities;

@end

static NSArray *iosConsoleSupportedVersions;

@implementation Capabilities

+ (void)initialize
{
    iosConsoleSupportedVersions = [[NSArray arrayWithObjects:[NSDecimalNumber decimalNumberWithString:@"2.1"], [NSDecimalNumber decimalNumberWithString:@"2.0"], nil] retain];
}

- (id)initWithSupportedVersions:(NSArray *)versions apiSecurities:(NSArray *)securities capabilities:(NSArray *)someCapabilities
{
    self = [super init];
    if (self) {
        self.supportedVersions = versions;
        self.apiSecurities = securities;
        self.capabilities = someCapabilities;
    }
    return self;
}

- (void)dealloc
{
    self.supportedVersions = nil;
    self.apiSecurities = nil;
    self.capabilities = nil;
    [super dealloc];
}

+ (NSArray *)iosConsoleSupportedVersions
{
    return iosConsoleSupportedVersions;
}

@synthesize supportedVersions;
@synthesize apiSecurities;
@synthesize capabilities;

@end