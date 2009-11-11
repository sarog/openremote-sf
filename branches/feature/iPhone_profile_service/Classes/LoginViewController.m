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

#import "LoginViewController.h"

@interface LoginViewController (Private)

- (void)cancelView:(id)sender;
@end


@implementation LoginViewController

@synthesize usernameField, passwordField;


- (id)init {
	if (self = [super initWithStyle:UITableViewStyleGrouped]) {
		[self	setTitle:@"Sign in"];
	}
	return self;
}

- (void)viewDidLoad {
	
	self.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc]initWithTitle:@"Cancel" style:UIBarButtonItemStylePlain target:self action:@selector(cancelView:)];
	[super viewDidLoad];
    // Uncomment the following line to display an Edit button in the navigation bar for this view controller.
    // self.navigationItem.rightBarButtonItem = self.editButtonItem;
}



- (void)didReceiveMemoryWarning {
	// Releases the view if it doesn't have a superview.
    [super didReceiveMemoryWarning];
	
	// Release any cached data, images, etc that aren't in use.
}

- (void)viewDidUnload {
	// Release any retained subviews of the main view.
	// e.g. self.myOutlet = nil;
}

- (void)cancelView:(id)sender {
	[self dismissModalViewControllerAnimated:YES];
	//[[NSNotificationCenter defaultCenter] postNotificationName:NotificationRefreshGroupsView object:nil];
}

- (void)signin:(id)sender {
	[self dismissModalViewControllerAnimated:YES];
}



#pragma mark Table view methods

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 2;
}


// Customize the number of rows in the table view.
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
	if(section == 0) {
		return 2;
	} else if (section ==1){
		return 1;
	}
	return 0;
}


// Customize the appearance of table view cells.
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    
	static NSString *loginCellIdentifier = @"loginCell";

	
	UITableViewCell *loginCell = [tableView dequeueReusableCellWithIdentifier:loginCellIdentifier];

	
	if (loginCell == nil) {
		loginCell = [[[UITableViewCell alloc] initWithFrame:CGRectZero reuseIdentifier:loginCellIdentifier] autorelease];
	}

	
	if (indexPath.section == 0) {
		loginCell.selectionStyle = UITableViewCellSelectionStyleNone;
		UITextField *textField = [[UITextField alloc] initWithFrame:CGRectZero];
		textField.contentVerticalAlignment = UIControlContentHorizontalAlignmentCenter;
		textField.font = [UIFont systemFontOfSize:22];
		textField.keyboardType = UIKeyboardTypeURL;
		textField.adjustsFontSizeToFitWidth = YES;
		textField.autocapitalizationType = UITextAutocapitalizationTypeNone;
		textField.autocorrectionType = UITextAutocorrectionTypeNo;
		textField.textColor = [UIColor darkGrayColor];
		textField.returnKeyType = UIReturnKeyDone;
		textField.frame = CGRectInset(CGRectMake(110, (loginCell.bounds.size.height - 26)/2.0, 200,26),10,0);
		
		[textField setDelegate:self];
		
		[loginCell addSubview:textField];
    
		if (indexPath.row == 0) {
			loginCell.textLabel.text = @"Username";
			//[textField becomeFirstResponder];
			usernameField = textField;
		} else if (indexPath.row == 1) {
			loginCell.textLabel.text = @"Password";
			[textField setSecureTextEntry:YES];
			passwordField = textField;
		}
		
		
	} else if (indexPath.section == 1) {
		UIButton *signinButton = [[UIButton buttonWithType:UIButtonTypeCustom] retain];
		[signinButton setFrame:CGRectInset([loginCell bounds], 10, 0)];
		UIImage *buttonImage = [[UIImage imageNamed:@"btn_green.png"] stretchableImageWithLeftCapWidth:20 topCapHeight:20];
		[signinButton setBackgroundImage:buttonImage forState:UIControlStateNormal];
		
		signinButton.titleLabel.font = [UIFont boldSystemFontOfSize:18];
		[signinButton setTitle:@"Sign In" forState:UIControlStateNormal];
		[loginCell addSubview:signinButton];
		[signinButton addTarget:self action:@selector(signin:) forControlEvents:UIControlEventTouchDown];
	}
	
	
	return loginCell;
}


- (BOOL)textFieldShouldReturn:(UITextField *)textField {
	
	if ([@"" isEqualToString:usernameField.text]) {
		[passwordField resignFirstResponder];
		[usernameField becomeFirstResponder];
		NSLog(@"username is nil");
	} 
	if ([@"" isEqualToString:passwordField.text]) {
		[usernameField resignFirstResponder];
		[passwordField becomeFirstResponder];
		NSLog(@"passwordField is nil");
	} 



	
	return YES;
}


- (NSString *)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section {
	if (section == 0) {
		return @"Sign in using your Controller username and password";
	}
	return nil;
}



- (void)dealloc {
    [super dealloc];
}


@end

