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
#import "Control.h"
#import "Navigate.h"

//CW rotate: +1 % 4
//CCW rotate: (-1+4) % 4
//opposite : +2 % 4
typedef enum {
	GestureSwipeTypeBottomToTop  = 0,
	GestureSwipeTypeLeftToRight  = 1,
	GestureSwipeTypeTopToBottom  = 2,
	GestureSwipeTypeRightToLeft  = 3
} GestureSwipeType;

/**
 * Gesture model stores swipeType, hasControlCommand and navigate data, parsed from element gesture in panel.xml.
 * XML fragment example:
 * <gesture id="514" hasControlCommand="true" type="swipe-bottom-to-top">
 *    <navigate to="setting" />
 * </gesture>
 * <gesture id="515" hasControlCommand="true" type="swipe-top-to-bottom">
 *    <navigate to="setting" />
 * </gesture>
 * <gesture id="516" hasControlCommand="true" type="swipe-left-to-right">
 *    <navigate to="setting" />
 * </gesture>
 * <gesture id="517" hasControlCommand="true" type="swipe-right-to-left">
 *    <navigate to="setting" />
 * </gesture>
 */
@interface Gesture : Control <NSXMLParserDelegate> {
	GestureSwipeType swipeType;
	BOOL hasControlCommand;
	Navigate *navigate;

}

@property (nonatomic, readonly)GestureSwipeType swipeType;
@property (nonatomic, readonly)BOOL hasControlCommand;
@property (nonatomic, readonly)Navigate *navigate;

/**
 * Construct a gesture instance with swipe type.
 */
- (id)initWithGestureSwipeType:(GestureSwipeType)type;

/**
 * Construct a gesture instance with swipe type and orientation value.
 */
- (id)initWithGestureSwipeType:(GestureSwipeType)type orientation:(UIInterfaceOrientation)orientation;

- (NSString *)toString;

@end
