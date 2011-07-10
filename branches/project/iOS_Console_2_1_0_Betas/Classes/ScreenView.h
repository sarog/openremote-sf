/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2011, OpenRemote Inc.
 *
 * See the contributors.txt file in the distribution for a
 * full listing of individual contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
#import <UIKit/UIKit.h>
#import "Screen.h"
#import "URLConnectionHelper.h"
#import "ControlView.h"

#define IPHONE_SCREEN_STATUS_BAR_HEIGHT 20 // Height macro of iPhone status bar
#define IPHONE_SCREEN_BOTTOM_PAGE_CONTROL_HEIGHT 20 // Height macro of iPHone page control

/**
 * Screen view is a container view for render layouts.
 */
@interface ScreenView : UIView {
	Screen *screen;
}


@property(nonatomic,retain) Screen *screen;

- (void)setScreen:(Screen *)s;

@end
