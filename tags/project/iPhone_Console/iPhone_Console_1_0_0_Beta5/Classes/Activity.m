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


#import "Activity.h"
#import "Screen.h"

@implementation Activity

@synthesize activityId,name, icon, screens;

#pragma mark Initializers

/**
 * Initialize according to the XML parser.
 */
- (id)initWithXMLParser:(NSXMLParser *)parser elementName:(NSString *)elementName attributes:(NSDictionary *)attributeDict parentDelegate:(NSObject *)parent {
	if (self == [super init]) {
		activityId = [[attributeDict objectForKey:@"id"] intValue];
		name = [[attributeDict objectForKey:@"name"] copy];
		icon = [[attributeDict objectForKey:@"icon"] copy]; 
		
		screens = [[NSMutableArray alloc] init];
		
		xmlParserParentDelegate = [parent retain];
		[parser setDelegate:self];
	}
	return self;
}

- (void)dealloc {
	[name release];
	[icon release];
	[screens release];
	
	[super dealloc];
}

#pragma mark Delegate methods of NSXMLParser

/**
 * Parse the screen elements by creating Screen instances for them.
 */
- (void)parser:(NSXMLParser *)parser didStartElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qualifiedName attributes:(NSDictionary *)attributeDict{
	if ([elementName isEqualToString:@"screen"]) {
		NSLog(@"start parse Screen");
		Screen *screen = [[Screen alloc] initWithXMLParser:parser elementName:elementName attributes:attributeDict parentDelegate:self];
		[screens addObject:screen];
		[screen release];
	}
}

/**
 * When we find an activity end element, restore the original XML parser delegate.
 */
- (void)parser:(NSXMLParser *)parser didEndElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qName {
	if ([elementName isEqualToString:@"activity"]) {
		NSLog(@"end parse Activity");
 		[parser setDelegate:xmlParserParentDelegate];
		[xmlParserParentDelegate release];
		xmlParserParentDelegate = nil;
	}
}

@end
