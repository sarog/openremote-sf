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

#define UIColorWithRGBAndAplpha(rgbAlphaValue) [UIColor colorWithRed:((rgbAlphaValue>>24)&0xFF)/255.0 green:((rgbAlphaValue>>16)&0xFF)/255.0 blue:((rgbAlphaValue>>8)&0xFF)/255.0 alpha:((rgbAlphaValue)&0xFF)/255.0];
#define UIColorWithRGB(rgbValue) [UIColor colorWithRed:((float)((rgbValue & 0xFF0000) >> 16))/255.0 green:((float)((rgbValue & 0xFF00) >> 8))/255.0 blue:((float)(rgbValue & 0xFF))/255.0 alpha:1.0]

@interface UIColor (UIColor_ORAdditions)

+ (UIColor *)or_TableViewCheckMarkColor;
+ (UIColor *)or_ColorWithRGBString:(NSString *)rgbString;

@end