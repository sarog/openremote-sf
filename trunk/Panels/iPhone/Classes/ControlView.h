//
//  ControlView.h
//  openremote
//
//  Created by finalist on 2/23/09.
//  Copyright 2009 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "Control.h"
#import "URLConnectionHelper.h"

@interface ControlView : UIView <URLConnectionHelperDelegate> {

	Control *control;
	UIButton *button;
	NSTimer *buttonTimer;
	BOOL isTouchUp;
	BOOL shouldSendEnd;
	BOOL isError;
	UIImage *icon;
}

@property (nonatomic,retain) Control *control;
@end
