//
//  ScreenView.m
//  openremote
//
//  Created by wei allen on 09-2-20.
//  Copyright 2009 finalist. All rights reserved.
//

#import "ScreenView.h"
#import "Control.h"
#import "ControlView.h"


@interface ScreenView (Private) 
- (void)createButtons;
@end

@implementation ScreenView

@synthesize screen;

//override the constractor
- (id)initWithFrame:(CGRect)frame {
    if (self = [super initWithFrame:frame]) {
        controlViews = [[NSMutableArray alloc] init];
    }
    return self;
}

//Set screen and create button on it
- (void)setScreen:(Screen *)s {
	[s retain];
	[screen release];
	screen = s;
	
	[self createButtons];
}
	
//create buttons for each control in screen
- (void)createButtons {
	if (controlViews.count != 0) {
		[controlViews release];
		 controlViews = [[NSMutableArray alloc] init];
	}
	[self setBackgroundColor:[UIColor blackColor]];

	for (Control *control in screen.controls) {
		
		ControlView *controlView = [[ControlView alloc] init];
		[controlView setControl:control];
		[self addSubview:controlView];
		
		[controlViews addObject:controlView];
		[controlView release];
	}
}


//override layoutSubviews method of UIView, In order to resize the ControlView when add subview
// Only in this time we can know this view's size
- (void)layoutSubviews {
	int h = self.bounds.size.height/screen.rows;
	int w = self.bounds.size.width/screen.cols;
	
	for (ControlView *controlView in controlViews) {
		Control *control = [controlView control];
		[controlView setFrame:CGRectInset(CGRectMake(control.x*w, control.y*h, w*control.width, h*control.height),roundf(w*0.1), roundf(h*0.1))];
	}
}

- (void)dealloc {
	[screen release];
	[controlViews release];
	
    [super dealloc];
}


@end
