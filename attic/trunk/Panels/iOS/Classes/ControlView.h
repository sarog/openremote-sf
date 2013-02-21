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
#import "Control.h"
#import "URLConnectionHelper.h"
#import "ComponentView.h"
#import "ControlDelegate.h"
#import "ORControllerCommandSender.h"

/**
 * It's super class of all control views in screen view.
 */
@interface ControlView : ComponentView <ControlDelegate, ORControllerCommandSenderDelegate> {
	BOOL isError;
    
}

/**
 * Build controls with control model data and frame of its layout container.
 */
+ (ControlView *)controlViewWithControl:(Control *)control frame:(CGRect)frame;

/**
 * Construct controlView instance with control model data and frame of its layout container.
 */
- (id)initWithControl:(Control *)c frame:(CGRect)frame;

- (void)commandSendFailed;

@end