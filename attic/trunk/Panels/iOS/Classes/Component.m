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
#import "Component.h"
#import "Control.h"
#import "Label.h"
#import "Web.h"
#import "Image.h"
#import "Definition.h"

@implementation Component

@synthesize componentId;

+ (id)buildWithXMLParser:(NSString *) componentType parser:(NSXMLParser *)parser elementName:(NSString *)elementName attributes:(NSDictionary *)attributeDict parentDelegate:(NSObject *)parent
{
	Component *newComponent;
	if ([componentType isEqualToString:LABEL]) {
		newComponent = [Label alloc];
	} else if ([componentType isEqualToString:IMAGE]) {
		newComponent = [Image alloc];
	} else if ([componentType isEqualToString:WEB]) {
		newComponent = [Web alloc];
	} else {
		return [Control buildWithXMLParser:componentType parser:parser elementName:elementName attributes:attributeDict parentDelegate:parent];
	}
	newComponent = [newComponent initWithXMLParser:parser elementName:elementName attributes:attributeDict parentDelegate:parent];

	// Cache labels
	if ([newComponent isKindOfClass:[Label class]]) {
		[[Definition sharedDefinition] addLabel:(Label *)newComponent];
	}
	
	return [newComponent autorelease];
}

@end
