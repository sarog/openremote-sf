/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2012, OpenRemote Inc.
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
#import "PollingStatusParserDelegate.h"
#import "NotificationConstant.h"

@implementation PollingStatusParserDelegate

- (id)init {
	if (self = [super init]) {
		statusMap = [[NSMutableDictionary alloc] init];
	}
	return self;
}

#pragma mark delegate method of NSXMLParser
//Delegate method when find a status start we set it to its component
- (void)parser:(NSXMLParser *)parser didStartElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qualifiedName attributes:(NSDictionary *)attributeDict{
	
	if ([elementName isEqualToString:@"status"]) {
		lastId = [[attributeDict valueForKey:@"id"] copy];
	}
}


//find status element body
- (void)parser:(NSXMLParser *)parser foundCharacters:(NSString *)string {
	NSString *status = [string stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceAndNewlineCharacterSet]];//trim found string
	
	//assign lastest status to sensor id
	if (lastId && ![@"" isEqualToString:status]) {
		NSLog(@"change %@ to %@  !!!", lastId, status);
		[statusMap setObject:status forKey:lastId];
		
		// notify latest sensor status to component listener by sensor id
		[[NSNotificationCenter defaultCenter] postNotificationName:[NSString stringWithFormat:NotificationPollingStatusIdFormat,[lastId intValue]] object:self];
	}
}

- (void)publishNewValue:(NSString *)status forSensorId:(NSString *)sensorId {
	[statusMap setObject:status forKey:sensorId];
	[[NSNotificationCenter defaultCenter] postNotificationName:[NSString stringWithFormat:NotificationPollingStatusIdFormat,[sensorId intValue]] object:self];
}

- (void)dealloc {
	[statusMap release];
	[lastId release];

	[super dealloc];	
}

@synthesize lastId, statusMap;

@end