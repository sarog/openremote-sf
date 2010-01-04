/* OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2009, OpenRemote Inc.
 * 
 * See the contributors.txt file in the distribution for a
 * full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3.0 of
 * the License, or (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

#import "ComponentView.h"
#import "ControlView.h"
#import "Label.h"
#import "LabelView.h"
#import "Image.h"
#import "ImageView.h"
#import "Control.h"

@implementation ComponentView

@synthesize component;


//NOTE:You should init all these views with initWithFrame and you should pass in valid frame rects.
//Otherwise, UI widget will not work in nested UIViews
+ (ComponentView *)buildWithComponent:(Component *)component frame:(CGRect)frame {
	ComponentView *componentView = nil;

	if ([component isKindOfClass:[Label class]]) {
		componentView = [LabelView alloc];
	} else if ([component isKindOfClass:[Image class]]) {
		componentView	= [ImageView alloc];
	} else {
		return [ControlView buildWithControl:(Control *)component frame:frame];
	}
	
	return [componentView initWithComponent:component frame:frame];
}

- (id)initWithComponent:(Component *)c frame:(CGRect)frame {
	if (self = [super initWithFrame:frame]) {
		component = c;
		//transparent background 
		[self setBackgroundColor:[UIColor clearColor]];
		//[self setContentMode:UIViewContentModeTopLeft];
	}
	
	return self;
}


- (void)drawRect:(CGRect)rect {
    // Drawing code
}


- (void)dealloc {
    [super dealloc];
}


@end
