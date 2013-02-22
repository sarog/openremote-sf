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

/**
 * Returns the versions that are defined in this Capabilities instance and supported by the iOSConsole, in descending order.
 */
- (NSArray *)matchingVersions
{
    
    NSMutableSet *versions = [NSMutableSet set];
    for (NSString *version in self.supportedVersions) {
        [versions addObject:[NSDecimalNumber decimalNumberWithString:version]];
    }
    
    [versions intersectSet:[NSSet setWithArray:[[self class] iosConsoleSupportedVersions]]];

    NSArray *sortedNumberVersions = [[versions allObjects] sortedArrayUsingDescriptors:[NSArray arrayWithObject:[NSSortDescriptor sortDescriptorWithKey:@"" ascending:NO]]];
     
    NSNumberFormatter *f = [[NSNumberFormatter alloc] init];
    [f setLocale:[[[NSLocale alloc] initWithLocaleIdentifier:@"en_US"] autorelease]];
    f.minimumFractionDigits = 1; // Ensures 2 is converted to "2.0" string
    NSMutableArray *sortedStringVersions = [NSMutableArray arrayWithCapacity:[sortedNumberVersions count]];
    for (NSDecimalNumber *versionNumber in sortedNumberVersions) {
        [sortedStringVersions addObject:[f stringFromNumber:versionNumber]];
    }
    [f release];
    
    return [NSArray arrayWithArray:sortedStringVersions];
}

+ (NSArray *)iosConsoleSupportedVersions
{
    return iosConsoleSupportedVersions;
}

@synthesize supportedVersions;
@synthesize apiSecurities;
@synthesize capabilities;

@end