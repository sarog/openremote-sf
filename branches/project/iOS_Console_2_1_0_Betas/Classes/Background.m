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
#import "Background.h"
#import "XMLEntity.h"


@implementation Background

@synthesize backgroundImageAbsolutePositionLeft, backgroundImageAbsolutePositionTop, isBackgroundImageAbsolutePosition, backgroundImageRelativePosition, fillScreen, backgroundImage;

- (id)initWithRelativePosition:(NSString *)relativePositionString fillScreen:(BOOL)fillScreenTag
{
    self = [super init];
    if (self) {
        backgroundImageRelativePosition = relativePositionString;
        isBackgroundImageAbsolutePosition = NO;
        fillScreen = fillScreenTag;
    }
    return self;
}

- (id)initWithAbsolutePositionLeft:(int)leftPos top:(int)topPos fillScreen:(BOOL)fillScreenTag
{
    self = [super init];
    if (self) {
        backgroundImageAbsolutePositionLeft = leftPos;
        backgroundImageAbsolutePositionTop = topPos;
        isBackgroundImageAbsolutePosition = YES;
        fillScreen = fillScreenTag;
    }
    return self;
}

#pragma mark constructor
//Initialize itself accoding to xml parser
- (id)initWithXMLParser:(NSXMLParser *)parser elementName:(NSString *)elementName attributes:(NSDictionary *)attributeDict parentDelegate:(NSObject<NSXMLParserDelegate> *)parent {
	NSLog(@"Begin Constructed background");
	if (self = [super init]) {
		NSString *relativeStr = [[attributeDict objectForKey:@"relative"] copy];
		if (relativeStr) {
			backgroundImageRelativePosition = relativeStr;
			isBackgroundImageAbsolutePosition = NO;
		}
		
		NSString *absoluteStr = [attributeDict objectForKey:@"absolute"];
		if (absoluteStr) {
			// Devide the absolute string by comma
			NSRange rangeOfComma = [absoluteStr rangeOfString:@","];
			int indexOfComma = rangeOfComma.location;
			backgroundImageAbsolutePositionLeft = [[absoluteStr substringToIndex:indexOfComma] intValue];
			backgroundImageAbsolutePositionTop = [[absoluteStr substringFromIndex:indexOfComma+1] intValue];
			isBackgroundImageAbsolutePosition = YES;
		}
		
		NSString *fillScreenStr = [attributeDict objectForKey:@"fillScreen"];
		fillScreen = (fillScreenStr) ? ([@"true" isEqualToString:[fillScreenStr lowercaseString]]) : NO;
		
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
