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
#import "GridCellParser.h"
#import "GridCell.h"
#import "LabelParser.h"
#import "ImageParser.h"
#import "WebParser.h"
#import "ButtonParser.h"
#import "Button.h"
#import "SwitchParser.h"
#import "SliderParser.h"
#import "ColorPickerParser.h"
#import "DefinitionElementParserRegister.h"
#import "Definition.h"
#import "XMLEntity.h"

@interface GridCellParser()

@property (nonatomic, retain, readwrite) GridCell *gridCell;

@end
/**
 * Store model data of components and parsed from element cell in panel.xml.
 * XML fragment example:
 * <grid left="20" top="20" width="300" height="400" rows="2" cols="2">
 *    <cell x="0" y="0" rowspan="1" colspan="1">
 *    </cell>
 * </grid>
 */
@implementation GridCellParser

- (void)dealloc
{
    self.gridCell = nil;;
    [super dealloc];
}

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
        GridCell *tmp = [[GridCell alloc] initWithX:[[attributeDict objectForKey:@"x"] intValue]
                                             y:[[attributeDict objectForKey:@"y"] intValue]
                                       rowspan:[[attributeDict objectForKey:@"rowspan"] intValue]
                                       colspan:[[attributeDict objectForKey:@"colspan"] intValue]];
        self.gridCell = tmp;
        [tmp release];
    }
    return self;
}

- (void)endLabelElement:(LabelParser *)parser
{
    self.gridCell.component = parser.label;
    [self.depRegister.definition addLabel:parser.label];
}

- (void)endImageElement:(ImageParser *)parser
{
    self.gridCell.component = parser.image;
}

- (void)endWebElement:(WebParser *)parser
{
    self.gridCell.component = parser.web;
}

- (void)endButtonElement:(ButtonParser *)parser
{
    self.gridCell.component = parser.button;
}

- (void)endSwitchElement:(SwitchParser *)parser
{
    self.gridCell.component = parser.sswitch;
}

- (void)endSliderElement:(SliderParser *)parser
{
    self.gridCell.component = parser.slider;
}

- (void)endColorPickerElement:(ColorPickerParser *)parser
{
    self.gridCell.component = parser.colorPicker;
}

@synthesize gridCell;

@end