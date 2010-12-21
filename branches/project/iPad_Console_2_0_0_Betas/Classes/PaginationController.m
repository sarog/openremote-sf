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


#import "PaginationController.h"
#import "ScreenViewController.h"

#define PAGE_CONTROL_HEIGHT 20

@interface PaginationController (Private)

- (void)initView;
- (void)updateView;
- (void)initViewForPage:(NSUInteger)page;
- (void)updateViewForPage:(NSUInteger)page;
- (void)updateViewForCurrentPage;
- (void)updateViewForCurrentPageAndBothSides;
- (void)pageControlValueDidChange:(id)sender;
- (void)scrollToSelectedViewWithAnimation:(BOOL)withAnimation;
- (BOOL)switchToScreen:(int)screenId withAnimation:(BOOL) withAnimation;

@end

@implementation PaginationController

@synthesize viewControllers, selectedIndex;

- (void)dealloc {
	[viewControllers release];
	
	[super dealloc];
}

/**
 * Assign the ScreenViewController array to paginationController with landscape boolean value.
 */
- (void)setViewControllers:(NSArray *)newViewControllers isLandscape:(BOOL)isLandscapeOrientation {
	isLandscape = isLandscapeOrientation;
	CGSize size = [UIScreen mainScreen].bounds.size;
	frameWidth = isLandscape ? size.height : size.width;
	frameHeight = isLandscape ? size.width : size.height;
	
	for (UIView *view in [scrollView subviews]) {
		[view removeFromSuperview];
	}
	
	[viewControllers release];
	viewControllers = [newViewControllers retain];
	
	//Recover last screen
	NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
	int lastScreenId = [[userDefaults objectForKey:@"lastScreenId"] intValue];
	NSLog(@"last screen id =%d", lastScreenId);
	
	if (lastScreenId > 0) {
		for (int i = 0; i < [viewControllers count]; i++) {
			if (lastScreenId == [(Screen *)[[viewControllers objectAtIndex:i] screen] screenId]) {
				UIViewController *vc = [viewControllers objectAtIndex:i];
				vc.view.bounds = scrollView.bounds;
				selectedIndex = i;
				break;
			}
		}
	} else {
		selectedIndex = 0;
	}
}

- (BOOL)switchToFirstScreen {
	int screenId = ((ScreenViewController *)[viewControllers objectAtIndex:0]).screen.screenId;
	return [self switchToScreen:screenId];
}

- (ScreenViewController *)currentScreenViewController {
	return [viewControllers objectAtIndex:selectedIndex]; 
}

- (void)initView {
	[scrollView setContentSize:CGSizeMake(frameWidth * [viewControllers count], frameHeight)];
	[pageControl setNumberOfPages:[viewControllers count]];
	[self updateViewForCurrentPage];
}

- (void)updateView {
	[scrollView setContentSize:CGSizeMake(frameWidth * [viewControllers count], frameHeight)];
	[pageControl setNumberOfPages:[viewControllers count]];
	[self updateViewForCurrentPageAndBothSides];
}

//Return YES if succuess, without animation.
- (BOOL)switchToScreen:(int)screenId {
	return [self switchToScreen:screenId withAnimation:NO];
}

//Return YES if succuess
- (BOOL)switchToScreen:(int)screenId withAnimation:(BOOL) withAnimation {
	int index = -1;
	for (int i = 0; i< viewControllers.count; i++) {
		ScreenViewController *svc = (ScreenViewController *)[viewControllers objectAtIndex:i];
		if (svc.screen.screenId == screenId) {
			index = i;
			break;
		}
	}
	if (index != -1) {//found screen in current orientation
		selectedIndex = index;
		NSLog(@"switch to screen index = %d, id = %d animation=%d", selectedIndex, screenId, withAnimation);
		[pageControl setCurrentPage:selectedIndex];
		[self scrollToSelectedViewWithAnimation:withAnimation];
	} else {
		// not found, may be in the opposite orientation, or a invalid screenId.
		return NO;
	}
	
	return YES;
}

//Return YES if succuess
- (BOOL)previousScreen {
	if (selectedIndex == 0) {
		return NO;
	}
	selectedIndex--;
	[pageControl setCurrentPage:selectedIndex];
	[self scrollToSelectedViewWithAnimation:YES];
	return YES;
}

//Return YES if succuess
- (BOOL)nextScreen {
	if (selectedIndex == pageControl.numberOfPages - 1) {
		return NO;
	}
	selectedIndex++;
	[pageControl setCurrentPage:selectedIndex];
	[self scrollToSelectedViewWithAnimation:YES];
	return YES;
}

// Save last screen's id while switching screen view for recovery of lastScreenView in RootViewController.
- (void)saveLastScreen {
	if (selectedIndex < viewControllers.count && selectedIndex >= 0) {
		int lastScreenId = ((ScreenViewController *)[viewControllers objectAtIndex:selectedIndex]).screen.screenId;
		if (lastScreenId == 0) {
			return;
		}
		NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
		[userDefaults setObject:[NSString stringWithFormat:@"%d",lastScreenId] forKey:@"lastScreenId"];
	}
}

// Refresh the current screenView.
- (void)updateViewForCurrentPage {
	[self initViewForPage:selectedIndex];
	[pageControl setCurrentPage:selectedIndex];
}

// Refresh the current screenView, previous screenView and next screenView.
- (void)updateViewForCurrentPageAndBothSides {
	[self updateViewForPage:selectedIndex - 1];
	[self updateViewForPage:selectedIndex];
	[self updateViewForPage:selectedIndex + 1];
	
	[pageControl setCurrentPage:selectedIndex];
	[self saveLastScreen];
}

// Init current screen view of paginationController with specified page index.
- (void)initViewForPage:(NSUInteger)page {
	if (page < 0) return;
	if (page >= [viewControllers count]) return;
	UIViewController *controller = [viewControllers objectAtIndex:page];
	
	if (controller.view.superview != scrollView) {
		CGRect frame = scrollView.bounds;
		frame.origin.x = frameWidth * page;
		[controller.view setFrame:frame];
		scrollView.contentOffset = CGPointMake(frame.origin.x, 0);
		[scrollView addSubview:controller.view];
	}
	
	if (page == selectedIndex) {
		[((ScreenViewController *)controller) startPolling];
	} else {
		[((ScreenViewController *)controller) stopPolling];
	}
}


// Refresh the screenView page index specified.
- (void)updateViewForPage:(NSUInteger)page {
	if (page < 0) return;
	if (page >= [viewControllers count]) return;
	UIViewController *controller = [viewControllers objectAtIndex:page];
	CGRect frame = scrollView.bounds;
	frame.origin.x = frameWidth * page;
	[controller.view setFrame:frame];
	if (controller.view.superview != scrollView) {
		[scrollView addSubview:controller.view];
	}
	
	if (page == selectedIndex) {
		[((ScreenViewController *)controller) startPolling];
	} else {
		[((ScreenViewController *)controller) stopPolling];
	}
	
}

//if you have changed *selectedIndex* then calling this method will scroll to that seleted view immediately
- (void)scrollToSelectedViewWithAnimation:(BOOL)withAnimation {
	
	//HACK: clear 2nd view, it's a hack, beause 2nd view always cover 1st view.
	//TODO: should fix this hack.
	if (withAnimation == NO && selectedIndex == 0 && viewControllers.count > 1) {
		UIViewController *vc = [viewControllers objectAtIndex:1];
		[vc.view removeFromSuperview];
	}
	
	[self updateViewForCurrentPage];
	CGRect frame = scrollView.bounds;
	
	//HACK: make ContentSize larger to show last view correctly, it's a hack, beause last view is half.
	//TODO: should fix this hack.
	if (selectedIndex == pageControl.numberOfPages - 1) {
		[scrollView setContentSize:CGSizeMake(frameWidth * (pageControl.numberOfPages + 0.5), frameHeight)];
	} 

	frame.origin.x = frameWidth * selectedIndex;
	frame.origin.y = 0;
	[scrollView scrollRectToVisible:frame animated:withAnimation];

}

- (void)loadView {
	[super loadView];
	[self.view setFrame:CGRectMake(0, 0, frameWidth, frameHeight)];
	scrollView = [[UIScrollView alloc] init];
	[scrollView setDelegate:self];
	[scrollView setPagingEnabled:YES];
	[scrollView setShowsVerticalScrollIndicator:NO];
	[scrollView setShowsHorizontalScrollIndicator:NO];
	[scrollView setScrollsToTop:NO];
	[scrollView setOpaque:YES];
	[scrollView setAutoresizingMask:UIViewAutoresizingFlexibleHeight | UIViewAutoresizingFlexibleWidth];
	[scrollView setFrame:CGRectMake(0, 0, frameWidth, frameHeight)];
	[scrollView setBackgroundColor:[UIColor blackColor]];
	[self.view addSubview:scrollView];
	[scrollView release];
	pageControl = [[UIPageControl alloc] init];
	if (viewControllers.count > 1) {
		[pageControl setAutoresizingMask:UIViewAutoresizingFlexibleTopMargin | UIViewAutoresizingFlexibleWidth];
		[pageControl setFrame:CGRectMake(0, frameHeight - PAGE_CONTROL_HEIGHT, frameWidth, PAGE_CONTROL_HEIGHT)];
		[pageControl setBackgroundColor:[UIColor colorWithWhite:0 alpha:0.5f]];
		[pageControl setOpaque:YES];
		[pageControl addTarget:self action:@selector(pageControlValueDidChange:) forControlEvents:UIControlEventValueChanged];
		[self.view addSubview:pageControl];
		[pageControl release];
	}
	pageControlUsed = NO;
	[self initView];
}

- (void)didReceiveMemoryWarning {
	// Our view will be released when it has no superview, so set these references to nil
	if (self.view.superview == nil) {
		scrollView = nil;
		pageControl = nil;
	}
	
	[super didReceiveMemoryWarning];
}

- (void)scrollViewDidScroll:(UIScrollView *)sender {
	if (pageControlUsed) return;

	// Switch the indicator when more than 50% of the previous/next page is visible
	selectedIndex = floor((scrollView.contentOffset.x - frameWidth / 2) / frameWidth) + 1;
	
	[self updateViewForCurrentPageAndBothSides];
}

- (void)scrollViewWillBeginDragging:(UIScrollView *)s {
	//HACK: correct hacked larger ContentSize back to normal.
	//TODO: should fix this hack.
	[scrollView setContentSize:CGSizeMake(frameWidth * pageControl.numberOfPages, frameHeight)];
}

- (void)scrollViewDidEndDecelerating:(UIScrollView *)s {
	pageControlUsed = NO;
}

- (void)pageControlValueDidChange:(id)sender {
	selectedIndex = pageControl.currentPage;
	[self updateViewForCurrentPageAndBothSides];
	
	CGRect frame = scrollView.bounds;
	frame.origin.x = frameWidth * selectedIndex;
	frame.origin.y = 0;
	[scrollView scrollRectToVisible:frame animated:YES];

	// DENNIS: Maybe you want to make sure that the user can't interact with the scroll view while it is animating.
	//[scrollView setUserInteractionEnabled:NO];
	pageControlUsed = NO;
}


- (void)viewWillAppear:(BOOL)animated {
	[self initView];
}

@end
