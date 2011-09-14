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
#import "AbsoluteLayoutContainerSubController.h"
#import "AbsoluteLayoutContainer.h"
#import "Component.h"
#import "ComponentSubController.h"

@interface AbsoluteLayoutContainerSubController()

@property (nonatomic, retain) LayoutContainer *layoutContainer;
@property (nonatomic, retain) ComponentSubController *componentSubController;

@end

@implementation AbsoluteLayoutContainerSubController

- (id)initWithLayoutContainer:(LayoutContainer *)aLayoutContainer
{
    self = [super initWithLayoutContainer:aLayoutContainer];
    if (self) {
        Component *aComponent = ((AbsoluteLayoutContainer *)aLayoutContainer).component;
        self.componentSubController = [[[[ComponentSubController subControllerClassForModelObject:aComponent] alloc] initWithComponent:aComponent] autorelease];
        self.componentSubController.view.frame = CGRectMake(self.layoutContainer.left, self.layoutContainer.top, self.layoutContainer.width, self.layoutContainer.height);
    }
    
    return self;
}

- (void)dealloc
{
    self.layoutContainer = nil;
    self.componentSubController = nil;
    [super dealloc];
}

- (UIView *)view
{
    return self.componentSubController.view;
}

@synthesize layoutContainer;
@synthesize componentSubController;

@end
