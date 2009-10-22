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


#import "Screen.h"
#import "Control.h"
#import "AbsoluteLayoutContainer.h"
#import "GridLayoutContainer.h"


@implementation Screen
 
@synthesize screenId,name,background,layouts,gestures;

#pragma mark constructor
//Initialize itself accoding to xml parser
- (id)initWithXMLParser:(NSXMLParser *)parser elementName:(NSString *)elementName attributes:(NSDictionary *)attributeDict parentDelegate:(NSObject *)parent {
	if (self = [super init]) {
		screenId = [[attributeDict objectForKey:@"id"] intValue];
		name = [[attributeDict objectForKey:@"name"] copy];
		background = [[attributeDict objectForKey:@"background"] copy]; 
		
		layouts = [[NSMutableArray alloc] init];
		gestures = [[NSMutableArray alloc] init];
		
		xmlParserParentDelegate = [parent retain];
		[parser setDelegate:self];
	}
	NSLog(@"screen %@",[attributeDict objectForKey:@"name"]);
	return self;
}

// get element name, must be overriden in subclass
- (NSString *) elementName {
	return @"screen";
}

#pragma mark deleget method of NSXMLParser

//Parse sub element in screen 
- (void)parser:(NSXMLParser *)parser didStartElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qualifiedName attributes:(NSDictionary *)attributeDict{

	if ([elementName isEqualToString:@"absolute"]) {
		// Call AbsoluteLayoutContainer's initialize method to parse xml using NSXMLParser
		AbsoluteLayoutContainer *absolute = [[AbsoluteLayoutContainer alloc] initWithXMLParser:parser elementName:elementName attributes:attributeDict parentDelegate:self];
		[layouts addObject:absolute];
		[absolute release];
//	} else if ([elementName isEqualToString:@"grid"]) {
//		// Call GridLayoutContainer's initialize method to parse xml using NSXMLParser
//		GridLayoutContainer *grid = [[GridLayoutContainer alloc] initWithXMLParser:parser elementName:elementName attributes:attributeDict parentDelegate:self];
//		[layouts addObject:grid];
//		[grid release];
//	} else if ([elementName isEqualToString:@"gesture"]) {
//		// Call Gesture's initialize method to parse xml using NSXMLParser
	}
}



- (void)dealloc {
	[name release];
	[background release];
	[layouts release];
	[gestures release];
	[super dealloc];
}

@end
