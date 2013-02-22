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
#import "GridLayoutContainerParser.h"
#import "GridLayoutContainer.h"
#import "GridCellParser.h"

@interface GridLayoutContainerParser()

@property (nonatomic, retain, readwrite) LayoutContainer *layoutContainer;

@end

/**
 * Store gridcell model and parsed from element grid in panel.xml.
 * XML fragment example:
 * <grid left="20" top="20" width="300" height="400" rows="2" cols="2">
 *    <cell x="0" y="0" rowspan="1" colspan="1">
 *    </cell>
 * </grid>
 */
@implementation GridLayoutContainerParser

- (id)initWithRegister:(DefinitionElementParserRegister *)aRegister attributes:(NSDictionary *)attributeDict
{
    self = [super initWithRegister:aRegister attributes:attributeDict];
    if (self) {
        [self addKnownTag:@"cell"];
        LayoutContainer *tmp = [[GridLayoutContainer alloc] initWithLeft:[[attributeDict objectForKey:@"left"] intValue]
                                                                    top:[[attributeDict objectForKey:@"top"] intValue]
                                                                  width:[[attributeDict objectForKey:@"width"] intValue]
                                                                 height:[[attributeDict objectForKey:@"height"] intValue]
                                                               rows:[[attributeDict objectForKey:@"rows"] intValue]
                                                               cols:[[attributeDict objectForKey:@"cols"] intValue]];
        self.layoutContainer = tmp;
        [tmp release];
    }
    return self;
}

- (void)endCellElement:(GridCellParser *)parser
{
    [((GridLayoutContainer *)self.layoutContainer).cells addObject:parser.gridCell];
}

@synthesize layoutContainer;

@end