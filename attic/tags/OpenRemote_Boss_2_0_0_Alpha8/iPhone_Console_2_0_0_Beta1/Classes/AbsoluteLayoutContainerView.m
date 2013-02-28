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

#import "AbsoluteLayoutContainerView.h"
#import "AbsoluteLayoutContainer.h"
#import "LayoutContainer.h"

@implementation AbsoluteLayoutContainerView

@synthesize componentView;


- (void)layoutSubviews {
	AbsoluteLayoutContainer *abso = (AbsoluteLayoutContainer *)layout;
	if (abso.component) {
		//NOTE:You should init all nested views with *initWithFrame* and you should pass in valid frame rects.
		//Otherwise, UI widget inside will not work in nested UIViews
		componentView = [ComponentView buildWithComponent:abso.component frame:CGRectMake(0, 0, abso.width, abso.height)];
	}

	[self addSubview:componentView];

}


- (void)dealloc {
	[componentView release];
  
	[super dealloc];
}


@end
