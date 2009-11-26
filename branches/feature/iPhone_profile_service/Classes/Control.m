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


#import "Control.h"
#import "Toggle.h"
#import "Button.h"
#import "Switch.h"
#import "Monitor.h"

@implementation Control

@synthesize controlId;

+ (id)buildWithXMLParser:(NSString *) controlType parser:(NSXMLParser *)parser elementName:(NSString *)elementName attributes:(NSDictionary *)attributeDict parentDelegate:(NSObject *)parent {
	Control *newControl;
	if ([controlType isEqualToString:@"toggle"]) {
		newControl = [Toggle alloc]; 
	} else if ([controlType isEqualToString:@"button"]) {
		newControl = [Button alloc];
	} else if ([controlType isEqualToString:@"switch"]) {
		newControl = [Switch alloc];
	} else if ([controlType isEqualToString:@"monitor"]) {
		newControl = [Monitor alloc];
	}
	return [newControl initWithXMLParser:parser elementName:elementName attributes:attributeDict parentDelegate:parent];
}

/* Whether this control has status to do polling.
 * Returns YES if it has.
 * NOTE: This is an abstract method, must be implemented in subclass
 */
- (BOOL)hasPollingStatus {
	[self doesNotRecognizeSelector:_cmd];
	return NO;
}
	
	
@end
