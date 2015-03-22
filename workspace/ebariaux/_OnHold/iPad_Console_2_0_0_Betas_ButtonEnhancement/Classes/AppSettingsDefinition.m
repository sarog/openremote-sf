/* OpenRemote, the Home of the Digital Home.
 *  * Copyright 2008-2011, OpenRemote Inc-2009, OpenRemote Inc.
 * 
 * See the contributors.txt file in the distribution for a
 * full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3.0 of
 * the License, or (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

#import "AppSettingsDefinition.h"
#import "DirectoryDefinition.h"
#import "Definition.h"
#import "ViewHelper.h"
#import "NotificationConstant.h"
#import "CheckNetwork.h"
#import "CheckNetworkException.h"

#import "ORConsoleSettingsManager.h"
#import "ORConsoleSettings.h"
#import "ORController.h"

static AppSettingsDefinition *sharedAppSettingsDefinition = nil;

@implementation AppSettingsDefinition

+ (AppSettingsDefinition *)sharedAppSettingsDefinition
{
    if (sharedAppSettingsDefinition == nil) {
        sharedAppSettingsDefinition = [[super allocWithZone:NULL] init];
    }
    return sharedAppSettingsDefinition;
}

+ (id)allocWithZone:(NSZone *)zone
{
    return [[self sharedAppSettingsDefinition] retain];
}

- (id)copyWithZone:(NSZone *)zone
{
    return self;
}

- (id)retain
{
    return self;
}

- (NSUInteger)retainCount
{
    return NSUIntegerMax;  //denotes an object that cannot be released
}

- (void)release
{
    //do nothing
}

- (id)autorelease
{
    return self;
}

- (void)dealloc
{
    [super dealloc];
}

- (NSArray *)settingsDefinition
{
    if (!settingsDefinition) {
        settingsDefinition = [[NSArray alloc] initWithContentsOfFile:[DirectoryDefinition settingsDefinitionFilePath]];
    }
    return settingsDefinition;
}

// Get the specified section with index from appSettings.plist .
- (NSDictionary *)getSectionWithIndex:(int)index {
	return [self.settingsDefinition objectAtIndex:index];
}

// Get the specified section's header with index from appSettings.plist .
- (NSString *)getSectionHeaderWithIndex:(int)index{
	return [[self.settingsDefinition objectAtIndex:index] valueForKey:@"header"];
}

// Get the specified section's footer with index from appSettings.plist .
- (NSString *)getSectionFooterWithIndex:(int)index{
	return [[self.settingsDefinition objectAtIndex:index] valueForKey:@"footer"];
}

// Get the map value of auto discovery boolean value.
- (NSDictionary *)getAutoDiscoveryDic {
	return (NSDictionary *)[[self getSectionWithIndex:AUTO_DISCOVERY_SWITCH_INDEX] objectForKey:@"item"];
}

@end
