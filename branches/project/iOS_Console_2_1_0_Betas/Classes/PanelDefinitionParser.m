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
#import "PanelDefinitionParser.h"
#import "Definition.h"
#import "DefinitionElementParserRegister.h"
#import "DefinitionParser.h"
#import "ScreenParser.h"
#import "GroupParser.h"
#import "TabBarParser.h"
#import "LocalLogicParser.h"
#import "AbsoluteLayoutContainerParser.h"
#import "GridLayoutContainerParser.h"
#import "GestureParser.h"
#import "BackgroundParser.h"
#import "GridCellParser.h"
#import "LabelParser.h"
#import "ImageParser.h"
#import "WebParser.h"
#import "ButtonParser.h"
#import "SwitchParser.h"
#import "SliderParser.h"
#import "ColorPickerParser.h"
#import "NavigateParser.h"
#import "SensorLinkParser.h"
#import "SensorStateParser.h"
#import "TabBarItemParser.h"
#import "SensorParser.h"
#import "CommandParser.h"
#import "TaskParser.h"
#import "XMLEntity.h"

// TODO: for now definition is a singleton, will have to change to accomodate this logic
// the parsing "factory" should be injected based on the configuration retrieved from the controller
// definition should be set on controller as this is the "central point"
// TODO: note: the openremote tag presence is not checked, is this an issue ?

@interface PanelDefinitionParser()

@property (nonatomic, retain) DefinitionElementParserRegister *depRegistry;

@end

@implementation PanelDefinitionParser

@synthesize depRegistry;

- (id)init {
    self = [super init];
    if (self) {
        self.depRegistry = [[[DefinitionElementParserRegister alloc] init] autorelease];
        [self.depRegistry registerParserClass:[DefinitionParser class] endSelector:NULL forTag:@"openremote"];
        [self.depRegistry registerParserClass:[ScreenParser class] endSelector:@selector(endScreenElement:) forTag:@"screen"];
        [self.depRegistry registerParserClass:[GroupParser class] endSelector:@selector(endGroupElement:) forTag:@"group"];
        [self.depRegistry registerParserClass:[TabBarParser class] endSelector:@selector(endTabBarElement:) forTag:@"tabbar"];
        [self.depRegistry registerParserClass:[LocalLogicParser class] endSelector:@selector(endLocalLogicElement:) forTag:@"locallogic"];
        [self.depRegistry registerParserClass:[AbsoluteLayoutContainerParser class] endSelector:@selector(endLayoutElement:) forTag:ABSOLUTE];
        [self.depRegistry registerParserClass:[GridLayoutContainerParser class] endSelector:@selector(endLayoutElement:) forTag:GRID];
        [self.depRegistry registerParserClass:[GestureParser class] endSelector:@selector(endGestureElement:) forTag:GESTURE];
        [self.depRegistry registerParserClass:[NavigateParser class] endSelector:@selector(endNavigateElement:) forTag:NAVIGATE];
        [self.depRegistry registerParserClass:[BackgroundParser class] endSelector:@selector(endBackgroundElement:) forTag:BACKGROUND];        
        [self.depRegistry registerParserClass:[GridCellParser class] endSelector:@selector(endCellElement:) forTag:@"cell"];
        [self.depRegistry registerParserClass:[LabelParser class] endSelector:@selector(endLabelElement:) forTag:LABEL];
        [self.depRegistry registerParserClass:[ImageParser class] endSelector:@selector(endImageElement:) forTag:IMAGE];
        [self.depRegistry registerParserClass:[SensorLinkParser class] endSelector:@selector(endSensorLinkElement:) forTag:LINK];
        [self.depRegistry registerParserClass:[SensorStateParser class] endSelector:@selector(endSensorStateElement:) forTag:STATE];
        [self.depRegistry registerParserClass:[WebParser class] endSelector:@selector(endWebElement:) forTag:WEB];
        [self.depRegistry registerParserClass:[ButtonParser class] endSelector:@selector(endButtonElement:) forTag:BUTTON];
        [self.depRegistry registerParserClass:[SwitchParser class] endSelector:@selector(endSwitchElement:) forTag:SWITCH];
        [self.depRegistry registerParserClass:[SliderParser class] endSelector:@selector(endSliderElement:) forTag:SLIDER];
        [self.depRegistry registerParserClass:[ColorPickerParser class] endSelector:@selector(endColorPickerElement:) forTag:COLORPICKER];
        [self.depRegistry registerParserClass:[TabBarItemParser class] endSelector:@selector(endTabBarItemElement:) forTag:ITEM];        
        [self.depRegistry registerParserClass:[SensorParser class] endSelector:@selector(endSensorElement:) forTag:SENSOR];        
        [self.depRegistry registerParserClass:[CommandParser class] endSelector:@selector(endCommandElement:) forTag:COMMAND];        
        [self.depRegistry registerParserClass:[TaskParser class] endSelector:@selector(endTaskElement:) forTag:TASK];
    }
    return self;
}

- (void)dealloc
{
    self.depRegistry = nil;
    [super dealloc];
}

- (Definition *)parseDefinitionFromXML:(NSData *)definitionXMLData
{
    NSXMLParser *xmlParser = [[NSXMLParser alloc] initWithData:definitionXMLData];
    DefinitionParser *parser = [[[self.depRegistry parserClassForTag:@"openremote"] alloc] initWithRegister:self.depRegistry attributes:nil];
    [xmlParser setDelegate:parser];
    [xmlParser parse];
    [xmlParser release];
    Definition *definition = [((DefinitionParser *)parser).definition retain];
    [parser release];
    [depRegistry resolveStandbys];
    return [definition autorelease];
}

@end
