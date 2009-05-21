//
//  AddServerViewController.h
//  openremote
//
//  Created by finalist on 5/20/09.
//  Copyright 2009 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "TextFieldCell.h"


@interface AddServerViewController : UITableViewController<UITableViewDelegate, UITableViewDataSource,UITextFieldDelegate> {
	NSMutableDictionary *editingItem;
	TextFieldCell *serverUrlFieldCell;
	NSMutableArray *servers;
	BOOL newItem;
	UIView *headerView;
	UIView *footerView;
	
}

@property (nonatomic,retain) NSMutableDictionary *editingItem;
@property (nonatomic,retain) NSMutableArray *servers;
@end
