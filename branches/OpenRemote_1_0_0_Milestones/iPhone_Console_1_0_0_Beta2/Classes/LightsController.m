//
//  LightsController.m
//  openremote
//
//  Created by finalist on 2/26/09.
//  Copyright 2009 __MyCompanyName__. All rights reserved.
//

#import "LightsController.h"
#import <QuartzCore/QuartzCore.h>

@interface LightsController (Private)

- (void)showCorrectViewForInterfaceOrientation:(UIInterfaceOrientation)fromInterfaceOrientation;
- (void)showCorrectBtnBackground;
-(void)showCorrectSwitchStatus;
- (void)setBtnBackgroundForStatus:(BOOL)status button:(UIButton *)btn;
@end


@implementation LightsController

- (id)init {
	if (self = [super initWithNibName:@"LightsController" bundle:nil]) {
		        // Custom initialization
		
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



// Override to allow orientations other than the default portrait orientation.
- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation {
    // Return YES for supported orientations
    return YES;
}

- (void)willRotateToInterfaceOrientation:(UIInterfaceOrientation)toInterfaceOrientation duration:(NSTimeInterval)duration {
	//CATransition *transition =[CATransition animation];
//	[transition setType:kCATransitionFade];
//	[transition setDuration:duration];
	
	[self showCorrectViewForInterfaceOrientation:toInterfaceOrientation];
	
	if (listView.superview == nil) {
		[self showCorrectBtnBackground];
	} else {
		[self showCorrectSwitchStatus];

	}
	
//	[self.view.layer addAnimation:transition forKey:@"idunno"];
	
}

-(IBAction)tapLight:(id)sender {
	UIButton *button = (UIButton *)sender;	
	if (button.selected) {
		[button setSelected:NO];
	} else {
		[button setSelected:YES];
	}
	[self setBtnBackgroundForStatus:button.selected button:button];
}

- (void)setBtnBackgroundForStatus:(BOOL)status button:(UIButton *)btn{
	UIImage *onImage =[UIImage imageNamed: @"lightbulb.png"];
	UIImage *offImage =[UIImage imageNamed: @"lightbulb_off.png"];
	btn.selected = status;
	if (status) {
		[btn setBackgroundImage:onImage forState:UIControlStateNormal];
	} else {
		[btn setBackgroundImage:offImage forState:UIControlStateNormal];
	}
}

-(void)showCorrectBtnBackground {
	[self setBtnBackgroundForStatus:livingroomLightSwitch.on button:livingroomLight ];
	[self setBtnBackgroundForStatus:kitchenLightSwitch.on button:kitchenLight ];
	[self setBtnBackgroundForStatus:barLightSwitch.on button:barLight ];
}

-(void)showCorrectSwitchStatus{
	[livingroomLightSwitch setOn:livingroomLight.selected animated:YES];
	[kitchenLightSwitch setOn:kitchenLight.selected animated:YES];
	[barLightSwitch setOn:barLight.selected animated:YES];
}

- (void)showCorrectViewForInterfaceOrientation:(UIInterfaceOrientation)fromInterfaceOrientation {
	[listView removeFromSuperview];
	[floorplanView removeFromSuperview];
	
	if (fromInterfaceOrientation == UIInterfaceOrientationLandscapeLeft || fromInterfaceOrientation==UIInterfaceOrientationLandscapeRight) {
		[[self view] addSubview:floorplanView];
	} else {
		[[self view] addSubview:listView];
	}
}
- (void)viewWillAppear:(BOOL)animated {
	[self showCorrectViewForInterfaceOrientation:[self interfaceOrientation]];
	lights = [[NSArray alloc] initWithObjects:@"LivingRoom",@"Kitchen",@"Bar",nil];
}


- (NSInteger)tableView:(UITableView *)table numberOfRowsInSection:(NSInteger)section
{
	//    return activities.count;
	return lights.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{	
	UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"TextAndImage"];
	
	if (cell == nil) {
		cell = [[[UITableViewCell alloc] initWithFrame:CGRectZero reuseIdentifier:@"TextAndImage"] autorelease];
		cell.selectionStyle = UITableViewCellSelectionStyleNone;
		cell.autoresizingMask = UIViewAutoresizingFlexibleWidth;
	}

	
	UISwitch *switcher =nil;
	switch (indexPath.row) {
		case 0:
			livingroomLightSwitch = [[UISwitch alloc] init];
			switcher = livingroomLightSwitch;
			[switcher setOn:livingroomLight.selected];
			break;
		case 1:
			kitchenLightSwitch = [[UISwitch alloc] init];
			switcher = kitchenLightSwitch;
			 [switcher setOn:kitchenLight.selected];
			break;
		case 2:
			barLightSwitch = [[UISwitch alloc] init];
			switcher = barLightSwitch;
			[switcher setOn:barLight.selected];
			break;			
	}
	
	[cell setAccessoryView:switcher];
	cell.text =[lights objectAtIndex:indexPath.row];
	cell.image = [UIImage imageNamed:@"lights.png"];
	return cell;
}




- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning]; // Releases the view if it doesn't have a superview
    // Release anything that's not essential, such as cached data
}


- (void)dealloc {
	[livingroomLightSwitch release];
	[kitchenLightSwitch release];
	[barLightSwitch release];
    [super dealloc];
}


@end
