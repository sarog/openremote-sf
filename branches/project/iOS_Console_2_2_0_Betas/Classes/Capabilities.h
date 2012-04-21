//
//  Capabilities.h
//  openremote
//
//  Created by Eric Bariaux on 19/04/12.
//  Copyright (c) 2012 OpenRemote, Inc. All rights reserved.
//

#import <Foundation/Foundation.h>

#define DEFAULT_CONTROLLER_API_VERSION  @"2.0"

@interface Capabilities : NSObject

+ (NSArray *)iosConsoleSupportedVersions;

- (id)initWithSupportedVersions:(NSArray *)versions;

@property (nonatomic, copy, readonly) NSArray *supportedVersions;

@end