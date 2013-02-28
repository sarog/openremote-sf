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

#import <UIKit/UIKit.h>
#import "Control.h"
#import "URLConnectionHelper.h"
#import "ComponentView.h"
#import "ControlDelegate.h"

/**
 * It's super class of all control views in screen view.
 */
@interface ControlView : ComponentView <ControlDelegate>{
	NSTimer *controlTimer;
	BOOL isError;
}

/**
 * Build controls with control model data and frame of its layout container.
 */
+ (ControlView *)buildWithControl:(Control *)control frame:(CGRect)frame;

/**
 * Construct controlView instance with control model data and frame of its layout container.
 */
- (id)initWithControl:(Control *)c frame:(CGRect)frame;

/**
 * Handle the server response which are from controller server with status code.
 */
- (void)handleServerResponseWithStatusCode:(int) statusCode;

/**
 * Cancel timer of repeated sending control command.
 */
- (void)cancelTimer;

@end
