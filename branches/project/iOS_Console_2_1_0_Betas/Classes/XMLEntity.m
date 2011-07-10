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
#import "XMLEntity.h"

/* This is an abstract class for all entities (element) in panel.xml.
 * Objective-C doesn't have the abstract compiler construct like Java at 
 * this time.
 * 
 * So all you do is define the abstract class as any other normal class 
 * and implement methods stubs for the abstract methods that report NotRecognize for selector.
 */
@implementation XMLEntity

NSString *const ID = @"id";
NSString *const REF = @"ref";

NSString *const SWITCH = @"switch";
NSString *const ON = @"on";
NSString *const OFF = @"off";

NSString *const SENSOR = @"sensor";
NSString *const LINK = @"link";
NSString *const TYPE = @"type";
NSString *const INCLUDE = @"include";

NSString *const STATE = @"state";
NSString *const NAME = @"name";
NSString *const VALUE = @"value";

NSString *const SLIDER = @"slider";
NSString *const THUMB_IMAGE = @"thumbImage";
NSString *const VERTICAL = @"vertical";
NSString *const PASSIVE = @"passive";
NSString *const MIN_VALUE = @"min";
NSString *const MAX_VALUE = @"max";
NSString *const WEB = @"web";
NSString *const IMAGE = @"image";
NSString *const TRACK_IMAGE = @"trackImage";

NSString *const LABEL = @"label";
NSString *const FONT_SIZE = @"fontSize";
NSString *const COLOR = @"color";
NSString *const TEXT = @"text";

NSString *const BUTTON = @"button";
NSString *const DEFAULT = @"default";
NSString *const PRESSED = @"pressed";

NSString *const SRC = @"src";
NSString *const USERNAME = @"username";
NSString *const PASSWORD = @"password";
NSString *const STYLE = @"style";

NSString *const BG_IMAGE_RELATIVE_POSITION_LEFT = @"LEFT";
NSString *const BG_IMAGE_RELATIVE_POSITION_RIGHT = @"RIGHT";
NSString *const BG_IMAGE_RELATIVE_POSITION_TOP = @"TOP";
NSString *const BG_IMAGE_RELATIVE_POSITION_BOTTOM =@"BOTTOM";
NSString *const BG_IMAGE_RELATIVE_POSITION_TOP_LEFT = @"TOP_LEFT";
NSString *const BG_IMAGE_RELATIVE_POSITION_BOTTOM_LEFT = @"BOTTOM_LEFT";
NSString *const BG_IMAGE_RELATIVE_POSITION_TOP_RIGHT = @"TOP_RIGHT";
NSString *const BG_IMAGE_RELATIVE_POSITION_BOTTOM_RIGHT = @"BOTTOM_RIGHT";
NSString *const BG_IMAGE_RELATIVE_POSITION_CENTER = @"CENTER";

NSString *const SCREEN = @"screen";
NSString *const BACKGROUND = @"background";
NSString *const INVERSE_SCREEN_ID = @"inverseScreenId";
NSString *const LANDSCAPE = @"landscape";

NSString *const ABSOLUTE = @"absolute";
NSString *const GRID = @"grid";
NSString *const GESTURE = @"gesture";

NSString *const GROUP = @"group";
NSString *const TABBAR = @"tabbar";
NSString *const ITEM = @"item";

NSString *const NAVIGATE = @"navigate";

NSString *const LOCALLOGIC = @"locallogic";

NSString *const COLORPICKER = @"colorpicker";

NSString *const CLASS = @"class";
NSString *const METHOD = @"method";
NSString *const REFRESH_RATE = @"refreshRate";
NSString *const FREQUENCY = @"frequency";

NSString *const COMMAND = @"command";
NSString *const TASK = @"task";


// NOTE: This is an abstract method, must be implemented in subclass
- (NSString *) elementName {
	[self doesNotRecognizeSelector:_cmd];
	return nil;
}

#pragma mark Delegate methods of NSXMLParser

/* init a xml entity with NSXMLParser and remember its xmlparser parent delegate
 * NOTE: This is an abstract method, must be implemented in subclass
 */
- (id)initWithXMLParser:(NSXMLParser *)parser elementName:(NSString *)elementName attributes:(NSDictionary *)attributeDict parentDelegate:(NSObject<NSXMLParserDelegate> *)parent {
	[self doesNotRecognizeSelector:_cmd];
	return nil;
}

/**
 * Parse the start element and create XMLEntity instance.
 * Should be overriden in subclass.
 * NOTE: This is an abstract method, must be implemented in subclass
 */
- (void)parser:(NSXMLParser *)parser didStartElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qualifiedName attributes:(NSDictionary *)attributeDict{
	[self doesNotRecognizeSelector:_cmd];
}

/**
 * When we find a end element, restore the original (parent) XML parser delegate.
 * most subclass does the same action, needn't be overriden.
 * This is an instance method.
 */
- (void)parser:(NSXMLParser *)parser didEndElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qName {
	if ([elementName isEqualToString:[self elementName]]) {	
		//NSLog(@"end %@",[self elementName]);
 		[parser setDelegate:xmlParserParentDelegate];
		[xmlParserParentDelegate release];
		xmlParserParentDelegate = nil;
	}
}

@end
