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
#import "TextFieldCell.h"


@implementation TextFieldCell

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier {
    if (self = [super initWithStyle:style reuseIdentifier:reuseIdentifier]) {
        textField = [[UITextField alloc] initWithFrame:CGRectZero];
        textField.contentVerticalAlignment = UIControlContentVerticalAlignmentCenter;
        textField.font = [UIFont systemFontOfSize:22];
        textField.keyboardType = UIKeyboardTypeURL;
        textField.adjustsFontSizeToFitWidth = YES;
        //textField.clearButtonMode = UITextFieldViewModeWhileEditing;// has a clear 'x' button to the right
        textField.autocapitalizationType = UITextAutocapitalizationTypeNone;// no auto capitalization support
        textField.autocorrectionType = UITextAutocorrectionTypeNo;// no auto correction support
        textField.textColor = [UIColor darkGrayColor];
        textField.returnKeyType = UIReturnKeyDone;
        self.selectionStyle = UITableViewCellSelectionStyleNone;
        [self addSubview:textField];
        [textField release];
    }
    return self;
}

// Override method of UIView.
- (void)layoutSubviews {
    [super layoutSubviews];
    // Place textfield appropriatly in cell
	textField.frame = CGRectInset(self.contentView.frame, 10, 0);
}

// Handler of TextFieldCell is selected.
- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
	[super setSelected:selected animated:animated];
	// Update text color so that it matches expected selection behavior.
	if (selected) {
		textField.textColor = [UIColor whiteColor];
	} else {
		textField.textColor = [UIColor darkGrayColor];
	}
}

@synthesize textField;

@end