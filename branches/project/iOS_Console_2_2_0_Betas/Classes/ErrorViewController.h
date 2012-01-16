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
 * It's responsible for controlling errorView's renderiing.
 * Error view will be shown in condition of no groups and no screens.
 * And users can be navigated to AppSetting view with Setting button in errorView.
 */
@interface ErrorViewController : UIViewController {
	
	IBOutlet UILabel *titleLabel;
	IBOutlet UILabel *msgLabel;
	NSMutableArray *items;

}

/**
 * Init errorView with title and message content.
 */
- (id)initWithErrorTitle:(NSString *)title message:(NSString *)message;

- (void)setTitle:(NSString *)title message:(NSString *)message;

@end
