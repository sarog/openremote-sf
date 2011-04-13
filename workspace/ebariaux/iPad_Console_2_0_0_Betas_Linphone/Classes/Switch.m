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
#import "SensorState.h"

@implementation Switch

@synthesize onImage, offImage;


//get element name, must be overriden in subclass
- (NSString *) elementName {
	return SWITCH;
}


#pragma mark Delegate methods of NSXMLParser  


- (id)initWithXMLParser:(NSXMLParser *)parser elementName:(NSString *)elementName attributes:(NSDictionary *)attributeDict parentDelegate:(NSObject<NSXMLParserDelegate> *)parent {
	if (self = [super init]) {
		componentId = [[attributeDict objectForKey:ID] intValue];
		
		xmlParserParentDelegate = [parent retain];
		[parser setDelegate:self];
	}
	return self;
}

/**
 * Fill the switch on/off state images .
 */
- (void)parser:(NSXMLParser *)parser didEndElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qName {
	if ([elementName isEqualToString:[self elementName]]) {	
		
		for (SensorState *state in sensor.states) {
			Image *img = [[Image alloc] init];
			img.src = [state.value copy];
			if ([[state.name lowercaseString] isEqualToString:ON]) {
				onImage = img;
			} else if ([[state.name lowercaseString] isEqualToString:OFF]) {
				offImage = img;
			}
			
		}
				
 		[parser setDelegate:xmlParserParentDelegate];
		[xmlParserParentDelegate release];
		xmlParserParentDelegate = nil;
	}
}

- (void)dealloc {
	[onImage release];
	[offImage release];
	
	[super dealloc];
}


@end
