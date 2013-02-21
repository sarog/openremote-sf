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
#import <UIKit/UIKit.h>
#import "XMLEntity.h"
#import "Gesture.h"
#import "Background.h"

/**
 * Stores model data about screen parsed from screen element of panel data and parsed from element screen in panel.xml.
 * XML fragment example:
 * <screen id="5" name="basement">
 *    <background absolute="100,100">
 *       <image src="basement1.png" />
 *    </background>
 *    <absolute left="20" top="320" width="100" height="100" >
 *       <image id="59" src = "a.png" />
 *    </absolute>
 *    <absolute left="20" top="320" width="100" height="100" >
 *       <image id="60" src = "b.png" />
 *    </absolute>
 * </screen>
 */
@interface Screen : XMLEntity <NSXMLParserDelegate> {
	
	int screenId;
	NSString *name;
	Background *background;
	NSMutableArray *layouts;
	NSMutableArray *gestures;
	BOOL landscape;
	int inverseScreenId;// portrait vs landscape screen id

}

/**
 * Get all polling id of sensory components in screen.
 */
- (NSArray *)pollingComponentsIds;

/**
 * Get gesture instance by gesture swipe type.
 */
- (Gesture *)getGestureIdByGestureSwipeType:(GestureSwipeType)type;

/**
 * Returns the id of the screen appriopriate for the provided orientation.
 * If no specific screen exists for the provided orientation, the id of the receiver is returned.
 *
 * @param orientation The orientation for which we're requesting the screen
 *
 * @return int id of the screen to use for the provided orientation.
 */
- (int)screenIdForOrientation:(UIInterfaceOrientation)orientation;

@property (nonatomic,readonly) int screenId;
@property (nonatomic,readonly) NSString *name;
@property (nonatomic,readonly) Background *background;
@property (nonatomic,readonly) NSMutableArray *layouts;
@property (nonatomic,readonly) NSMutableArray *gestures;
@property (nonatomic,readonly) BOOL landscape;
@property (nonatomic,readonly) int inverseScreenId;

@end
