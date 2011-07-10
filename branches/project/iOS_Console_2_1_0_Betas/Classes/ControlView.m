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
#import "ControlView.h"
#import "SwitchView.h"
#import "Switch.h"
#import "ViewHelper.h"
#import "ServerDefinition.h"
#import "ButtonView.h"
#import "Button.h"
#import "CFNetwork/CFHTTPMessage.h"
#import "Definition.h"
#import "NotificationConstant.h"
#import "CredentialUtil.h"
#import "ControllerException.h"
#import "Slider.h"
#import "SliderView.h"
#import "LocalLogic.h"
#import "LocalCommand.h"
#import "AppDelegate.h"
#import "ColorPicker.h"
#import "ColorPickerView.h"
#import "ORConsoleSettingsManager.h"
#import "ORControllerProxy.h"

@implementation ControlView

//NOTE:You should init all these views with initWithFrame and you should pass in valid frame rects.
//Otherwise, UI widget will not work in nested UIViews
+ (ControlView *)controlViewWithControl:(Control *)control frame:(CGRect)frame{
	ControlView *controlView = nil;
	if ([control isKindOfClass:[Switch class]]) {
		controlView = [SwitchView alloc];
	} else if  ([control isKindOfClass:[Button class]]) {
		controlView = [ButtonView alloc];
	} else if ([control isKindOfClass:[Slider class]]) {
		controlView = [SliderView alloc];
	} else if ([control isKindOfClass:[ColorPicker class]]) {
		controlView = [ColorPickerView alloc];
	} else {
		return nil;
	}

	return [[controlView initWithControl:control frame:frame] autorelease];
}

#pragma mark instance methods

- (id)initWithControl:(Control *)c frame:(CGRect)frame
{
    self = [super initWithFrame:frame];
	if (self) {
		component = c;
		isError = NO;
		//transparent background 
		[self setBackgroundColor:[UIColor colorWithRed:0.0 green:0.0 blue:0.0 alpha:0.0]];
		//[self setContentMode:UIViewContentModeTopLeft];
	}

	return self;
}

#pragma mark delegate methods of Protocol ControlDelegate.

- (void)sendCommandRequest:(NSString *)commandType {
	// Check for local command first
	LocalCommand *localCommand = [[Definition sharedDefinition].localLogic commandForId:component.componentId];
	if (localCommand) {
		Class clazz = NSClassFromString(localCommand.className);
		SEL selector = NSSelectorFromString([NSString stringWithFormat:@"%@:", localCommand.methodName]);
		[clazz performSelector:selector withObject:((AppDelegate *)[[UIApplication sharedApplication] delegate]).localContext];
	} else {
        [[ORConsoleSettingsManager sharedORConsoleSettingsManager].currentController sendCommand:commandType forComponent:component delegate:nil];
	}
}

#pragma mark ORControllerCommandSenderDelegate implementation

- (void)commandSendFailed
{
    isError = YES;
}

@end