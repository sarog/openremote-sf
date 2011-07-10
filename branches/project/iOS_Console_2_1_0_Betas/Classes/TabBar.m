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
#import "TabBar.h"
#import "TabBarItem.h"

@implementation TabBar

@synthesize tabBarItems;

// This method is abstract method of XMLEntity, must be overriden in it's subclass.
- (NSString *) elementName {
	return TABBAR;
}

#pragma mark Delegate methods of NSXMLParser
/**
 * Initialize according to the XML parser.
 */
- (id)initWithXMLParser:(NSXMLParser *)parser elementName:(NSString *)elementName attributes:(NSDictionary *)attributeDict parentDelegate:(NSObject<NSXMLParserDelegate> *)parent {
	if (self = [super init]) {
		tabBarItems = [[NSMutableArray alloc] init];
		xmlParserParentDelegate = [parent retain];
		[parser setDelegate:self];
	}
	
	return self;
}    

/**
 * Parse the tabbaritem reference elements .
 */
- (void)parser:(NSXMLParser *)parser didStartElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qualifiedName attributes:(NSDictionary *)attributeDict{	
	if ([elementName isEqualToString:ITEM]) {
		NSLog(@"start item in tabbar");		
		TabBarItem *tabBarItem = [[TabBarItem alloc] initWithXMLParser:parser elementName:elementName attributes:attributeDict parentDelegate:self];
		NSLog(@"find item %@", tabBarItem.tabBarItemName);
		[tabBarItems addObject:tabBarItem];
		NSLog(@"end item in tabbar");
        [tabBarItem release];
	}
}

- (void)dealloc {
	[tabBarItems release];
	[super dealloc];
}

@end
