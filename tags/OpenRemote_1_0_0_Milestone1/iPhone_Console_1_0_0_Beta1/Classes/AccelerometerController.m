//
//  AccelerometerController.m
//  openremote
//
//  Created by finalist on 2/27/09.
//  Copyright 2009 __MyCompanyName__. All rights reserved.
//

#import "AccelerometerController.h"


@implementation AccelerometerController

- (id)init {
	if (self = [super initWithNibName:@"AccelerometerController" bundle:nil]) {
		// Custom initialization
		count = 19;
		UIAccelerometer *accelerometer = [UIAccelerometer sharedAccelerometer];
		[accelerometer setDelegate:self];
		[accelerometer setUpdateInterval:(1.0f/50.f)];
		[self.view addSubview:normalView];
}
    return self;
}
/*
// The designated initializer. Override to perform setup that is required before the view is loaded.
- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil {
    if (self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil]) {
        // Custom initialization
    }
    return self;
}
*/

/*
// Implement loadView to create a view hierarchy programmatically, without using a nib.
- (void)loadView {
}
*/


// Implement viewDidLoad to do additional setup after loading the view, typically from a nib.
- (void)viewDidLoad {
	[self updateLabel];
	        [super viewDidLoad];
}

- (void)accelerometer:(UIAccelerometer *)accelerometer didAccelerate:(UIAcceleration *)acceleration {
	double y = acceleration.y + 1;
	
	if (fabs(y) > accelerationThreshold.value) {
		if (y > 0) {
			if (highValue < y || acceleration.timestamp - highTime > speedThreshold.value) {
				highValue = y;
				highTime = acceleration.timestamp;
			}
		}
		else {
			if (y < lowValue || acceleration.timestamp - lowTime > speedThreshold.value) {
				lowValue = y;
				lowTime = acceleration.timestamp;
			}
		}
	}
	else if (highTime > 0 && lowTime > 0 && fabs(highTime - lowTime) < speedThreshold.value) {
		if (highTime < lowTime) {
			count--;
		}
		else {
			count++;
		}
		
		highTime = lowTime = highValue = lowValue = 0;
		
		[self updateLabel];
	}
}

- (void)updateLabel {
	NSString *str = [[NSString alloc] initWithFormat:@"%d", count];
	volume.text = str;
	[str	release];	
}

- (IBAction)showSettings {    
	
    [UIView beginAnimations:nil context:NULL];
    [UIView setAnimationDuration:1];
    [UIView setAnimationTransition:([normalView superview] ? UIViewAnimationTransitionFlipFromRight : UIViewAnimationTransitionFlipFromLeft) forView:self.view cache:YES];
    
    if ([normalView superview] != nil) {
        [self viewWillAppear:YES];
        [self viewWillDisappear:YES];
        [normalView removeFromSuperview];
        [self.view addSubview:settingView];
		
        [self viewDidDisappear:YES];
        [self viewDidAppear:YES];
		
    } else {
        [self viewWillAppear:YES];
        [self viewWillDisappear:YES];
        [settingView removeFromSuperview];
	[[self navigationItem] setRightBarButtonItem:nil];
        [self.view addSubview:normalView];
        [self viewDidDisappear:YES];
        [self viewDidAppear:YES];
    }
    [UIView commitAnimations];
	
	if ([settingView superview] != nil) {
		UIBarButtonItem *barButtonItem = [[UIBarButtonItem alloc] initWithTitle:@"Done" style:UIBarButtonItemStyleDone target:self action:@selector(showSettings)];
		[[self navigationItem] setRightBarButtonItem:barButtonItem animated:YES];
		[barButtonItem release];
	}
}

- (IBAction)backToNormal {
}

/*
// Override to allow orientations other than the default portrait orientation.
- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation {
    // Return YES for supported orientations
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}
*/

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning]; // Releases the view if it doesn't have a superview
    // Release anything that's not essential, such as cached data
}


- (void)dealloc {
	[[UIAccelerometer sharedAccelerometer] setDelegate:nil];
    [super dealloc];
}


@end
