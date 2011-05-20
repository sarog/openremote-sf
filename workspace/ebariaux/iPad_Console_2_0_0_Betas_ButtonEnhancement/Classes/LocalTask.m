/* OpenRemote, the Home of the Digital Home.
 *  * Copyright 2008-2011, OpenRemote Inc-2009, OpenRemote Inc.
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

#import "LocalTask.h"

@implementation LocalTask

@synthesize className, methodName, frequency;

- (id)initWithXMLParser:(NSXMLParser *)parser elementName:(NSString *)elementName attributes:(NSDictionary *)attributeDict parentDelegate:(NSObject<NSXMLParserDelegate> *)parent {
	if (self = [super init]) {
		componentId = [[attributeDict objectForKey:ID] intValue];
		className = [[attributeDict objectForKey:CLASS] retain];
		methodName = [[attributeDict objectForKey:METHOD] retain];
		if ([attributeDict objectForKey:FREQUENCY]) {
			frequency = [[attributeDict objectForKey:FREQUENCY] intValue];
		} else {
			frequency = 5000; // Default to 5 sec
		}
	}
	return self;
}

- (void)dealloc {
	[className release];
	[methodName release];
	[super dealloc];
}

@end
