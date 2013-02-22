/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2012, OpenRemote Inc.
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
#import "ComponentSubController.h"
#import "Component.h"
#import "LabelSubController.h"
#import "Label.h"
#import "ImageSubController.h"
#import "Image.h"
#import "ButtonSubController.h"
#import "Button.h"
#import "SwitchSubController.h"
#import "Switch.h"
#import "WebSubController.h"
#import "Web.h"
#import "ColorPickerSubController.h"
#import "ColorPicker.h"
#import "SliderSubController.h"
#import "Slider.h"

@interface ComponentSubController()

@property (nonatomic, readwrite, retain) Component *component;

@end

@implementation ComponentSubController

static NSMutableDictionary *modelObjectToSubControllerClassMapping;

+ (void)initialize
{
    [super initialize];
    modelObjectToSubControllerClassMapping = [[NSMutableDictionary alloc] initWithObjectsAndKeys:
                                              [LabelSubController class], [Label class],
                                              [ImageSubController class], [Image class],
                                              [ButtonSubController class], [Button class],
                                              [SwitchSubController class], [Switch class],
                                              [WebSubController class], [Web class],
                                              [ColorPickerSubController class], [ColorPicker class],
                                              [SliderSubController class], [Slider class],
                                              nil];
}

- (id)initWithComponent:(Component *)aComponent
{
    self = [super init];
    if (self) {
        self.component = [aComponent retain];
    }
    return self;
}

- (void)dealloc
{
    self.component = nil;;
    [super dealloc];
}

+ (Class)subControllerClassForModelObject:(id)modelObject
{
    Class clazz = [modelObjectToSubControllerClassMapping objectForKey:[modelObject class]];
    return clazz?clazz:self;
}

@synthesize component;

@end