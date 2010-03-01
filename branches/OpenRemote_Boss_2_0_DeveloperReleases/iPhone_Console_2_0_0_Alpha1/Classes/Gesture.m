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


#import "Gesture.h"


@implementation Gesture

@synthesize swipeType, hasControlCommand, navigate;

// This method is abstract method of indirectclass XMLEntity.
// So, this method must be overridden in subclass.
- (NSString *) elementName {
	return @"gesture";
}

- (id)initWithGestureSwipeType:(GestureSwipeType)type {
	if (self = [super init]) {
		swipeType = type;
	}
	return self;
}

#pragma mark Delegate methods of NSXMLParser

- (id)initWithXMLParser:(NSXMLParser *)parser elementName:(NSString *)elementName attributes:(NSDictionary *)attributeDict parentDelegate:(NSObject *)parent {
	if (self = [super init]) {
		componentId = [[attributeDict objectForKey:@"id"] intValue];
		NSString *type = [attributeDict objectForKey:@"type"];
		NSLog(@"gestrue %@", elementName);
		if ([type isEqualToString:@"swipe-top-to-bottom"]) {
			swipeType = GestureSwipeTypeTopToBottom;
		} else if ([type isEqualToString:@"swipe-bottom-to-top"]) {
			swipeType = GestureSwipeTypeBottomToTop;
		} else if ([type isEqualToString:@"swipe-left-to-right"]) {
			swipeType = GestureSwipeTypeLeftToRight;
		} else if ([type isEqualToString:@"swipe-right-to-left"]) {
			swipeType = GestureSwipeTypeRightToLeft;
		}
		hasControlCommand = [@"TRUE" isEqualToString:[[attributeDict objectForKey:@"hasControlCommand"] uppercaseString]] ? YES : NO;

		xmlParserParentDelegate = [parent retain];
		[parser setDelegate:self];
	}
	return self;
}

/**
 * Parse the gesture's sub elements .
 */
- (void)parser:(NSXMLParser *)parser didStartElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qualifiedName attributes:(NSDictionary *)attributeDict{
	if ([elementName isEqualToString:@"navigate"]) {
		navigate = [[Navigate alloc] initWithXMLParser:parser elementName:elementName attributes:attributeDict parentDelegate:self];
	}
}


@end
