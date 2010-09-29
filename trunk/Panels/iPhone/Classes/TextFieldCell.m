/* OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2009, OpenRemote Inc.
 * 
 * See the contributors.txt file in the distribution for a
 * full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3.0 of
 * the License, or (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */


#import "TextFieldCell.h"


@implementation TextFieldCell
@synthesize textField;

- (id)initWithFrame:(CGRect)frame reuseIdentifier:(NSString *)reuseIdentifier {
    if (self = [super initWithFrame:frame reuseIdentifier:reuseIdentifier]) {
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
    }
    return self;
}

// Override method of UIView.
- (void)layoutSubviews {
	// Place the subviews appropriately.
	textField.frame = CGRectInset(self.contentView.bounds, 10, 0);
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

- (void)dealloc {
	[textField release];
	[super dealloc];
}


@end
