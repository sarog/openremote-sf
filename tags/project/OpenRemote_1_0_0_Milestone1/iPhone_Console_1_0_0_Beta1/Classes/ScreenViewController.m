//
//  ScreenViewController.m
//  openremote
//
//  Created by wei allen on 09-2-20.
//  Copyright 2009 finalist. All rights reserved.
//

#import "ScreenViewController.h"
#import "ScreenView.h"


@implementation ScreenViewController

@synthesize screen;

//- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil {
//    if (self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil]) {
//        // Custom initialization
//    }
//    return self;
//}

- (void)setScreen:(Screen *)s {
	[s retain];
	[screen release];
	screen = s;
	
	[self setTitle:screen.name];
	
}

// Implement loadView to create a view hierarchy programmatically.
- (void)loadView {
	ScreenView *view = [[ScreenView alloc] init];
	
	//set Screen in ScreeenView
	[view setScreen:screen];
	[self setView:view];
	[view release];
}


/*
// Implement viewDidLoad to do additional setup after loading the view.
- (void)viewDidLoad {
    [super viewDidLoad];
}
*/



@end
