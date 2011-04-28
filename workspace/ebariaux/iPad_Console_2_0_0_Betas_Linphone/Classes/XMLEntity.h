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

extern NSString *const ID;
extern NSString *const REF;

extern NSString *const SWITCH;
extern NSString *const ON;
extern NSString *const OFF;

extern NSString *const SENSOR;
extern NSString *const LINK;
extern NSString *const TYPE;
extern NSString *const INCLUDE;

extern NSString *const STATE;
extern NSString *const NAME;
extern NSString *const VALUE;

extern NSString *const SLIDER;
extern NSString *const THUMB_IMAGE;
extern NSString *const VERTICAL;
extern NSString *const PASSIVE;
extern NSString *const MIN_VALUE;
extern NSString *const MAX_VALUE;
extern NSString *const IMAGE;
extern NSString *const WEB;
extern NSString *const TRACK_IMAGE;

extern NSString *const LABEL;
extern NSString *const FONT_SIZE;
extern NSString *const COLOR;
extern NSString *const TEXT;

extern NSString *const BUTTON;
extern NSString *const DEFAULT;
extern NSString *const PRESSED;

extern NSString *const SRC;
extern NSString *const USERNAME;
extern NSString *const PASSWORD;
extern NSString *const STYLE;

extern NSString *const BG_IMAGE_RELATIVE_POSITION_LEFT;
extern NSString *const BG_IMAGE_RELATIVE_POSITION_RIGHT;
extern NSString *const BG_IMAGE_RELATIVE_POSITION_TOP;
extern NSString *const BG_IMAGE_RELATIVE_POSITION_BOTTOM;
extern NSString *const BG_IMAGE_RELATIVE_POSITION_TOP_LEFT;
extern NSString *const BG_IMAGE_RELATIVE_POSITION_BOTTOM_LEFT;
extern NSString *const BG_IMAGE_RELATIVE_POSITION_TOP_RIGHT;
extern NSString *const BG_IMAGE_RELATIVE_POSITION_BOTTOM_RIGHT;
extern NSString *const BG_IMAGE_RELATIVE_POSITION_CENTER;

extern NSString *const SCREEN;
extern NSString *const BACKGROUND;
extern NSString *const INVERSE_SCREEN_ID;
extern NSString *const LANDSCAPE;

extern NSString *const ABSOLUTE;
extern NSString *const GRID;
extern NSString *const GESTURE;

extern NSString *const GROUP;
extern NSString *const TABBAR;
extern NSString *const ITEM;

extern NSString *const NAVIGATE;

extern NSString *const LOCALLOGIC;
extern NSString *const CLASS;
extern NSString *const METHOD;
extern NSString *const REFRESH_RATE;
extern NSString *const FREQUENCY;

extern NSString *const COLORPICKER;

extern NSString *const COMMAND;
extern NSString *const TASK;


/* This is an abstract class for all entities (element) in panel.xml.
 * Objective-C doesn't have the abstract compiler construct like Java at 
 * this time.
 * 
 * So all you do is define the abstract class as any other normal class 
 * and implement methods stubs for the abstract methods that report NotRecognize for selector.
 */
@interface XMLEntity : NSObject {
	
	NSObject<NSXMLParserDelegate> *xmlParserParentDelegate;

}
 
// NOTE: This is an abstract method, must be implemented in subclass
- (id)initWithXMLParser:(NSXMLParser *)parser elementName:(NSString *)elementName attributes:(NSDictionary *)attributeDict parentDelegate:(NSObject<NSXMLParserDelegate> *)parent;


// NOTE: This is an abstract method, must be implemented in subclass
- (NSString *) elementName;


@end
