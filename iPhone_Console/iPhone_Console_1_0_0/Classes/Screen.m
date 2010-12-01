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


@implementation Screen
 
@synthesize name,icon,controls,rows,cols;

#pragma mark constructor
//Initialize itself accoding to xml parser
- (id)initWithXMLParser:(NSXMLParser *)parser elementName:(NSString *)elementName attributes:(NSDictionary *)attributeDict parentDelegate:(NSObject *)parent {
	if (self = [super init]) {
		
		name = [[attributeDict objectForKey:@"name"] copy];
		icon = [[attributeDict objectForKey:@"icon"] copy]; 
		rows = [[attributeDict objectForKey:@"row"] intValue];
		cols = [[attributeDict objectForKey:@"col"] intValue];
		
		controls = [[NSMutableArray alloc] init];
		
		xmlParserParentDelegate = [parent retain];
		[parser setDelegate:self];
	}
	return self;
}

#pragma mark deleget method of NSXMLParser
//end the screen parse set deleget back to parent
- (void)parser:(NSXMLParser *)parser didEndElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qName {
	if ([elementName isEqualToString:@"screen"]) {
		// set back the delegate to original one. In order to  parse "screens" element
 		[parser setDelegate:xmlParserParentDelegate];
		[xmlParserParentDelegate release];
		xmlParserParentDelegate = nil;
	}
}

//Parse control element and add it in to controls 
- (void)parser:(NSXMLParser *)parser didStartElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qualifiedName attributes:(NSDictionary *)attributeDict{
	if ([elementName isEqualToString:@"button"]) {
		// Call Control's initialize method to parse xml using NSXMLParser
		Control *control = [[Control alloc] initWithXMLParser:parser elementName:elementName attributes:attributeDict parentDelegate:self];
		[controls addObject:control];
		[control release];
	}
}


- (void)dealloc {
	[name release];
	[icon release];
	[controls release];
	
	[super dealloc];
}
@end
