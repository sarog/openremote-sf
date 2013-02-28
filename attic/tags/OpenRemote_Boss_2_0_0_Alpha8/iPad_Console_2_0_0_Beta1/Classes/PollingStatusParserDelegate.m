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

#import "PollingStatusParserDelegate.h"
#import "NotificationConstant.h"

@implementation PollingStatusParserDelegate


@synthesize lastId, statusMap;

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

- (void)dealloc {
	[statusMap release];
	[lastId release];

	[super dealloc];	
}

@end
