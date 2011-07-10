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
#import "Switch.h"
#import <UIKit/UIKit.h>
#import "ControlView.h"
#import "URLConnectionHelper.h"
#import "ComponentView.h"
#import "SensoryControlView.h"

/**
 * SwitchView is mainly for rendering boolean status and sending boolean command to remote controller server.
 */
@interface SwitchView : SensoryControlView {
	UIButton *button;
	BOOL isOn;
	BOOL canUseImage;
	UIImage *onUIImage;
	UIImage *offUIImage;
}

@property (nonatomic,readonly)UIButton *button;
@property (nonatomic,readonly)UIImage *onUIImage;
@property (nonatomic,readonly)UIImage *offUIImage;

@end
