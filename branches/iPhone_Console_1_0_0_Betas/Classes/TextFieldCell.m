//
//  TextFieldCell.m
//  openremote
//
//  Created by finalist on 5/20/09.
//  Copyright 2009 __MyCompanyName__. All rights reserved.
//

#import "TextFieldCell.h"


@implementation TextFieldCell
@synthesize textField;

- (id)initWithFrame:(CGRect)frame reuseIdentifier:(NSString *)reuseIdentifier {
    if (self = [super initWithFrame:frame reuseIdentifier:reuseIdentifier]) {
			textField = [[UITextField alloc] initWithFrame:CGRectZero];
			textField.contentVerticalAlignment = UIControlContentHorizontalAlignmentCenter;
			textField.font = [UIFont systemFontOfSize:22];
			textField.keyboardType = UIKeyboardTypeURL;
			textField.adjustsFontSizeToFitWidth = YES;
			textField.autocapitalizationType = UITextAutocapitalizationTypeNone;
			textField.autocorrectionType = UITextAutocorrectionTypeNo;
			textField.textColor = [UIColor darkGrayColor];
			textField.returnKeyType = UIReturnKeyDone;
			[self addSubview:textField];
    }
    return self;
}

- (void)layoutSubviews {
	// Place the subviews appropriately.
	textField.frame = CGRectInset(self.contentView.bounds, 10, 0);
}

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
