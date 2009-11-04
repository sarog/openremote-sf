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


#import <Foundation/Foundation.h>

/* This is an abstract class for all entities (element) in panel.xml.
 * Objective-C doesn't have the abstract compiler construct like Java at 
 * this time.
 * 
 * So all you do is define the abstract class as any other normal class 
 * and implement methods stubs for the abstract methods that report NotRecognize for selector.
 */
@interface XMLEntity : NSObject {
	
	NSObject *xmlParserParentDelegate;

}
 
// NOTE: This is an abstract method, must be implemented in subclass
- (id)initWithXMLParser:(NSXMLParser *)parser elementName:(NSString *)elementName attributes:(NSDictionary *)attributeDict parentDelegate:(NSObject *)parent;


// NOTE: This is an abstract method, must be implemented in subclass
- (NSString *) elementName;


@end
