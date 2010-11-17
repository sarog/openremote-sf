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
#import "ScreenViewController.h"
/**
 * This class is mainly responsible for switching screenView in groupController's screenViews.
 */
@interface PaginationController : UIViewController <UIScrollViewDelegate> {
	NSArray *viewControllers;
	NSUInteger selectedIndex;
	
	UIScrollView *scrollView;
	UIPageControl *pageControl;
	
	BOOL isLandscape;
	BOOL isGrinding;
	BOOL pageControlUsed;
	
	CGFloat frameWidth;
	CGFloat frameHeight;
	
}

@property(nonatomic,copy) NSArray *viewControllers;
@property(nonatomic,readonly) NSUInteger selectedIndex;

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
