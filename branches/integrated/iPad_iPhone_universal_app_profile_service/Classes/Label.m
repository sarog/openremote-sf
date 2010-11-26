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

#import "Label.h"


@implementation Label

@synthesize fontSize, color, text;

// This method is abstract method of indirectclass XMLEntity.
// So, this method must be overridden in subclass.
- (NSString *) elementName {
	return LABEL;
}


#pragma mark Delegate methods of NSXMLParser  

- (id)initWithXMLParser:(NSXMLParser *)parser elementName:(NSString *)elementName attributes:(NSDictionary *)attributeDict parentDelegate:(NSObject *)parent {
	if (self = [super init]) {
		componentId = [[attributeDict objectForKey:ID] intValue];
		fontSize = [[attributeDict objectForKey:FONT_SIZE] intValue];
		color = [[attributeDict objectForKey:COLOR] copy];
		text = [[attributeDict objectForKey:TEXT] copy];
		xmlParserParentDelegate = [parent retain];
		[parser setDelegate:self];
	}
	return self;
}

- (void)dealloc {
	[color release];
	[text release];
	[super dealloc];
}

@end
