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
#import "AbsoluteLayoutContainerParser.h"
#import "AbsoluteLayoutContainer.h"
#import "LabelParser.h"
#import "ImageParser.h"
#import "WebParser.h"
#import "ButtonParser.h"
#import "SwitchParser.h"
#import "SliderParser.h"
#import "ColorPickerParser.h"

// TODO: should go when addLabelElement: fixed
#import "Definition.h"

@implementation AbsoluteLayoutContainerParser

- (id)initWithRegister:(DefinitionElementParserRegister *)aRegister attributes:(NSDictionary *)attributeDict
{
    self = [super initWithRegister:aRegister attributes:attributeDict];
    if (self) {
        [self addKnownTag:LABEL];
        [self addKnownTag:IMAGE];
        [self addKnownTag:WEB];
        [self addKnownTag:BUTTON];
        [self addKnownTag:SWITCH];
        [self addKnownTag:SLIDER];
        [self addKnownTag:COLORPICKER];
        layoutContainer = [[AbsoluteLayoutContainer alloc] initWithLeft:[[attributeDict objectForKey:@"left"] intValue]
                                                                    top:[[attributeDict objectForKey:@"top"] intValue]
                                                                  width:[[attributeDict objectForKey:@"width"] intValue]
                                                                 height:[[attributeDict objectForKey:@"height"] intValue]];
    }
    return self;
}

- (void)endLabelElement:(LabelParser *)parser
{
    ((AbsoluteLayoutContainer *)layoutContainer).component = parser.label;
    
    // TODO: review that to access the appropriate Definition instance
    [[Definition sharedDefinition] addLabel:parser.label];
}

- (void)endImageElement:(ImageParser *)parser
{
    ((AbsoluteLayoutContainer *)layoutContainer).component = parser.image;
}

- (void)endWebElement:(WebParser *)parser
{
    ((AbsoluteLayoutContainer *)layoutContainer).component = parser.web;
}

- (void)endButtonElement:(ButtonParser *)parser
{
    ((AbsoluteLayoutContainer *)layoutContainer).component = parser.button;
}

- (void)endSwitchElement:(SwitchParser *)parser
{
    ((AbsoluteLayoutContainer *)layoutContainer).component = parser.sswitch;
}

- (void)endSliderElement:(SliderParser *)parser
{
    ((AbsoluteLayoutContainer *)layoutContainer).component = parser.slider;
}

- (void)endColorPickerElement:(ColorPickerParser *)parser
{
    ((AbsoluteLayoutContainer *)layoutContainer).component = parser.colorPicker;
}

@end
