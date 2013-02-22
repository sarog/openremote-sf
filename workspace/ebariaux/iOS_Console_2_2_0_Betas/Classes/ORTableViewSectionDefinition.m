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
#import "ORTableViewSectionDefinition.h"

@interface ORTableViewSectionDefinition ()

@property (nonatomic, assign, readwrite) NSInteger sectionIdentifier;

@end

@implementation ORTableViewSectionDefinition

- (id)initWithSectionIdentifier:(NSInteger)identifier sectionHeader:(NSString *)header sectionFooter:(NSString *)footer
{
    self = [super init];
    if (self) {
        self.sectionIdentifier = identifier;
        self.sectionHeader = header;
        self.sectionFooter = footer;
    }
    return self;
}

- (id)initWithSectionIdentifier:(NSInteger)identifier
{
    return [self initWithSectionIdentifier:identifier sectionHeader:nil sectionFooter:nil];
}

- (void)dealloc
{
    self.sectionHeader = nil;
    self.sectionFooter = nil;
    [super dealloc];
}

@synthesize sectionIdentifier;
@synthesize sectionHeader;
@synthesize sectionFooter;

@end