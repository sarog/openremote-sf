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
}

@property (nonatomic,retain) Control *control;
@end
