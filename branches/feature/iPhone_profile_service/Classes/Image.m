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

#import "Image.h"
#import "Definition.h"

@implementation Image

@synthesize src;

// get element name, must be overriden in subclass
- (NSString *) elementName {
	return @"image";
}

// This method is abstract method of direct superclass Control.
// So, this method must be overridden in subclass.
- (BOOL)hasPollingStatus {
	return NO;
}

// init a xml entity with NSXMLParser and remember its xmlparser parent delegate 
- (id)initWithXMLParser:(NSXMLParser *)parser elementName:(NSString *)elementName attributes:(NSDictionary *)attributeDict parentDelegate:(NSObject *)parent {
	if (self = [super init]) {
		controlId = [[attributeDict objectForKey:@"id"] intValue];
		src = [[attributeDict objectForKey:@"src"] copy];
		[[Definition sharedDefinition] addImageName:src];
		xmlParserParentDelegate = [parent retain];
		[parser setDelegate:self];
	}
	return self;
}

- (void)parser:(NSXMLParser *)parser didStartElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qualifiedName attributes:(NSDictionary *)attributeDict{
	//stop parsing
}

- (void)dealloc {
	[src release];
	[super dealloc];
}

@end
