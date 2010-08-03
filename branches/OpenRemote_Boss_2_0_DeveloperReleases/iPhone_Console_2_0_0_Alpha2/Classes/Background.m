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
#import "XMLEntity.h"


@implementation Background

@synthesize backgroundImageAbsolutePositionLeft, backgroundImageAbsolutePositionTop, isBackgroundImageAbsolutePosition, backgroundImageRelativePosition, fillScreen, backgroundImage;

#pragma mark constructor
//Initialize itself accoding to xml parser
- (id)initWithXMLParser:(NSXMLParser *)parser elementName:(NSString *)elementName attributes:(NSDictionary *)attributeDict parentDelegate:(NSObject *)parent {
	NSLog(@"Begin Constructed background");
	if (self = [super init]) {
		NSString *relativeStr = [[attributeDict objectForKey:@"relative"] copy];
		if (relativeStr) {
			backgroundImageRelativePosition = relativeStr;
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
		
		NSString *fillScreenStr = [[attributeDict objectForKey:@"fillScreen"] copy];
		fillScreen = (fillScreenStr) ? (([@"true" isEqualToString:[fillScreenStr lowercaseString]]) ? YES : NO) : NO;
		
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
