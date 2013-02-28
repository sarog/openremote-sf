//
//  ScreenView.h
//  openremote
//
//  Created by wei allen on 09-2-20.
//  Copyright 2009 finalist. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "Screen.h"


@interface ScreenView : UIView {
	Screen *screen;
	NSMutableArray *controlViews;
}

@property(nonatomic,retain) Screen *screen;

@end
