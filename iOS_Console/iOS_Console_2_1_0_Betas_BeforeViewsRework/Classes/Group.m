/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2011, OpenRemote Inc.
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
#import "Group.h"
#import "Screen.h"
#import "Definition.h"

@implementation Group

@synthesize groupId, name, screens, tabBar;


- (id)initWithGroupId:(int)anId name:(NSString *)aName
{
    self = [super init];
    if (self) {
        groupId = anId;
        name = [aName copy];
		screens = [[NSMutableArray alloc] init];
    }
    return self;
}

- (void)dealloc {
	[name release];
	[screens release];
	[tabBar release];
	[super dealloc];
}



// Get all portrait screens in group.
- (NSArray *) getPortraitScreens {
	return [screens filteredArrayUsingPredicate:[NSPredicate predicateWithFormat: @"landscape == %d", NO]]; 
}

// Get all landscape screens in group.
- (NSArray *) getLandscapeScreens {
	return [screens filteredArrayUsingPredicate:[NSPredicate predicateWithFormat: @"landscape == %d", YES]]; 
}

// Find screen model in specified orientation screens of group containing by screen id.
- (BOOL)canFindScreenById:(int)screenId inOrientation:(BOOL)isLandscape {
	return [screens filteredArrayUsingPredicate:[NSPredicate predicateWithFormat: @"landscape == %d && screenId == %d", isLandscape, screenId]].count > 0; 
}


- (Screen *) findScreenByScreenId:(int)screenId {
	NSArray *ss = [screens filteredArrayUsingPredicate:[NSPredicate predicateWithFormat: @"screenId == %d", screenId]];
	if (ss.count > 0) {
		Screen *screen = [ss objectAtIndex:0];
		return screen;
	}
	return nil;
}


@end
