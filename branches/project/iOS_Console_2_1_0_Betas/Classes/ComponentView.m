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
#import "ComponentView.h"
#import "ControlView.h"
#import "Label.h"
#import "LabelView.h"
#import "Image.h"
#import "ImageView.h"
#import "Web.h"
#import "ORWebView.h"
#import "Control.h"

@implementation ComponentView

@synthesize component;

#pragma mark class methods.

//NOTE:You should init all these views with initWithFrame and you should pass in valid frame rects.
//Otherwise, UI widget will not work in nested UIViews
+ (ComponentView *)componentViewWithComponent:(Component *)component frame:(CGRect)frame {
	ComponentView *componentView = nil;

	if ([component isKindOfClass:[Label class]]) {
		componentView = [LabelView alloc];
	} else if ([component isKindOfClass:[Image class]]) {
		componentView	= [ImageView alloc];
	} else if ([component isKindOfClass:[Web class]]) {
		componentView = [ORWebView alloc];
	} else {
		return [ControlView controlViewWithControl:(Control *)component frame:frame];
	}
	
	return [[componentView initWithComponent:component frame:frame] autorelease];
}


#pragma mark instance methods.

- (id)initWithComponent:(Component *)c frame:(CGRect)frame
{
    self = [super initWithFrame:frame];
	if (self) {
		component = c;
		//transparent background 
		[self setBackgroundColor:[UIColor clearColor]];
		//[self setContentMode:UIViewContentModeTopLeft];
	}
	
	return self;
}

- (void) initView {
	[self doesNotRecognizeSelector:_cmd];
}

#pragma mark Methods of UIView

- (void)layoutSubviews {
	[self initView];
}

@end
