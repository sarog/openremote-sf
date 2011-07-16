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
#import "Switch.h"
#import "Image.h"
#import "SensorState.h"

@implementation Switch

@synthesize onImage, offImage;

//get element name, must be overriden in subclass
- (NSString *) elementName
{
	return SWITCH;
}

#pragma mark Delegate methods of NSXMLParser  


- (id)initWithXMLParser:(NSXMLParser *)parser elementName:(NSString *)elementName attributes:(NSDictionary *)attributeDict parentDelegate:(NSObject<NSXMLParserDelegate> *)parent
{
    self = [super init];
	if (self) {
		componentId = [[attributeDict objectForKey:ID] intValue];
		
		xmlParserParentDelegate = [parent retain];
		[parser setDelegate:self];
	}
	return self;
}

/**
 * Fill the switch on/off state images .
 */
- (void)parser:(NSXMLParser *)parser didEndElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qName
{
	if ([elementName isEqualToString:[self elementName]]) {	
		
		for (SensorState *state in sensor.states) {
			Image *img = [[Image alloc] init];
			img.src = [state.value copy];
			if ([[state.name lowercaseString] isEqualToString:ON]) {
				onImage = [img retain];
			} else if ([[state.name lowercaseString] isEqualToString:OFF]) {
				offImage = [img retain];
			}
			[img release];
		}
				
 		[parser setDelegate:xmlParserParentDelegate];
		[xmlParserParentDelegate release];
		xmlParserParentDelegate = nil;
	}
}

- (void)dealloc
{
	[onImage release];
	[offImage release];
	[xmlParserParentDelegate release];
	[super dealloc];
}


@end
