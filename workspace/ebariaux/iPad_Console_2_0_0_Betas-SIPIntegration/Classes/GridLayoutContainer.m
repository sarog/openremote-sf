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

#import "GridLayoutContainer.h"
#import "GridCell.h"
#import "SensorComponent.h"

@implementation GridLayoutContainer

@synthesize cells, rows, cols;


- (id)initWithXMLParser:(NSXMLParser *)parser elementName:(NSString *)elementName attributes:(NSDictionary *)attributeDict parentDelegate:(NSObject<NSXMLParserDelegate> *)parent {
	if (self = [super init]) {		
		left = [[attributeDict objectForKey:@"left"] intValue];		
		top = [[attributeDict objectForKey:@"top"] intValue];
		width = [[attributeDict objectForKey:@"width"] intValue];
		height = [[attributeDict objectForKey:@"height"] intValue];
		rows = [[attributeDict objectForKey:@"rows"] intValue];
		cols = [[attributeDict objectForKey:@"cols"] intValue];
		
		cells = [[NSMutableArray alloc] init];
		xmlParserParentDelegate = [parent retain];
		[parser setDelegate:self];
	}
	NSLog(@"grid");
	return self;
}

- (NSArray *)pollingComponentsIds {
	NSMutableArray *ids = [[[NSMutableArray alloc] init] autorelease];
	for (GridCell *cell in cells) {
		if ([cell.component isKindOfClass:SensorComponent.class]){
			Sensor *sensor = ((SensorComponent *)cell.component).sensor;
			if (sensor) {
				[ids addObject:[NSString stringWithFormat:@"%d",sensor.sensorId]];
			}
			
		} 
	}
	return ids;
}


// parse all kinds of controls
- (void)parser:(NSXMLParser *)parser didStartElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qualifiedName attributes:(NSDictionary *)attributeDict{
	if ([elementName isEqualToString:@"cell"]) {
		[cells addObject:[[GridCell alloc] initWithXMLParser:parser elementName:elementName attributes:attributeDict parentDelegate:self]];
	}
}



- (void)dealloc {
	[cells release];
	
	[super dealloc];
}

// get element name, must be overriden in subclass
- (NSString *) elementName {
	return @"grid";
}



@end
