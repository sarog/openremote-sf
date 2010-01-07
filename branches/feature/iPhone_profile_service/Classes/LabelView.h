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
#import "ComponentView.h"
#define UIColorWithRGBAndAplpha(rgbAlphaValue) [UIColor colorWithRed:((rgbAlphaValue>>24)&0xFF)/255.0 green:((rgbAlphaValue>>16)&0xFF)/255.0 blue:((rgbAlphaValue>>8)&0xFF)/255.0 alpha:((rgbAlphaValue)&0xFF)/255.0];
#define UIColorWithRGB(rgbValue) [UIColor colorWithRed:((float)((rgbValue & 0xFF0000) >> 16))/255.0 green:((float)((rgbValue & 0xFF00) >> 8))/255.0 blue:((float)(rgbValue & 0xFF))/255.0 alpha:1.0]

@interface LabelView : ComponentView <PollingCallBackNotificationDelegate> {
	UILabel *uiLabel;
}

@property (nonatomic, retain) UILabel *uiLabel;

@end
