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

#import "TableViewCellWithSelectionAndIndicator.h"
#import "UIColor+ORAdditions.h"

@implementation TableViewCellWithSelectionAndIndicator

@synthesize entrySelected;
@synthesize indicatorView;

- (void)setEntrySelected:(BOOL)anEntrySelected
{
    entrySelected = anEntrySelected;
    if (self.entrySelected) {
        self.imageView.image = [UIImage imageNamed:@"CheckMark"];
        self.textLabel.textColor = [UIColor or_TableViewCheckMarkColor];
    } else {
        self.imageView.image = [UIImage imageNamed:@"CheckMarkBlankPlaceHolder"];
        self.textLabel.textColor = [UIColor blackColor];
    }
}

- (id)initWithReuseIdentifier:(NSString *)reuseIdentifier
{
    self = [super initWithStyle:UITableViewCellStyleDefault reuseIdentifier:reuseIdentifier];
    if (self) {
        indicatorView = [[UIView alloc] initWithFrame:CGRectZero];
        [self.contentView addSubview:indicatorView];
        
    }
    return self;
}

- (void)dealloc
{
    [indicatorView release];
    [super dealloc];
}

@end
