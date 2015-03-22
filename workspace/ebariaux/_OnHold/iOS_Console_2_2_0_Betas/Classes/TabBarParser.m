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
#import "TabBarParser.h"
#import "TabBar.h"
#import "TabBarItemParser.h"
#import "XMLEntity.h"

@interface TabBarParser ()

@property (nonatomic, retain, readwrite) TabBar *tabBar;

@end

/**
 * Stores model data about tabbar parsed from "tabbar" element in panel.xml.
 * XML fragment example:
 * <tabbar>
 *    <item name="previous">
 *       <navigate to="PreviousScreen" />
 *       <image src="previous.png" />
 *    </item>
 *    <item name="next">
 *	     <navigate to="NextScreen" />
 *		 <image src="next.png" />
 *    </item>
 *	  <item name="setting">
 *       <navigate to="Setting" />
 *       <image src="setting.png" />
 *    </item>                
 * </tabbar>
 */
@implementation TabBarParser

- (void)dealloc
{
    self.tabBar = nil;
    [super dealloc];
}

- (id)initWithRegister:(DefinitionElementParserRegister *)aRegister attributes:(NSDictionary *)attributeDict;
{
    self = [super initWithRegister:aRegister attributes:attributeDict];
    if (self) {
        [self addKnownTag:ITEM];
        TabBar *tmp = [[TabBar alloc] init];
        self.tabBar = tmp;
        [tmp release];
    }
    return self;
}

- (void)endTabBarItemElement:(TabBarItemParser *)parser
{
    [self.tabBar.tabBarItems addObject:parser.tabBarItem];
}

@synthesize tabBar;

@end