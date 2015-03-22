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

#import "LocalLogic.h"
#import "LocalSensor.h"
#import "LocalCommand.h"
#import "LocalTask.h"

@implementation LocalLogic

// This method is abstract method of XMLEntity, must be overriden in it's subclass.
- (NSString *) elementName {
	return LOCALLOGIC;
}

#pragma mark Delegate methods of NSXMLParser
/**
 * Initialize according to the XML parser.
 */
- (id)initWithXMLParser:(NSXMLParser *)parser elementName:(NSString *)elementName attributes:(NSDictionary *)attributeDict parentDelegate:(NSObject<NSXMLParserDelegate> *)parent {
	if (self = [super init]) {
		sensors = [[NSMutableDictionary alloc] init];
		commands = [[NSMutableDictionary alloc] init];
		tasks = [[NSMutableDictionary alloc] init];
		xmlParserParentDelegate = [parent retain];
		[parser setDelegate:self];
	}
	
	return self;
}    

/**
 * Parse the tabbaritem reference elements .
 */
- (void)parser:(NSXMLParser *)parser didStartElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qualifiedName attributes:(NSDictionary *)attributeDict{	
	if ([elementName isEqualToString:SENSOR]) {
		NSLog(@"start sensor in locallogic");
		LocalSensor *sensor = [[LocalSensor alloc] initWithXMLParser:parser elementName:elementName attributes:attributeDict parentDelegate:self];
		[sensors setObject:sensor forKey:[NSNumber numberWithInt:sensor.componentId]];
		[sensor release];
		NSLog(@"end sensor in locallogic");
	} else if ([elementName isEqualToString:COMMAND]) {
		LocalCommand *command = [[LocalCommand alloc] initWithXMLParser:parser elementName:elementName attributes:attributeDict parentDelegate:self];
		[commands setObject:command forKey:[NSNumber numberWithInt:command.componentId]];
		[command release];
	} else if ([elementName isEqualToString:TASK]) {
		LocalTask *task = [[LocalTask alloc] initWithXMLParser:parser elementName:elementName attributes:attributeDict parentDelegate:self];
		[tasks setObject:task forKey:[NSNumber numberWithInt:task.componentId]];
		[task release];
	}	
}

- (LocalSensor *)sensorForId:(NSUInteger)anId {
	return [sensors objectForKey:[NSNumber numberWithInt:anId]];
}

- (LocalCommand *)commandForId:(NSUInteger)anId {
	return [commands objectForKey:[NSNumber numberWithInt:anId]];
}

- (LocalTask *)taskForId:(NSUInteger)anId {
	return [tasks objectForKey:[NSNumber numberWithInt:anId]];
}

- (void)dealloc {
	[sensors release];
	[commands release];
	[tasks release];
	[super dealloc];
}

@end
