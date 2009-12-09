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

#import "Button.h"


@implementation Button

@synthesize image, imagePressed, repeat, hasCommand, name, navigate;

- (id)initWithXMLParser:(NSXMLParser *)parser elementName:(NSString *)elementName attributes:(NSDictionary *)attributeDict parentDelegate:(NSObject *)parent {
	if (self = [super init]) {		
		controlId = [[attributeDict objectForKey:@"id"] intValue];
		name = [[attributeDict objectForKey:@"name"] copy];
		hasCommand = [@"TRUE" isEqualToString:[[attributeDict objectForKey:@"hasControlCommand"] uppercaseString]] ? YES : NO;
		repeat = NO;
		if ([[attributeDict objectForKey:@"repeat"] isEqualToString:@"true"]) {
			repeat = YES;
		}
		
		xmlParserParentDelegate = [parent retain];
		[parser setDelegate:self];
	}
	return self;
}

// parse image, pressed image, command, navigate.
- (void)parser:(NSXMLParser *)parser didStartElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qualifiedName attributes:(NSDictionary *)attributeDict{
	if ([elementName isEqualToString:@"image"]) {
		Image *img = [[Image alloc] initWithXMLParser:parser elementName:elementName attributes:attributeDict parentDelegate:self];
		if ([[attributeDict objectForKey:@"state"] isEqualToString:@"onPress"]) {
			imagePressed = img;
		} else {
			image = img;
		}

	} else if ([elementName isEqualToString:@"navigate"]) {
		navigate = [[Navigate alloc] initWithXMLParser:parser elementName:elementName attributes:attributeDict parentDelegate:self];
	} 
	
}


// get element name, must be overriden in subclass
- (NSString *) elementName {
	return @"button";
}


// don't do polling to button
- (BOOL)hasPollingStatus {
	return NO;
}



- (void)dealloc {
	[image release];
	[imagePressed release];
	[navigate release];
	[name	 release];
	
	[super dealloc];
}

@end
