/* OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2009, OpenRemote Inc.
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
#import "Group.h"
#import "Screen.h"
#import "Definition.h"

@implementation Group

@synthesize groupId, name, screens, tabBar;


#pragma mark Initializers

/**
 * Initialize according to the XML parser.
 */
- (id)initWithXMLParser:(NSXMLParser *)parser elementName:(NSString *)elementName attributes:(NSDictionary *)attributeDict parentDelegate:(NSObject<NSXMLParserDelegate> *)parent {
	if (self = [super init]) {
		groupId = [[attributeDict objectForKey:ID] intValue];					
		name = [[attributeDict objectForKey:NAME] copy];		
		screens = [[NSMutableArray alloc] init];
		
		xmlParserParentDelegate = [parent retain];
		[parser setDelegate:self];
	}
	
	return self;
}

// get element name, must be overriden in subclass
- (NSString *) elementName {
	return GROUP;
}

- (void)dealloc {
	[name release];
	[screens release];
	[tabBar release];
	[super dealloc];
}


#pragma mark Delegate methods of NSXMLParser      

/**
 * Parse the screen reference elements .
 */
- (void)parser:(NSXMLParser *)parser didStartElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qualifiedName attributes:(NSDictionary *)attributeDict{
	NSLog(@"start at screen ref");
	if ([elementName isEqualToString:INCLUDE] && [SCREEN isEqualToString:[attributeDict objectForKey:TYPE]]) {
		int screenRefId = [[attributeDict objectForKey:REF] intValue];
		Screen *existedScreen = [[Definition sharedDefinition] findScreenById:screenRefId];
		[self.screens addObject:existedScreen];
	} else if ([elementName isEqualToString:TABBAR]) {
		tabBar = [[TabBar alloc] initWithXMLParser:parser elementName:elementName attributes:attributeDict parentDelegate:self];
	}
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
