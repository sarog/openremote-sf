//
//  LightsController.h
//  openremote
//
//  Created by finalist on 2/26/09.
//  Copyright 2009 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>


@interface LightsController : UIViewController <UITableViewDataSource> {
	IBOutlet UIView *listView;
	IBOutlet UIView *floorplanView;
	NSArray *lights;
	
	IBOutlet UIButton *livingroomLight;
	IBOutlet UIButton *kitchenLight;
	IBOutlet UIButton *barLight;
	UISwitch *livingroomLightSwitch;
	UISwitch *kitchenLightSwitch;
	UISwitch *barLightSwitch;
}

-(IBAction)tapLight:(id)sender;
@end
