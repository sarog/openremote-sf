//
//  AccelerometerController.h
//  openremote
//
//  Created by finalist on 2/27/09.
//  Copyright 2009 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>


@interface AccelerometerController : UIViewController <UIAccelerometerDelegate>{
	IBOutlet UILabel *volume;
	IBOutlet UISlider *speedThreshold;
	IBOutlet UISlider *accelerationThreshold;
	IBOutlet UIButton *settingsBtn;
	IBOutlet UIView *settingView;
	IBOutlet UIView *normalView;
	
	double lastY;
	double highValue;
	double highTime;
	double lowValue;
	double lowTime;
	
	int count;
}
- (void)updateLabel;
- (IBAction)showSettings;
- (IBAction)backToNormal;
@end
