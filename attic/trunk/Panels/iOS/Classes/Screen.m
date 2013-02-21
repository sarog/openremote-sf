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
#import "Screen.h"
#import "Control.h"
#import "AbsoluteLayoutContainer.h"
#import "GridLayoutContainer.h"
#import "Definition.h"
#import "Gesture.h"

@implementation Screen
 
@synthesize screenId,name,background,layouts,gestures,landscape,inverseScreenId;

#pragma mark constructor
//Initialize itself accoding to xml parser
- (id)initWithXMLParser:(NSXMLParser *)parser elementName:(NSString *)elementName attributes:(NSDictionary *)attributeDict parentDelegate:(NSObject<NSXMLParserDelegate> *)parent {
	if (self = [super init]) {
		screenId = [[attributeDict objectForKey:ID] intValue];
		name = [[attributeDict objectForKey:NAME] copy];
		layouts = [[NSMutableArray alloc] init];
		gestures = [[NSMutableArray alloc] init];
		
		landscape = [@"TRUE" isEqualToString:[[attributeDict objectForKey:LANDSCAPE] uppercaseString]] ? YES : NO;
		inverseScreenId = [[attributeDict objectForKey:INVERSE_SCREEN_ID] intValue];		
		
		xmlParserParentDelegate = [parent retain];
		[parser setDelegate:self];
	}
	NSLog(@"screen %@",[attributeDict objectForKey:NAME]);
	return self;
}

// get element name, must be overriden in subclass
- (NSString *) elementName {
	return SCREEN;
}

- (NSArray *)pollingComponentsIds {
	NSMutableArray *ids = [[[NSMutableArray alloc] init] autorelease];
	for (LayoutContainer *layout in layouts) {		
		[ids addObjectsFromArray:[layout pollingComponentsIds]];
	}
	return ids;
}

- (int)screenIdForOrientation:(UIInterfaceOrientation)orientation {
    if (inverseScreenId == 0) {
        return screenId;
    }
    return (self.landscape == UIInterfaceOrientationIsLandscape(orientation))?screenId:inverseScreenId;
}

#pragma mark deleget method of NSXMLParser

//Parse sub element in screen 
- (void)parser:(NSXMLParser *)parser didStartElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qualifiedName attributes:(NSDictionary *)attributeDict{

	if ([elementName isEqualToString:ABSOLUTE]) {
		// Call AbsoluteLayoutContainer's initialize method to parse xml using NSXMLParser
		AbsoluteLayoutContainer *absolute = [[AbsoluteLayoutContainer alloc] initWithXMLParser:parser elementName:elementName attributes:attributeDict parentDelegate:self];
		[layouts addObject:absolute];
		[absolute release];
	} else if ([elementName isEqualToString:GRID]) {
		// Call GridLayoutContainer's initialize method to parse xml using NSXMLParser
		GridLayoutContainer *grid = [[GridLayoutContainer alloc] initWithXMLParser:parser elementName:elementName attributes:attributeDict parentDelegate:self];
		[layouts addObject:grid];
		[grid release];
	} else if ([elementName isEqualToString:GESTURE]) {
		NSLog(@"start gesture");
		// Call Gesture's initialize method to parse xml using NSXMLParser
		Gesture *gesture = [[Gesture alloc] initWithXMLParser:parser elementName:elementName attributes:attributeDict parentDelegate:self];
		[gestures addObject:gesture];
		[gesture release];
	} else if ([elementName isEqualToString:BACKGROUND]) {
		NSLog(@"start background in screen");
		// Call Background's initialize method to parse xml using NSXMLParser
		background = [[Background alloc] initWithXMLParser:parser elementName:elementName attributes:attributeDict parentDelegate:self];
		NSLog(@"end background in screen");
	}
}

- (Gesture *)getGestureIdByGestureSwipeType:(GestureSwipeType)type {
	for (Gesture *g in gestures) {
		if (g.swipeType == type) {
			return g;
		}
	}
	return nil;
}

- (void)dealloc {
	[name release];
	[background release];
	[layouts release];
	[gestures release];
	[super dealloc];
}

@end
