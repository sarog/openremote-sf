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

#import "Switch.h"
#import "Image.h"


@implementation Switch

@synthesize onImage, offImage;

//whether to use native style on specified system, e.g. UISwitch on iPhone.
-(BOOL) useNativeStyle {
	return (onImage == nil) || (offImage == nil);
}


//get element name, must be overriden in subclass
- (NSString *) elementName {
	return @"switch";
}

- (BOOL)hasPollingStatus {
	return YES;
}

#pragma mark Delegate methods of NSXMLParser  


- (id)initWithXMLParser:(NSXMLParser *)parser elementName:(NSString *)elementName attributes:(NSDictionary *)attributeDict parentDelegate:(NSObject *)parent {
	if (self = [super init]) {
		controlId = [[attributeDict objectForKey:@"id"] intValue];
		xmlParserParentDelegate = [parent retain];
		[parser setDelegate:self];
	}
	return self;
}

/**
 * Parse the switch on/off sub elements .
 */
- (void)parser:(NSXMLParser *)parser didStartElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qualifiedName attributes:(NSDictionary *)attributeDict{
	
	if ([elementName isEqualToString:@"image"]) {
		Image *img = [[Image alloc] initWithXMLParser:parser elementName:elementName attributes:attributeDict parentDelegate:self];
		if ([[attributeDict objectForKey:@"state"] isEqualToString:@"ON"]) {
				onImage = img;
		} else if ([[attributeDict objectForKey:@"state"] isEqualToString:@"OFF"]) {
				offImage = img;
		}

	}
}

- (void)dealloc {
	[onImage release];
	[offImage release];
	
	[super dealloc];
}


@end
