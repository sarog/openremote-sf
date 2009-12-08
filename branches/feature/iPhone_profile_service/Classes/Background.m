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

#import "Background.h"
#import "BackgroundImageRelativePositionConstant.h"


@implementation Background

@synthesize backgroundImageAbsolutePositionLeft, backgroundImageAbsolutePositionTop, isBackgroundImageAbsolutePosition, backgroundImageRelativePosition, fullScreen, backgroundImage;

#pragma mark constructor
//Initialize itself accoding to xml parser
- (id)initWithXMLParser:(NSXMLParser *)parser elementName:(NSString *)elementName attributes:(NSDictionary *)attributeDict parentDelegate:(NSObject *)parent {
	NSLog(@"Begin Constructed background");
	if (self = [super init]) {
		NSString *relativeStr = [[attributeDict objectForKey:@"relative"] copy];
		if (relativeStr) {
			if ([BG_IMAGE_RELATIVE_POSITION_LEFT isEqualToString:relativeStr]) {
				backgroundImageRelativePosition = BG_IMAGE_RELATIVE_POSITION_LEFT;
			} else if ([BG_IMAGE_RELATIVE_POSITION_RIGHT isEqualToString:relativeStr]) {
				backgroundImageRelativePosition = BG_IMAGE_RELATIVE_POSITION_RIGHT;
			} else if ([BG_IMAGE_RELATIVE_POSITION_TOP isEqualToString:relativeStr]) {
				backgroundImageRelativePosition = BG_IMAGE_RELATIVE_POSITION_TOP;
			} else if ([BG_IMAGE_RELATIVE_POSITION_BOTTOM isEqualToString:relativeStr]) {
				backgroundImageRelativePosition = BG_IMAGE_RELATIVE_POSITION_BOTTOM;
			} else if ([BG_IMAGE_RELATIVE_POSITION_TOP_LEFT isEqualToString:relativeStr]) {
				backgroundImageRelativePosition = BG_IMAGE_RELATIVE_POSITION_TOP_LEFT;
			} else if ([BG_IMAGE_RELATIVE_POSITION_BOTTOM_LEFT isEqualToString:relativeStr]) {
				backgroundImageRelativePosition = BG_IMAGE_RELATIVE_POSITION_BOTTOM_LEFT;
			} else if ([BG_IMAGE_RELATIVE_POSITION_TOP_RIGHT isEqualToString:relativeStr]) {
				backgroundImageRelativePosition = BG_IMAGE_RELATIVE_POSITION_TOP_RIGHT;
			} else if ([BG_IMAGE_RELATIVE_POSITION_BOTTOM_RIGHT isEqualToString:relativeStr]) {
				backgroundImageRelativePosition = BG_IMAGE_RELATIVE_POSITION_BOTTOM_RIGHT;
			} else if ([BG_IMAGE_RELATIVE_POSITION_CENTER isEqualToString:relativeStr]) {
				backgroundImageRelativePosition = BG_IMAGE_RELATIVE_POSITION_CENTER;
			}
			isBackgroundImageAbsolutePosition = NO;
		}
		
		NSString *absoluteStr = [[attributeDict objectForKey:@"absolute"] copy];
		if (absoluteStr) {
			// Devide the absolute string by comma
			NSRange rangeOfComma = [absoluteStr rangeOfString:@","];
			int indexOfComma = rangeOfComma.location;
			backgroundImageAbsolutePositionLeft = [[absoluteStr substringToIndex:indexOfComma] intValue];
			backgroundImageAbsolutePositionTop = [[absoluteStr substringFromIndex:indexOfComma+1] intValue];
			isBackgroundImageAbsolutePosition = YES;
		}
		
		NSString *fullScreenStr = [[attributeDict objectForKey:@"fullScreen"] copy];
		fullScreen = (fullScreenStr) ? (([@"true" isEqualToString:[fullScreenStr lowercaseString]]) ? YES : NO) : NO;
		
		xmlParserParentDelegate = [parent retain];
		[parser setDelegate:self];
	}
	NSLog(@"End Constructed background");
	return self;
}

// get element name, must be overriden in subclass
- (NSString *) elementName {
	return @"background";
}

#pragma mark deleget method of NSXMLParser

/**
 * Parse the image sub element .
 */
- (void)parser:(NSXMLParser *)parser didStartElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qualifiedName attributes:(NSDictionary *)attributeDict{
	
	if ([elementName isEqualToString:@"image"]) {
		backgroundImage = [[Image alloc] initWithXMLParser:parser elementName:elementName attributes:attributeDict parentDelegate:self];
	}
}

#pragma mark dealloc

-(void) dealloc {
	[backgroundImageRelativePosition release];
	[backgroundImage release];
	[super dealloc];
}

@end
