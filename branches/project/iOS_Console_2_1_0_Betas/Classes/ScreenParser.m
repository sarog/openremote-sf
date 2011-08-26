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
#import "ScreenParser.h"
#import "Screen.h"
#import "LayoutContainerParser.h"
#import "GestureParser.h"
#import "BackgroundParser.h"
#import "XMLEntity.h"

@implementation ScreenParser

@synthesize screen;

- (void)dealloc
{
    [screen release];
    [super dealloc];
}

- (id)initWithRegister:(DefinitionElementParserRegister *)aRegister attributes:(NSDictionary *)attributeDict
{
    self = [super initWithRegister:aRegister attributes:attributeDict];
    if (self) {
        [self addKnownTag:ABSOLUTE];
        [self addKnownTag:GRID];
        [self addKnownTag:GESTURE];
        [self addKnownTag:BACKGROUND];
        screen = [[Screen alloc] initWithScreenId:[[attributeDict objectForKey:ID] intValue]
                                    name:[attributeDict objectForKey:NAME]
                                    landscape:[@"TRUE" isEqualToString:[[attributeDict objectForKey:LANDSCAPE] uppercaseString]]
                                    inverseScreenId:[[attributeDict objectForKey:INVERSE_SCREEN_ID] intValue]];
    }
    
    // TODO: check how to report error ?
    
    return self;
}

- (void)endLayoutElement:(LayoutContainerParser *)parser
{
    [screen.layouts addObject:parser.layoutContainer];
}

- (void)endGestureElement:(GestureParser *)parser
{
    [screen.gestures addObject:parser.gesture];
}

- (void)endBackgroundElement:(BackgroundParser *)parser
{
    screen.background = parser.background;
}

- (NSString *)handledTag
{
    return @"screen";
}

@end
