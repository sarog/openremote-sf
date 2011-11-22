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
#import "DefinitionParser.h"
#import "ScreenParser.h"
#import "GroupParser.h"
#import "TabBarParser.h"
#import "LocalLogicParser.h"
#import "Definition.h"
#import "DefinitionElementParserRegister.h"

@interface DefinitionParser()

@property (nonatomic, retain, readwrite) Definition *definition;

@end

@implementation DefinitionParser

- (void)dealloc
{
    self.definition = nil;
    [super dealloc];
}

- (id)initWithRegister:(DefinitionElementParserRegister *)aRegister attributes:(NSDictionary *)attributeDict;
{
    self = [super initWithRegister:aRegister attributes:attributeDict];
    if (self) {
        [self addKnownTag:@"screen"];
        [self addKnownTag:@"group"];
        [self addKnownTag:@"tabbar"];
        [self addKnownTag:@"locallogic"];
        Definition *tmp;
        tmp = [[Definition alloc] init];
        self.definition = tmp;
        [tmp release];
        aRegister.definition = self.definition;
    }
    return self;
}

- (void)endScreenElement:(ScreenParser *)screenParser
{
    [self.definition addScreen:screenParser.screen];
}

- (void)endGroupElement:(GroupParser *)groupParser
{
    [self.definition addGroup:groupParser.group];
}

- (void)endTabBarElement:(TabBarParser *)tabBarParser
{
    self.definition.tabBar = tabBarParser.tabBar;
}

- (void)endLocalLogicElement:(LocalLogicParser *)localLogicParser
{
    self.definition.localLogic = localLogicParser.localLogic;
}

- (NSString *)handledTag
{
    return @"definition";
}

@synthesize definition;

@end