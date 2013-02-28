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

#import "Component.h"
#import "Control.h"
#import "Label.h"
#import "Image.h"
#import "Definition.h"

@implementation Component

@synthesize componentId;

+ (id)buildWithXMLParser:(NSString *) componentType parser:(NSXMLParser *)parser elementName:(NSString *)elementName attributes:(NSDictionary *)attributeDict parentDelegate:(NSObject *)parent {
	Component *newComponent;
	if ([componentType isEqualToString:LABEL]) {
		newComponent = [Label alloc];
	} else if ([componentType isEqualToString:IMAGE]) {
		newComponent = [Image alloc];
	} else {
		return [Control buildWithXMLParser:componentType parser:parser elementName:elementName attributes:attributeDict parentDelegate:parent];
	}
	newComponent = [newComponent initWithXMLParser:parser elementName:elementName attributes:attributeDict parentDelegate:parent];

	// Cache lables
	if ([newComponent isKindOfClass:[Label class]]) {
		[[Definition sharedDefinition] addLabel:[(Label *)newComponent retain]];
	}
	
	return newComponent;
}

@end
