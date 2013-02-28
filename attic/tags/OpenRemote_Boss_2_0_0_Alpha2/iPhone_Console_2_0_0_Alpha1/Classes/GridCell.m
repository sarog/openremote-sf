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

#import "GridCell.h"


@implementation GridCell

@synthesize x,y,rowspan,colspan,component;

- (id)initWithXMLParser:(NSXMLParser *)parser elementName:(NSString *)elementName attributes:(NSDictionary *)attributeDict parentDelegate:(NSObject *)parent {
	if (self = [super init]) {		
		x = [[attributeDict objectForKey:@"x"] intValue];		
		y = [[attributeDict objectForKey:@"y"] intValue];
		
		// rowspan default value is 1
		int thatRowspan = [[attributeDict objectForKey:@"rowspan"] intValue];
		rowspan = thatRowspan < 1 ? 1 : thatRowspan;
		
		// colspan default value is 1
		int thatColspan = [[attributeDict objectForKey:@"colspan"] intValue];
		colspan = thatColspan < 1 ? 1 : thatColspan;
		
		xmlParserParentDelegate = [parent retain];
		[parser setDelegate:self];
	}
	NSLog(@"cell");
	return self;
}

// parse all kinds of controls
- (void)parser:(NSXMLParser *)parser didStartElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qualifiedName attributes:(NSDictionary *)attributeDict{
	
	component = [Component buildWithXMLParser:elementName parser:parser elementName:elementName attributes:attributeDict parentDelegate:self];
	
}


// get element name, must be overriden in subclass
- (NSString *) elementName {
	return @"cell";
}

- (void)dealloc {
	[component release];
	[super dealloc];
}

@end
