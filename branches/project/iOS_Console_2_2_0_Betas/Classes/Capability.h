//
//  Capability.h
//  openremote
//
//  Created by Eric Bariaux on 24/05/12.
//  Copyright (c) 2012 OpenRemote, Inc. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface Capability : NSObject

- (id)initWithName:(NSString *)aName properties:(NSDictionary *)someProperties;

@property (nonatomic, copy, readonly) NSString *name;
@property (nonatomic, copy, readonly) NSDictionary *properties;

@end