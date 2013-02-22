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
#import "APISecurity.h"

@interface APISecurity ()

@property (nonatomic, copy, readwrite) NSString *path;
@property (nonatomic, assign, readwrite) SecurityType security;
@property (nonatomic, assign, readwrite) BOOL sslEnabled;

@end

@implementation APISecurity

- (id)initWithPath:(NSString *)aPath security:(SecurityType)aSecurity sslEnabled:(BOOL)flag
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
    [super dealloc];
}

+ (NSString *)securityTypeStringFromEnum:(SecurityType)securityType
{
    switch (securityType) {
        case None:
            return @"None";
        case HTTPBasic:
            return @"HTTP-basic";
    }
    return nil;
}

+ (SecurityType)securityTypeFromString:(NSString *)securityTypeString
{
    if ([@"HTTP-basic" isEqualToString:securityTypeString]) {
        return HTTPBasic;
    }
    return None;
}

- (NSString *)description
{
    NSMutableString *desc = [NSMutableString stringWithFormat:@"%@ : %@", self.path, [[self class] securityTypeStringFromEnum:self.security]];
    if (sslEnabled) {
        [desc appendString:@" [SSL]"];
    }
    return [NSString stringWithString:desc];
}

@synthesize path;
@synthesize security;
@synthesize sslEnabled;

@end