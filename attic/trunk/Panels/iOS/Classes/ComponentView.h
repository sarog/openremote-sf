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
#import "Component.h"

/**
 * It's super class of all component views in screen view.
 */
@interface ComponentView : UIView {
	Component *component;	
}

@property(nonatomic, readonly) Component *component;

/**
 * Build componentViews with component model data and frame of its layout container.
 */
+ (ComponentView *)componentViewWithComponent:(Component *)component frame:(CGRect)frame;

/**
 * Construct a component view instance with component data and frame of its layout container.
 */
- (id)initWithComponent:(Component *)c frame:(CGRect)frame;

/**
 * It's responsible for initializing view of sub components.
 * This method is abstract, So, it must be overriden in subComponentViews.
 */
- (void) initView;

@end
