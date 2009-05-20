//
//  InitViewController.m
//  openremote
//
//  Created by finalist on 5/18/09.
//  Copyright 2009 __MyCompanyName__. All rights reserved.
//

#import "InitViewController.h"
#import "NotificationConstant.h"
#import "AppSettingController.h"


@interface InitViewController (Private)
- (void)showSettingsView;

@end

@implementation InitViewController

- (id)init {
	if (self = [super  initWithNibName:@"InitViewController" bundle:nil]) {
		
		label = [[UILabel alloc] initWithFrame:CGRectMake(0, 430, 320, 20)];
		[label setText:@"loading ... please wait."];
		[label setBackgroundColor:[UIColor clearColor]];
		[label setTextColor:[UIColor whiteColor]];
		[label setFont:[UIFont boldSystemFontOfSize:14]];
		[label setTextAlignment:UITextAlignmentCenter];
		[label setShadowColor:[UIColor grayColor]];
		//	[label setShadowColor:[UIColor lightGrayColor]];
		//	[label setShadowOffset:CGSizeMake(1,1)];
		loadding = [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleGray];
		[loadding sizeToFit];
		[loadding setFrame:CGRectMake(140,390,30,30)];
		[self.view addSubview:loadding];
		[self.view  addSubview:label];
		[loadding startAnimating];
		
		[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(showSettingsView) name:NotificationShowSettingsView object:nil];	
	}
	return self;
}

- (void)showSettingsView {
	AppSettingController *settingController = [[AppSettingController alloc]init];
	UINavigationController *secondNavigationController = [[UINavigationController alloc] initWithRootViewController:settingController];
	[self presentModalViewController:secondNavigationController animated:YES];
	[settingController release];
}


- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning]; // Releases the view if it doesn't have a superview
    // Release anything that's not essential, such as cached data
}


- (void)dealloc {
	[loadding release];
	[label release];
    [super dealloc];
}


@end
