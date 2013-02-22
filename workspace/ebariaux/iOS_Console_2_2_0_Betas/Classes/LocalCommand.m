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
#import "LocalCommand.h"

@interface LocalCommand ()

@property (nonatomic, copy, readwrite) NSString *protocol;
@property (nonatomic, retain, readwrite) NSMutableDictionary *properties;

@end

@implementation LocalCommand

- (id)initWithId:(int)anId protocol:(NSString *)aProtocol
{
    self = [super init];
    if (self) {
        self.componentId = anId;
        self.protocol = aProtocol;
        self.properties = [NSMutableDictionary dictionary];
    }
    return self;
}

- (void)dealloc
{
    self.protocol = nil;
    self.properties = nil;
	[super dealloc];
}

- (void)addPropertyValue:(NSString *)value forKey:(NSString *)key
{
    [self.properties setValue:value forKey:key];
}

- (NSString *)propertyValueForKey:(NSString *)key
{
    return [self.properties objectForKey:key];
}

@synthesize protocol;
@synthesize properties;

@end