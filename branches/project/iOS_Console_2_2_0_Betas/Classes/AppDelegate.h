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
#import <UIKit/UIKit.h>
#import "InitViewController.h"
#import "UpdateController.h"
#import "ViewHelper.h"
#import "GestureWindow.h"
#import "ScreenViewController.h"
#import "GroupController.h"
#import "Group.h"
#import "Definition.h"
#import "UpdateController.h"
#import "DefaultViewController.h"

#ifdef INCLUDE_SIP_SUPPORT
@class SipController;
#endif

/*
 * This is the entrypoint of the application.
 *  After application have been started applicationDidFinishLaunching method will be called.
 */
@interface AppDelegate : NSObject <UIApplicationDelegate, UpdateControllerDelegate> {
	
	GestureWindow *window;
	DefaultViewController *defaultViewController;
	UpdateController *updateController;

#ifdef INCLUDE_SIP_SUPPORT
	SipController *sipController;
#endif
}

@end

