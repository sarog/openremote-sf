/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2012, OpenRemote Inc.
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
#import <Foundation/Foundation.h>
#import "Gesture.h"

@class Background;

@interface Screen : NSObject

- (id)initWithScreenId:(int)anId name:(NSString *)aName landscape:(BOOL)landscapeFlag inverseScreenId:(int)anInverseScreenId;

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
- (int)screenIdForOrientation:(UIDeviceOrientation)orientation;

@property (nonatomic, readonly) int screenId;
@property (nonatomic, retain, readonly) NSString *name;
@property (nonatomic, retain) Background *background;
@property (nonatomic, retain, readonly) NSMutableArray *layouts;
@property (nonatomic, retain, readonly) NSMutableArray *gestures;
@property (nonatomic, readonly) BOOL landscape;
@property (nonatomic, readonly) int inverseScreenId; // portrait vs landscape screen id

@end
