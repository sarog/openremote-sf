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
        GridCell *cell = [[GridCell alloc] initWithXMLParser:parser elementName:elementName attributes:attributeDict parentDelegate:self];
		[cells addObject:cell];
        [cell release];
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
