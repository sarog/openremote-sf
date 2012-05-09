//
//  ClientSideBeanManager.m
//  openremote
//
//  Created by Eric Bariaux on 09/05/12.
//  Copyright (c) 2012 OpenRemote, Inc. All rights reserved.
//

#import "ClientSideBeanManager.h"
#import "ClientSideRuntime.h"

@interface ClientSideBeanManager()

@property (nonatomic, retain) ClientSideRuntime *clientSideRuntime;
@property (nonatomic, retain) NSMutableDictionary *classRegistry;
@property (nonatomic, retain) NSMutableDictionary *beanRegistry;

@end

@implementation ClientSideBeanManager

- (id)initWithRuntime:(ClientSideRuntime *)runtime
{
    self = [super init];
    if (self) {
        self.clientSideRuntime = runtime;
        self.classRegistry = [NSMutableDictionary dictionary];
        self.beanRegistry = [NSMutableDictionary dictionary];
    }
    return self;
}

- (void)dealloc
{
    self.clientSideRuntime = nil;
    self.classRegistry = nil;
    self.beanRegistry = nil;
    [super dealloc];
}

- (void)loadRegistrationFromPropertyFile:(NSString *)propertyFilePath
{
    NSDictionary *propertyFileContent = [NSDictionary dictionaryWithContentsOfFile:propertyFilePath];
    if (propertyFilePath) {
        for (NSString *key in [propertyFileContent allKeys]) {
            Class clazz = NSClassFromString([propertyFileContent objectForKey:key]);
            if (clazz) {
                [self registerClass:clazz forKey:key];
            }
            // TODO: else log            
        }
    }
}

- (void)registerClass:(Class)aClass forKey:(NSString *)key
{
    [self.classRegistry setObject:aClass forKey:key];
}

- (id)beanForKey:(NSString *)key
{
    id bean = [self.beanRegistry objectForKey:key];
    if (bean) {
        return bean;
    }
    Class clazz = [self.classRegistry objectForKey:key];
    if (!clazz) {
        return nil;
    }
    bean = [[clazz alloc] initWithRuntime:self.clientSideRuntime];
    if (bean) {
        [self.beanRegistry setObject:bean forKey:key];
        [bean release];
    }
    return bean;
}

- (void)forgetBeanForKey:(NSString *)key
{
    [self.beanRegistry removeObjectForKey:key];
}

@synthesize clientSideRuntime;
@synthesize classRegistry;
@synthesize beanRegistry;

@end