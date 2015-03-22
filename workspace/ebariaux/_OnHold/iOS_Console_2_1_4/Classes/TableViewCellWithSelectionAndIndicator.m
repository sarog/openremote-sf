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

#pragma mark -

- (id)initWithReuseIdentifier:(NSString *)reuseIdentifier
{
    self = [super initWithStyle:UITableViewCellStyleDefault reuseIdentifier:reuseIdentifier];
    return self;
}

- (void)dealloc
{
    self.indicatorView = nil;
    [super dealloc];
}

#pragma mark -

- (void)layoutSubviews
{
    [super layoutSubviews];

    // indicatorView : fit height, place on right, 10px space with accessoryView and vertically centered    
    self.indicatorView.frame = CGRectMake(self.contentView.bounds.size.width - 10.0 - self.indicatorView.frame.size.width,
                                     (int)((self.contentView.bounds.size.height - self.indicatorView.frame.size.height) / 2),
                                     self.indicatorView.frame.size.width, MIN(self.indicatorView.frame.size.height, self.contentView.bounds.size.height));
    
    // label : make room for indicatorView
    self.textLabel.frame = CGRectMake(self.textLabel.frame.origin.x, self.textLabel.frame.origin.y, self.contentView.bounds.size.width - self.textLabel.frame.origin.x - self.indicatorView.frame.size.width - 10.0, self.textLabel.frame.size.height);
}

@synthesize entrySelected;
@synthesize indicatorView;

- (void)setEntrySelected:(BOOL)anEntrySelected
{
    entrySelected = anEntrySelected;
    if (entrySelected) {
        self.imageView.image = [UIImage imageNamed:@"CheckMark"];
        self.textLabel.textColor = [UIColor or_TableViewCheckMarkColor];
    } else {
        self.imageView.image = [UIImage imageNamed:@"CheckMarkBlankPlaceHolder"];
        self.textLabel.textColor = [UIColor blackColor];
    }
}

- (void)setIndicatorView:(UIView *)aView
{
    if (indicatorView != aView) {
        [indicatorView removeFromSuperview];
        [indicatorView release];
        indicatorView = [aView retain];
        [self.contentView addSubview:indicatorView];
        [self setNeedsLayout];
    }
}

@end