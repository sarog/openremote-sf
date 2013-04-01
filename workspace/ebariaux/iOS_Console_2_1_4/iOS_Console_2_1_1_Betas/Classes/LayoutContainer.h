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
#import <Foundation/Foundation.h>

/**
 * It's super class of all layoutContainer model(such as absoluteLayoutCotainer, gridLayoutContainer).
 * The layoutContainer can be located in screen by position info left and top.
 * The layoutContainer's size is described by width and height.
 */
@interface LayoutContainer : NSObject

@property (nonatomic, readonly) int left;
@property (nonatomic, readonly) int top;
@property (nonatomic, readonly) int width;
@property (nonatomic, readonly) int height;

/**
 * Get all component id layoutContainer(such as gridLayoutOutContainer, AbsoluteLayOutContainer).
 */
- (NSArray *)pollingComponentsIds;

@end
