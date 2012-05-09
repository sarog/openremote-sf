//
//  ClientSideBeanManager.h
//  openremote
//
//  Created by Eric Bariaux on 09/05/12.
//  Copyright (c) 2012 OpenRemote, Inc. All rights reserved.
//

#import <Foundation/Foundation.h>

@class ClientSideRuntime;

/**
 * Simple utility class that can instantiate beans and keep track of them.
 */
@interface ClientSideBeanManager : NSObject

- (id)initWithRuntime:(ClientSideRuntime *)runtime;

- (void)loadRegistrationFromPropertyFile:(NSString *)propertyFilePath;
- (void)registerClass:(Class)aClass forKey:(NSString *)key;

- (id)beanForKey:(NSString *)key;
- (void)forgetBeanForKey:(NSString *)key;

@end
