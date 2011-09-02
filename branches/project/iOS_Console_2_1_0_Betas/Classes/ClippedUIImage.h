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
#import <UIKit/UIKit.h>
#import <UIKit/UIView.h>

extern NSString *const IMAGE_ABSOLUTE_ALIGN_TO_VIEW;

/**
 * ClippedUIImage is for clipping uiimage depending on parent view and align way.
 */
@interface ClippedUIImage : UIImage {
}

/**
 * Clipping uiimage depending on parent view and align way.
 */
- (id) initWithUIImage:(UIImage *)uiImage dependingOnUIView:(UIView *)uiView imageAlignToView:(NSString *)align;

- (id) initWithUIImage:(UIImage *)uiImage withinUIView:(UIView *)uiView imageAlignToView:(NSString *)align;

@end
