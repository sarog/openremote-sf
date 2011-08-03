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
#import "Button.h"
#import "ControlView.h"

@class ButtonView;

@protocol ControllerButtonAPI

- (void)sendPressCommand:(ButtonView *)sender;
- (void)sendShortReleaseCommand:(ButtonView *)sender;
- (void)sendLongPressCommand:(ButtonView *)sender;
- (void)sendLongReleaseCommand:(ButtonView *)sender;

@end

/**
 * Button View for sending control command and there is no polling for button view.
 */
@interface ButtonView : ControlView {
	
	UIButton *uiButton;
	UIImage *uiImage;
	UIImage *uiImagePressed;
}

@property (nonatomic, readonly) UIButton *uiButton;
@property (nonatomic, readonly) UIImage *uiImage;
@property (nonatomic, readonly) UIImage *uiImagePressed;

@property (nonatomic, retain) id<ControllerButtonAPI> controllerButtonAPI;

@end
