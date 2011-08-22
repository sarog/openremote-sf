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
#import "Sensor.h"
#import "SensorState.h"

@implementation Sensor

@synthesize sensorId, states;

- (NSString *) elementName {
	return LINK;
}

- (id)initWithId:(int)anId
{
    self = [super init];
    if (self) {
        sensorId = anId;
        states = [[NSMutableArray alloc] init];
    }
    return self;
}

/**
 * Construct sensor instance.
 */
- (id)initWithXMLParser:(NSXMLParser *)parser elementName:(NSString *)elementName attributes:(NSDictionary *)attributeDict parentDelegate:(NSObject<NSXMLParserDelegate> *)parent {
	if (self = [super init]) {		
		sensorId = [[attributeDict objectForKey:REF] intValue];
		NSLog(@"sensor ref id=%d",sensorId);
		states = [[NSMutableArray alloc] init];
		
		xmlParserParentDelegate = [parent retain];
		[parser setDelegate:self];
	}
	return self;
}

// parse sensor state.
- (void)parser:(NSXMLParser *)parser didStartElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qualifiedName attributes:(NSDictionary *)attributeDict{
	if ([elementName isEqualToString:STATE]) {
		SensorState *state = [[SensorState alloc] initWithXMLParser:parser elementName:elementName attributes:attributeDict parentDelegate:self];
		[states addObject:state];
		[state release];
	} 	
}

- (void)dealloc {
	[states release];
	[super dealloc];
}

@end
