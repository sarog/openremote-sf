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

#import "Slider.h"


@implementation Slider

@synthesize thumbImage, vertical, passive, minValue, maxValue, minImage, minTrackImage, maxImage, maxTrackImage;

// This method is abstract method of indirectclass XMLEntity.
// So, this method must be overridden in subclass.
- (NSString *) elementName {
	return SLIDER;
}

#pragma mark Delegate methods of NSXMLParser  

- (id)initWithXMLParser:(NSXMLParser *)parser elementName:(NSString *)elementName attributes:(NSDictionary *)attributeDict parentDelegate:(NSObject<NSXMLParserDelegate> *)parent {
	if (self = [super init]) {
		componentId = [[attributeDict objectForKey:ID] intValue];
		
		NSString *verticalStr = [attributeDict objectForKey:VERTICAL];		
		vertical = verticalStr ? [[verticalStr lowercaseString] isEqualToString:@"true"] ? YES : NO : NO;
		NSString *passiveStr = [attributeDict objectForKey:PASSIVE];
		passive = passiveStr ? [[passiveStr lowercaseString] isEqualToString:@"true"] ? YES : NO : NO;
		Image *tempImg = [[Image alloc] init];
		tempImg.src = [[attributeDict objectForKey:THUMB_IMAGE] copy];
		thumbImage = tempImg;
		// Set default values for bounds, in case they're not provided in panel.xml
		minValue = 0.0;
		maxValue = 100.0;
		xmlParserParentDelegate = [parent retain];		
		[parser setDelegate:self];
	}
	return self;
}

/**
 * Parse the slider min/max sub elements .
 */
- (void)parser:(NSXMLParser *)parser didStartElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qualifiedName attributes:(NSDictionary *)attributeDict{
	Image *img = [[Image alloc] init];
	img.src = [[attributeDict objectForKey:IMAGE] copy];
	Image *trackImg = [[Image alloc] init];
	trackImg.src = [[attributeDict objectForKey:TRACK_IMAGE] copy];
	
	if ([elementName isEqualToString:MIN_VALUE]) {
		minValue = [[attributeDict objectForKey:VALUE] floatValue];				
		minImage = img;		
		minTrackImage = trackImg;
	} else if ([elementName isEqualToString:MAX_VALUE]) {
		maxValue = [[attributeDict objectForKey:VALUE] floatValue];
		maxImage = img;		
		maxTrackImage = trackImg;
	} 
	
	[super parser:parser didStartElement:elementName namespaceURI:namespaceURI qualifiedName:qualifiedName attributes:attributeDict];
}

- (void)dealloc {
	[super dealloc];
}

@end
