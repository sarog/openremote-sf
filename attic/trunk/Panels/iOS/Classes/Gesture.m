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
#import "Gesture.h"


@implementation Gesture

@synthesize swipeType, hasControlCommand, navigate;

// This method is abstract method of indirectclass XMLEntity.
// So, this method must be overridden in subclass.
- (NSString *) elementName {
	return GESTURE;
}

- (id)initWithGestureSwipeType:(GestureSwipeType)type {
	if (self = [super init]) {
		swipeType = type;
	}
	return self;
}

- (id)initWithGestureSwipeType:(GestureSwipeType)type orientation:(UIInterfaceOrientation)orientation{
	if (self = [super init]) {
		switch (orientation) {
			case UIInterfaceOrientationPortrait:
				swipeType = type;
				break;
			case UIInterfaceOrientationLandscapeLeft:
				swipeType = (type + 1) % 4;
				break;
			case UIInterfaceOrientationLandscapeRight:
				swipeType = (type - 1 + 4)  % 4;
				break;
			case UIInterfaceOrientationPortraitUpsideDown:
				swipeType = (type + 2) % 4;
				break;	
			default:
				swipeType = type;
				break;
		}		
	}
	return self;
}


- (NSString *)toString {
	switch (swipeType) {
		case GestureSwipeTypeTopToBottom:
			return @"top to bottom";
		case GestureSwipeTypeBottomToTop:
			return @"bottom to top";
		case GestureSwipeTypeLeftToRight:
			return @"left to right";
		case GestureSwipeTypeRightToLeft:
			return @"right to left";
	}
	return nil;
}

#pragma mark Delegate methods of NSXMLParser

- (id)initWithXMLParser:(NSXMLParser *)parser elementName:(NSString *)elementName attributes:(NSDictionary *)attributeDict parentDelegate:(NSObject<NSXMLParserDelegate> *)parent {
	if (self = [super init]) {
		componentId = [[attributeDict objectForKey:ID] intValue];
		NSString *type = [attributeDict objectForKey:TYPE];
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
	if ([elementName isEqualToString:NAVIGATE]) {
		navigate = [[Navigate alloc] initWithXMLParser:parser elementName:elementName attributes:attributeDict parentDelegate:self];
	}
}


@end
