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
#import <UIKit/UIKit.h>

/**
 * A table view controller that makes organizing sections easier.
 * Each section is represented by an ORTableViewSectionDefinition object.
 */
@interface ORTableViewController : UITableViewController

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSectionWithIdentifier:(NSInteger)sectionIdentifier;
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRow:(NSInteger)row inSectionWithIdentifier:(NSInteger)sectionIdentifier;

/**
 * Returns section "number" (as in an index path) based on it's identifier
 */
- (NSInteger)sectionWithIdentifier:(NSInteger)sectionIdentifier;

/**
 * Returns the section identifier for the given section
 */
- (NSInteger)sectionIdentifierForSection:(NSInteger)section;

@property (nonatomic, retain) NSArray *sectionDefinitions;

@end