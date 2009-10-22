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
#import "ControlView.h"
#import "Toggle.h"
#import "ToggleView.h"


@implementation ControlView

@synthesize control;

+ (ControlView *)buildWithControl:(Control *)control {
	ControlView *controlView = nil;
	if ([control isKindOfClass:[Toggle class]]) {
		controlView = [ToggleView alloc];
	}
	return [controlView initWithControl:control];
}

- (id)initWithControl:(Control *)c {
	if (self = [super init]) {
		control = c;
		[self layoutSubviews];
	}
	return self;
}

- (id)initWithFrame:(CGRect)frame {
    if (self = [super initWithFrame:frame]) {
        // Initialization code
    }
    return self;
}


- (void)drawRect:(CGRect)rect {
    // Drawing code
}


- (void)dealloc {
	[control release];
	[super dealloc];
}


@end
