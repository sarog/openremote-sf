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
#import "ColorPickerParser.h"
#import "ColorPicker.h"
#import "ImageParser.h"
#import "XMLEntity.h"

@interface ColorPickerParser ()

@property (nonatomic, retain, readwrite) ColorPicker *colorPicker;

@end
/**
 * ColorPicker mainly stores image model data and parsed from element colorpicker.
 * XML fragment example:
 * <colorpicker id="40" >
 *    <image src="colorWheel1.png" />
 * </colorpicker>
 */
@implementation ColorPickerParser

- (void)dealloc
{
    self.colorPicker = nil;
    [super dealloc];
}

- (id)initWithRegister:(DefinitionElementParserRegister *)aRegister attributes:(NSDictionary *)attributeDict
{
    self = [super initWithRegister:aRegister attributes:attributeDict];
    if (self) {
        [self addKnownTag:IMAGE];
        ColorPicker *tmp = [[ColorPicker alloc] initWithId:[[attributeDict objectForKey:@"id"] intValue]];
        self.colorPicker = tmp;
        [tmp release];
    }
    return self;
}

- (void)endImageElement:(ImageParser *)parser
{
    self.colorPicker.image = parser.image;
}

@synthesize colorPicker;

@end