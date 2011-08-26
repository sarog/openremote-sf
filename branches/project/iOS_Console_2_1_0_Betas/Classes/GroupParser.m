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
#import "GroupParser.h"
#import "Group.h"
#import "TabBarParser.h"
#import "ScreenDeferredBinding.h"
#import "DefinitionElementParserRegister.h"
#import "XMLEntity.h"

/**
 * Stores screens model data and parsed from element group in panel.xml.
 * XML fragment example:
 * <group id="27" name="Bedroom">
 *    <include type="screen" ref="30" />
 *    <include type="screen" ref="45" />
 * </group>
 */
@implementation GroupParser

@synthesize group;

- (void)dealloc
{
    [group release];
    [super dealloc];
}

- (id)initWithRegister:(DefinitionElementParserRegister *)aRegister attributes:(NSDictionary *)attributeDict;
{
    self = [super initWithRegister:aRegister attributes:attributeDict];
    if (self) {
        [self addKnownTag:TABBAR];
        group = [[Group alloc] initWithGroupId:[[attributeDict objectForKey:ID] intValue] name:[attributeDict objectForKey:NAME]];
    }
    return self;
}

- (void)parser:(NSXMLParser *)parser didStartElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qualifiedName attributes:(NSDictionary *)attributeDict
{
	if ([elementName isEqualToString:INCLUDE] && [SCREEN isEqualToString:[attributeDict objectForKey:TYPE]]) {
        // This is a reference to another element, will be resolved later, put a standby in place for now
        ScreenDeferredBinding *standby = [[ScreenDeferredBinding alloc] initWithBoundComponentId:[[attributeDict objectForKey:REF] intValue] enclosingObject:group];
        standby.definition = self.depRegister.definition;
        [self.depRegister addDeferredBinding:standby];
        [standby release];
	}
    [super parser:parser didStartElement:elementName namespaceURI:namespaceURI qualifiedName:qualifiedName attributes:attributeDict];
}

- (void)endTabBarElement:(TabBarParser *)parser
{
    group.tabBar = parser.tabBar;
}

@end
