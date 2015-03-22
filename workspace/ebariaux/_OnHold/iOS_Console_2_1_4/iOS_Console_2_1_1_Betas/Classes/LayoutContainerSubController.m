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
#import "LayoutContainerSubController.h"
#import "LayoutContainer.h"
#import "AbsoluteLayoutContainer.h"
#import "AbsoluteLayoutContainerSubController.h"
#import "GridLayoutContainer.h"
#import "GridLayoutContainerSubController.h"

@interface LayoutContainerSubController()

@property (nonatomic, retain) LayoutContainer *layoutContainer;

@end

@implementation LayoutContainerSubController

- (id)initWithLayoutContainer:(LayoutContainer *)aLayoutContainer
{
    self = [super init];
    if (self) {
        self.layoutContainer = aLayoutContainer;
    }    
    return self;
}

- (void)dealloc
{
    self.layoutContainer = nil;
    [super dealloc];
}

+ (Class)subControllerClassForModelObject:(id)modelObject
{
    if ([modelObject isKindOfClass:[AbsoluteLayoutContainer class]]) {
        return [AbsoluteLayoutContainerSubController class];
    } else if ([modelObject isKindOfClass:[GridLayoutContainer class]]) {
        return [GridLayoutContainerSubController class];
    }
    return self;
}

@synthesize layoutContainer;

@end