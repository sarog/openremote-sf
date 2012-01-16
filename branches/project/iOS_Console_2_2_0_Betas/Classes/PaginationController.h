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
#import "ScreenViewController.h"

@class Group;

/**
 * This class is mainly responsible for switching screenView in groupController's screenViews.
 */
@interface PaginationController : UIViewController <UIScrollViewDelegate, UITabBarDelegate> {
	NSArray *viewControllers;
	NSUInteger selectedIndex;
	
	UIScrollView *scrollView;
	UIPageControl *pageControl;
	
	BOOL isLandscape;
	
	CGFloat frameWidth;
	CGFloat frameHeight;	
}

@property(nonatomic,copy) NSArray *viewControllers;
@property(nonatomic,readonly) NSUInteger selectedIndex;

- (id)initWithGroup:(Group *)aGroup;

/**
 * Switch to the specified screen with screen id.
 */
- (BOOL)switchToScreen:(int)screenId;

/**
 * Switch to the previous screen of current screen.
 */
- (BOOL)previousScreen;

/**
 * Switch to the next screen of current screen.
 */
- (BOOL)nextScreen;

/**
 * Assign the ScreenViewController array to paginationController with landscape boolean value.
 */
- (void)setViewControllers:(NSArray *)newViewControllers isLandscape:(BOOL)isLandscapeOrientation;

/**
 * Get the current screenViewController instance.
 */
- (ScreenViewController *)currentScreenViewController;

/**
 * Refresh paginationController.
 */
- (void)updateView;

/**
 * Switch the current screen view of paginationController to the first screen view.
 */
- (BOOL)switchToFirstScreen;

@end
