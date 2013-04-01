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
#import "TaskParser.h"
#import "LocalTask.h"
#import "XMLEntity.h"

@interface TaskParser ()

@property (nonatomic, retain, readwrite) LocalTask *task;

@end

@implementation TaskParser

- (void)dealloc
{
    self.task = nil;
    [super dealloc];
}

- (id)initWithRegister:(DefinitionElementParserRegister *)aRegister attributes:(NSDictionary *)attributeDict;
{
    self = [super initWithRegister:aRegister attributes:attributeDict];
    if (self) {
        LocalTask *tmp = [[LocalTask alloc] initWithId:[[attributeDict objectForKey:ID] intValue]
                                       className:[attributeDict objectForKey:CLASS]
                                      methodName:[attributeDict objectForKey:METHOD]
                                     frequency:([attributeDict objectForKey:FREQUENCY]?[NSNumber numberWithInt:[[attributeDict objectForKey:FREQUENCY] intValue]]:nil)];
        self.task = tmp;
        [tmp release];
    }
    return self;
}

@synthesize task;

@end