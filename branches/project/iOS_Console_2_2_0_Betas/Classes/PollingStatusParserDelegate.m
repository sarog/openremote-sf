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
#import "SensorStatusCache.h"

@interface PollingStatusParserDelegate()

@property (nonatomic, retain) NSMutableString *statusValue;
@property (nonatomic, retain) SensorStatusCache *sensorStatusCache;
@end

@implementation PollingStatusParserDelegate

- (id)initWithSensorStatusCache:(SensorStatusCache *)cache {
	if (self = [super init]) {
        self.sensorStatusCache = cache;
	}
	return self;
}

#pragma mark delegate method of NSXMLParser
//Delegate method when find a status start we set it to its component
- (void)parser:(NSXMLParser *)parser didStartElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qualifiedName attributes:(NSDictionary *)attributeDict{
	
	if ([elementName isEqualToString:@"status"]) {
		lastId = [[attributeDict valueForKey:@"id"] copy];
        self.statusValue = [NSMutableString string];
	}
}


//find status element body
- (void)parser:(NSXMLParser *)parser foundCharacters:(NSString *)string {
    [self.statusValue appendString:string];
}

- (void)parser:(NSXMLParser *)parser didEndElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qName {
	if ([elementName isEqualToString:@"status"]) {
        NSString *status = [self.statusValue stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceAndNewlineCharacterSet]]; //trim found string
        
        //assign lastest status to sensor id
        if (lastId && ![@"" isEqualToString:status]) {
            NSLog(@"change %@ to %@  !!!", lastId, status);
            [self.sensorStatusCache publishNewValue:status forSensorId:[lastId intValue]];
        }
    }
}

- (void)dealloc {
    self.statusValue = nil;
	self.sensorStatusCache = nil;
	[lastId release];

	[super dealloc];	
}

@synthesize lastId;
@synthesize statusValue;
@synthesize sensorStatusCache;

@end