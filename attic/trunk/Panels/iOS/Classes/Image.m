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
#import "Image.h"
#import "Definition.h"
#import "SensorState.h"
#import "Definition.h"

@implementation Image

@synthesize src, style, label;

// get element name, must be overriden in subclass
- (NSString *) elementName {
	return IMAGE;
}

// init a xml entity with NSXMLParser and remember its xmlparser parent delegate 
- (id)initWithXMLParser:(NSXMLParser *)parser elementName:(NSString *)elementName attributes:(NSDictionary *)attributeDict parentDelegate:(NSObject<NSXMLParserDelegate> *)parent {
	if (self = [super init]) {
		componentId = [[attributeDict objectForKey:ID] intValue];
		src = [[attributeDict objectForKey:SRC] copy];
		style = [[attributeDict objectForKey:STYLE] copy];
		[[Definition sharedDefinition] addImageName:src];
		xmlParserParentDelegate = [parent retain];
		[parser setDelegate:self];
	}
	return self;
}

- (void)setSrc:(NSString *)imgSrc {
    if (imgSrc != src) {
        [src release];
        src = [imgSrc retain];
        [[Definition sharedDefinition] addImageName:src];
    }
}

/**
 * Parse the image sub element : sensor link and include.
 */
- (void)parser:(NSXMLParser *)parser didStartElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qualifiedName attributes:(NSDictionary *)attributeDict{
	if ([elementName isEqualToString:INCLUDE] && [LABEL isEqualToString:[attributeDict objectForKey:TYPE]]) {
		int labelRefId = [[attributeDict objectForKey:REF] intValue];
		label = [[Definition sharedDefinition] findLabelById:labelRefId];
	}
	[super parser:parser didStartElement:elementName namespaceURI:namespaceURI qualifiedName:qualifiedName attributes:attributeDict];
	
}

/**
 * Fill the image's sensorImages .
 */
- (void)parser:(NSXMLParser *)parser didEndElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qName {
	if ([elementName isEqualToString:[self elementName]]) {	
		
		for (SensorState *state in sensor.states) {
			[[Definition sharedDefinition] addImageName:state.value];
		}
		
 		[parser setDelegate:xmlParserParentDelegate];
		[xmlParserParentDelegate release];
		xmlParserParentDelegate = nil;
	}
}

- (void)dealloc {
	[src release];
	[style release];
	[label release];
	[super dealloc];
}

@end
