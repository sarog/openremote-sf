//
//  TextFieldCell.h
//  openremote
//
//  Created by finalist on 5/20/09.
//  Copyright 2009 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>


@interface TextFieldCell : UITableViewCell {
	UITextField *textField;
}

@property (nonatomic,retain) UITextField *textField;
@end
