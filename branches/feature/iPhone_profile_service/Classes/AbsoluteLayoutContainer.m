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

#import "AbsoluteLayoutContainer.h"
#import "SensorComponent.h"

@implementation AbsoluteLayoutContainer

@synthesize component;

- (id)initWithXMLParser:(NSXMLParser *)parser elementName:(NSString *)elementName attributes:(NSDictionary *)attributeDict parentDelegate:(NSObject *)parent {
	if (self = [super init]) {		
		left = [[attributeDict objectForKey:@"left"] intValue];		
		top = [[attributeDict objectForKey:@"top"] intValue];
		width = [[attributeDict objectForKey:@"width"] intValue];
		height = [[attributeDict objectForKey:@"height"] intValue];
		
		xmlParserParentDelegate = [parent retain];
		[parser setDelegate:self];
	}
	NSLog(@"absolute");
	return self;
}

/**
 * Get the polling ids of component in AbsoluteLayoutContainer.
 */
- (NSArray *)pollingComponentsIds {
	NSMutableArray *ids = [[NSMutableArray alloc] init];
	if ([component isKindOfClass:SensorComponent.class]){	
		Sensor *sensor = ((SensorComponent *)component).sensor;
		if (sensor) {
			[ids addObject:[NSString stringWithFormat:@"%d", sensor.sensorId]];
		}
		
	} 
	
	return ids;
}

// get element name, must be overriden in subclass
- (NSString *) elementName {
	return @"absolute";
}

// parse all kinds of controls
- (void)parser:(NSXMLParser *)parser didStartElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qualifiedName attributes:(NSDictionary *)attributeDict{
	component = [Component buildWithXMLParser:elementName parser:parser elementName:elementName attributes:attributeDict parentDelegate:self];
	/**
	 * The returned component instance must be nil, if method [Component buildWithXMLParser] doesn't immediately support latest direct subElements of absolute element 
	 * and wrong direct subElememnts appear in panel.xml.
	 * So, here needs a judgement for nil component instance as the following code.
	 * However, Some description in detail about judgement of nil component instance is neccessary as followiing:
	 * The component is nil, but the delegate of xmlparser is self(absoluteLayoutContainer), so, this delegate method "- (void)parser:(NSXMLParser *)parser didStartElement"
	 * will be called again if the previous metioned "latest direct subElements" and "wrong direct subElememnts" have direct subElements.
	 * And the parameter elementName is the name of direct subElement of previous metioned "latest direct subElement" and "wrong direct subElement".
	 * So, At this time, if the elementName(such as image element) is supported by method "[Component buildWithXMLParser]", 
	 * this returned component isn't nil but a model instance(such as image model), finally the view corresponded to this returned component will be rendered within absoluteLayoutContainerView.
	 * However, please note that the variable component is property of AbsoluteLayoutContainer and elementName built second returned component with isn't
	 * the direct subelement of absolute element in panel.xml. It doesn't make sense that the view corresponded to second returned component is rendered within absoluteLayoutContainerView.
	 * 
	 * So, XmlParser shouldn't be parsing, if direct subElement of absolute element isn't supported,in other word, compoment is nil.
	 * The resolution is assign the parentDelegate(screen) of currentDelegate(AbsoluteLayoutContainer) to xmlParser, 
	 * Because parentDelegate(screen) don't support parsing the second passing elementNames, so the second passing element won't be rendered in screenView either. 
	 * Indeed it works.
	 */
	if (component == nil) {
		[parser setDelegate:xmlParserParentDelegate];
	}
}



- (void)dealloc {
	[component release];
	
	[super dealloc];
}


@end
