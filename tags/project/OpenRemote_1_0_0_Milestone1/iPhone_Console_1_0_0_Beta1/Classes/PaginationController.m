//
//  PaginationController.m
//  SwipeScreens
//
//  Created by Dennis Stevense on 23-02-09.
//  Copyright 2009 __MyCompanyName__. All rights reserved.
//

#import "PaginationController.h"

@interface PaginationController (Private)

- (void)updateView;
- (void)updateViewForCurrentPage;
- (void)updateViewForPage:(NSUInteger)page;

@end

@implementation PaginationController

@synthesize viewControllers, selectedIndex;

- (void)dealloc {
	[viewControllers release];
	
    [super dealloc];
}

- (void)setViewControllers:(NSArray *)newViewControllers {
	for (UIView *view in [scrollView subviews]) {
		[view removeFromSuperview];
	}
	
	[viewControllers release];
	viewControllers = [newViewControllers copy];
	
	selectedIndex = 0;
	
	[self updateView];
}

- (void)updateView {
	[scrollView setContentSize:CGSizeMake(scrollView.bounds.size.width * [viewControllers count], scrollView.bounds.size.height)];
	[pageControl setNumberOfPages:[viewControllers count]];
	
	[self updateViewForCurrentPage];
}

- (void)updateViewForCurrentPage {
	[self updateViewForPage:selectedIndex - 1];
	[self updateViewForPage:selectedIndex];
	[self updateViewForPage:selectedIndex + 1];
	
	[pageControl setCurrentPage:selectedIndex];
}

- (void)updateViewForPage:(NSUInteger)page {
	if (page < 0) return;
	if (page >= [viewControllers count]) return;
	
	UIViewController *controller = [viewControllers objectAtIndex:page];
	if (controller.view.superview != scrollView) {
		[scrollView addSubview:controller.view];
	}
	
	CGRect frame = scrollView.bounds;
	frame.origin.x = frame.size.width * page;
	[controller.view setFrame:frame];
}

- (void)loadView {
	[super loadView];
	[self.view setFrame:CGRectMake(0, 0, 320, 416)];

	scrollView = [[UIScrollView alloc] init];
	[scrollView setDelegate:self];
	[scrollView setPagingEnabled:YES];
	[scrollView setShowsVerticalScrollIndicator:NO];
	[scrollView setShowsHorizontalScrollIndicator:NO];
	[scrollView setScrollsToTop:NO];
	[scrollView setBackgroundColor:[UIColor blackColor]];
	[scrollView setOpaque:YES];
	[scrollView setAutoresizingMask:UIViewAutoresizingFlexibleHeight | UIViewAutoresizingFlexibleWidth];
	[scrollView setFrame:CGRectMake(0, 0, 320, 396)];
	[self.view addSubview:scrollView];
	[scrollView release];
	
	pageControl = [[UIPageControl alloc] init];
	[pageControl setAutoresizingMask:UIViewAutoresizingFlexibleTopMargin | UIViewAutoresizingFlexibleWidth];
	[pageControl setFrame:CGRectMake(0, 396, 320, 20)];
	[pageControl setBackgroundColor:[UIColor blackColor]];
	[pageControl setOpaque:YES];
	[pageControl addTarget:self action:@selector(pageControlValueDidChange) forControlEvents:UIControlEventValueChanged];
	[self.view addSubview:pageControl];
	[pageControl release];
	
	[self updateView];
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
    CGFloat pageWidth = scrollView.bounds.size.width;
    selectedIndex = floor((scrollView.contentOffset.x - pageWidth / 2) / pageWidth) + 1;
	[self updateViewForCurrentPage];
}

- (void)scrollViewDidEndDecelerating:(UIScrollView *)s {
	pageControlUsed = NO;
}

- (void)pageControlValueDidChange {
	selectedIndex = pageControl.currentPage;
	[self updateViewForCurrentPage];
	
    CGRect frame = scrollView.bounds;
    frame.origin.x = frame.size.width * selectedIndex;
    frame.origin.y = 0;
    [scrollView scrollRectToVisible:frame animated:YES];

	// DENNIS: Maybe you want to make sure that the user can't interact with the scroll view while it is animating.
    //[scrollView setUserInteractionEnabled:NO];
    pageControlUsed = YES;
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation {
	return YES;
}

- (void)willAnimateSecondHalfOfRotationFromInterfaceOrientation:(UIInterfaceOrientation)fromInterfaceOrientation duration:(NSTimeInterval)duration {
	[self updateView];
}

- (void)didRotateFromInterfaceOrientation:(UIInterfaceOrientation)fromInterfaceOrientation {
	[self updateView];
}

- (void)viewWillAppear:(BOOL)animated {
	[self updateView];
}

@end
