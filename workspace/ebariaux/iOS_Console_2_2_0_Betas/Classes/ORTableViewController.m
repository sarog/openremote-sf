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
#import "ORTableViewController.h"
#import "ORTableViewSectionDefinition.h"

@interface ORTableViewController ()

@end

@implementation ORTableViewController

- (NSInteger)sectionWithIdentifier:(NSInteger)sectionIdentifier
{
    for (ORTableViewSectionDefinition *definition in self.sectionDefinitions) {
        if (definition.sectionIdentifier == sectionIdentifier) {
            return [self.sectionDefinitions indexOfObject:definition];
        }
    }
    return NSNotFound;
}

- (NSInteger)sectionIdentifierForSection:(NSInteger)section
{
    return ((ORTableViewSectionDefinition *)[self.sectionDefinitions objectAtIndex:section]).sectionIdentifier;
}

#pragma mark - Table view data source

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return [self.sectionDefinitions count];
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return [self tableView:tableView numberOfRowsInSectionWithIdentifier:[self sectionIdentifierForSection:section]];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return [self tableView:tableView cellForRow:indexPath.row inSectionWithIdentifier:[self sectionIdentifierForSection:indexPath.section]];
}

- (NSString *)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section
{
    return ((ORTableViewSectionDefinition *)[self.sectionDefinitions objectAtIndex:section]).sectionHeader;
}

- (NSString *)tableView:(UITableView *)tableView titleForFooterInSection:(NSInteger)section
{
	return ((ORTableViewSectionDefinition *)[self.sectionDefinitions objectAtIndex:section]).sectionFooter;
}

#pragma mark - Default behavior for methods that subclasses will need to override to provide meaningful behavior

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSectionWithIdentifier:(NSInteger)sectionIdentifier
{
    return 0;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRow:(NSInteger)row inSectionWithIdentifier:(NSInteger)sectionIdentifier
{
    return nil;
}

@synthesize sectionDefinitions;

@end