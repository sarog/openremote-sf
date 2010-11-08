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
#import <UIKit/UIKit.h>

/**
 * ViewHelper mainly provides convient methods for showing alert view.
 */
@interface ViewHelper : NSObject {
}

/**
 * Show common alert view with customized title and message content.
 */
+(void) showAlertViewWithTitle:(NSString *)title Message:(NSString *)message;

/**
 * Show alert view with customized title, message content and providing a setting button for navigating to appsetting view.
 */
-(void) showAlertViewWithTitleAndSettingNavigation:(NSString *)title Message:(NSString *)message;

@end
