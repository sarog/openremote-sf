/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2012, OpenRemote Inc.
 *
 * See the contributors.txt file in the distribution for a
 * full listing of individual contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
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