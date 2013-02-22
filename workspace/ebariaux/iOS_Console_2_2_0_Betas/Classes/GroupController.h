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
#import "Group.h"
#import "PaginationController.h"
#import "ErrorViewController.h"
#import "Screen.h"

/**
 * Control presentation of group view with group model data.
 */
@interface GroupController : UIViewController {
	PaginationController *portraitPaginationController;
	PaginationController *landscapePaginationController;
	ErrorViewController *errorViewController;
	UIInterfaceOrientation currentOrientation;
}

/**
 * Construct group controller with group model data.
 */
- (id)initWithGroup:(Group *)newGroup parentViewController:(UIViewController *)aVC;

/**
 * Start polling of groupController's rendering screenView.
 */
- (void)startPolling;

/**
 * Stop polling of groupController's rendering screenView.
 */
- (void)stopPolling;

/**
 * Switch the rendering screenView of groupController to the screenView the parameter screenId specified.
 */
- (BOOL)switchToScreen:(int)screenId;

/**
 * Swith the rendering screenView of groupController to previous screenView.
 */
- (BOOL)previousScreen;

/**
 * Swith the rendering screenView of groupController to next screenView.
 */
- (BOOL)nextScreen;

/**
 * Get the screen model data of current screenViewController in groupController.
 */
- (Screen *)currentScreen;

/**
 * Get the id of screen model data of current screenViewController in groupController.
 */
- (int)currentScreenId;

/**
 * Get the id of group model data of groupController.
 */
- (int)groupId;

/**
 * Perform gesture action. Currently, the gesture should be one action of sliding from left to right, 
 * sliding from right to left, sliding from top to bottom and sliding from bottom to top.
 */
- (void)performGesture:(Gesture *)gesture;

/**
 * Get the frame of handset's screen and is depending on the current orientation.
 * If current orientation is landscape, the width of returned frame is height of portrait screen 
 * and the height of returned frame is width of portrait screen.
 */
- (CGRect)getFullFrame;

@property (nonatomic, retain) Group *group;

@end
