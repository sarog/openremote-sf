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
#import "Definition.h"

@implementation Definition

@synthesize groups, screens, labels, tabBar, localLogic, imageNames;

- (void)dealloc
{
    [groups release];
    [screens release];
    [labels release];
    [imageNames release];
    [tabBar release];
    [localLogic release];
    [super dealloc];
}

- (id)init
{			
    self = [super init];
    if (self) {
        groups = [[NSMutableArray alloc] init];
		screens = [[NSMutableArray alloc] init];
		labels = [[NSMutableArray alloc] init];
		imageNames = [[NSMutableArray alloc] init];
	}
	return self;
}

- (Group *)findGroupById:(int)groupId {
	for (Group *g in groups) {
		if (g.groupId == groupId) {
			return g;			
		}
	}
	return nil;
}

- (Screen *)findScreenById:(int)screenId {
	for (Screen *tempScreen in self.screens) {
		if (tempScreen.screenId == screenId) {
			NSLog(@"find screen screenId %d", screenId);
			return tempScreen;
		}
	}
	return nil;
}

- (void)addGroup:(Group *)group {
	for (int i = 0; i < self.groups.count; i++) {
		Group *tempGroup = [self.groups objectAtIndex:i];
		if (tempGroup.groupId == group.groupId) {
			[self.groups replaceObjectAtIndex:i withObject:group];
			return;
		}
	}
	[self.groups addObject:group];
}

- (void)addScreen:(Screen *)screen {
	for (int i = 0; i < self.screens.count; i++) {
		Screen *tempScreen = [self.screens objectAtIndex:i];
		if (tempScreen.screenId == screen.screenId) {
			[self.screens replaceObjectAtIndex:i withObject:screen];
			return;
		}
	}
	[self.screens addObject:screen];
}

- (void) addLabel:(Label *)label {
	for (int i = 0; i < self.labels.count; i++) {
		Label *tempLabel = [self.labels objectAtIndex:i];
		if (tempLabel.componentId == label.componentId) {
			[self.labels replaceObjectAtIndex:i withObject:label];
			return;
		}
	}
	[self.labels addObject:label];
}

- (Label *)findLabelById:(int)labelId {
	for (Label *tempLabel in self.labels) {
		if (tempLabel.componentId == labelId) {
			return [tempLabel retain];
		}
	}
	return nil;
}

- (void)addImageName:(NSString *)imageName {
	for (NSString *name in imageNames) {
		// avoid duplicated
		if ([name isEqualToString:imageName]) {
			return;
		}
	}
	if (imageName) {
		[[self imageNames] addObject:imageName];	
	}
}

- (void)clearPanelXMLData {
	if (groups) {
		[groups removeAllObjects];
	}
	if (screens) {
		[screens removeAllObjects];
	}
	if (labels) {
		[labels removeAllObjects];
	}
	if (imageNames) {
		[imageNames removeAllObjects];
	}
	if (tabBar) {
		tabBar = nil;
	}
}

@end
