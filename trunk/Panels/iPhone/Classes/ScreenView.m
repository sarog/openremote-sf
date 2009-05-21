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
	screenNameLabel = [[UILabel alloc] init];
	screenNameLabel.text = s.name;
	[screenNameLabel setTextColor:[UIColor whiteColor]];
	[screenNameLabel setFont:[UIFont boldSystemFontOfSize:14]];
	[screenNameLabel setTextAlignment:UITextAlignmentCenter];
	[screenNameLabel setShadowColor:[UIColor grayColor]];
	[screenNameLabel setBackgroundColor:[UIColor clearColor]];
	[self addSubview:screenNameLabel];
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
	[screenNameLabel setFrame:CGRectMake(self.bounds.origin.x, self.bounds.origin.y, self.bounds.size.width, 20)];

	
	int h = (self.bounds.size.height-20)/screen.rows;
	int w = self.bounds.size.width/screen.cols;
	
	
	for (ControlView *controlView in controlViews) {
		Control *control = [controlView control];
		[controlView setFrame:CGRectInset(CGRectMake(control.x*w, (control.y*h +20), w*control.width, h*control.height),roundf(w*0.1), roundf(h*0.1))];
	}
}

- (void)dealloc {
	[screen release];
	[controlViews release];
	
    [super dealloc];
}


@end
