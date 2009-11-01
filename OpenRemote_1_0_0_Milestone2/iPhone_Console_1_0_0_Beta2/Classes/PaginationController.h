//
//  PaginationController.h
//  SwipeScreens
//
//  Created by Dennis Stevense on 23-02-09.
//  Copyright 2009 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface PaginationController : UIViewController <UIScrollViewDelegate> {
	NSArray *viewControllers;
	NSUInteger selectedIndex;
	
	UIScrollView *scrollView;
	UIPageControl *pageControl;
	
	BOOL pageControlUsed;
}

@property(nonatomic,copy) NSArray *viewControllers;
@property(nonatomic,readonly) NSUInteger selectedIndex;

@end
