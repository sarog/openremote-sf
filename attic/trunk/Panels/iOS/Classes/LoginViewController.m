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
#import "LoginViewController.h"
#import "Definition.h"
#import "ViewHelper.h"
#import "NotificationConstant.h"
#import "ORConsoleSettingsManager.h"
#import "ORConsoleSettings.h"
#import "ORController.h"

@interface LoginViewController ()

- (void)goBack:(id)sender;

@end

@implementation LoginViewController

- (id)initWithDelegate:(NSObject <LoginViewControllerDelegate> *)delegate 
{
    self = [super initWithStyle:UITableViewStyleGrouped];
	if (self) {
		[self setTitle:@"Sign in"];
		theDelegate = delegate;
	}
	return self;
}

- (void)dealloc
{
	[usernameField release];
	[passwordField release];
	
	[super dealloc];
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	[[NSNotificationCenter defaultCenter] postNotificationName:NotificationHideLoading object:nil];
	self.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc]initWithTitle:@"Back" style:UIBarButtonItemStylePlain target:self action:@selector(goBack:)];
}

- (void)viewDidUnload
{
	[[NSNotificationCenter defaultCenter] postNotificationName:NotificationShowLoading object:nil];
    [super viewDidUnload];
}

// Back to the view where loginView was triggered from.
- (void)goBack:(id)sender
{
	[self dismissModalViewControllerAnimated:YES];
	if ([theDelegate respondsToSelector:@selector(onBackFromLogin)]) {
		[theDelegate performSelector:@selector(onBackFromLogin)];
	}
}

// Send sign in request to remote controller server by loginViewController's delegate.
- (void)signin:(id)sender
{
	if (usernameField.text == nil || passwordField.text == nil ||
			[@"" isEqualToString:usernameField.text] || [@"" isEqualToString:passwordField.text]) {
		[ViewHelper showAlertViewWithTitle:@"" Message:@"No username or password entered."];
		return;
	}
	[[NSNotificationCenter defaultCenter] postNotificationName:NotificationShowLoading object:nil];
    
    ORController *activeController = [ORConsoleSettingsManager sharedORConsoleSettingsManager].consoleSettings.selectedController;
    activeController.userName = usernameField.text;
	activeController.password = passwordField.text;
    [[ORConsoleSettingsManager sharedORConsoleSettingsManager] saveConsoleSettings];
    
	[self dismissModalViewControllerAnimated:NO];
	if ([theDelegate respondsToSelector:@selector(onSignin)]) {
		[theDelegate performSelector:@selector(onSignin)];
	}
}

#pragma mark Table view methods

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return 2;
}

// Customize the number of rows in the table view.
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
	if (section == 0) {
		return 2;
	} else if (section == 1) {
		return 1;
	}
	return 0;
}

// Customize the appearance of table view cells.
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{    
	static NSString *loginCellIdentifier = @"loginCell";
	
	UITableViewCell *loginCell = [tableView dequeueReusableCellWithIdentifier:loginCellIdentifier];

	if (loginCell == nil) {
		loginCell = [[[UITableViewCell alloc] initWithFrame:CGRectZero reuseIdentifier:loginCellIdentifier] autorelease];
		loginCell.selectionStyle = UITableViewCellSelectionStyleNone;
	}

	if (indexPath.section == 0) {
		UITextField *textField = [[UITextField alloc] initWithFrame:CGRectZero];
		textField.contentVerticalAlignment = UIControlContentHorizontalAlignmentCenter;
		textField.font = [UIFont systemFontOfSize:22];
		textField.keyboardType = UIKeyboardTypeURL;
		textField.adjustsFontSizeToFitWidth = YES;
		textField.autocapitalizationType = UITextAutocapitalizationTypeNone;
		textField.autocorrectionType = UITextAutocorrectionTypeNo;
		textField.textColor = [UIColor darkGrayColor];
		textField.returnKeyType = UIReturnKeyDone;
		textField.frame = CGRectInset(CGRectMake(100, (loginCell.bounds.size.height - 26)/2.0, loginCell.bounds.size.width,26),10,0);
		[textField setDelegate:self];
		[loginCell.contentView addSubview:textField];
    
		if (indexPath.row == 0) {
			loginCell.textLabel.text = @"Username";
			[textField becomeFirstResponder];
			usernameField = textField;
            
            ORController *activeController = [ORConsoleSettingsManager sharedORConsoleSettingsManager].consoleSettings.selectedController;
			usernameField.text = activeController.userName; 
		} else if (indexPath.row == 1) {
			loginCell.textLabel.text = @"Password";
			[textField setSecureTextEntry:YES];
			passwordField = textField;
		}
	} else if (indexPath.section == 1) {
		UIButton *signinButton = [UIButton buttonWithType:UIButtonTypeCustom];
		
		UIImage *buttonImage = [[UIImage imageNamed:@"btn_green.png"] stretchableImageWithLeftCapWidth:20 topCapHeight:20];
		[signinButton setBackgroundImage:buttonImage forState:UIControlStateNormal];
		float height = [tableView rectForRowAtIndexPath:indexPath].size.height;
		[signinButton setFrame:CGRectMake(0, 0, loginCell.frame.size.width, height)];
		signinButton.autoresizingMask = UIViewAutoresizingFlexibleWidth;
		signinButton.titleLabel.font = [UIFont boldSystemFontOfSize:18];
		[signinButton setTitle:@"Sign In" forState:UIControlStateNormal];
		[loginCell.contentView addSubview:signinButton];		
		[signinButton addTarget:self action:@selector(signin:) forControlEvents:UIControlEventTouchDown];
	}
	return loginCell;
}

- (void)tableView:(UITableView *)tableView willDisplayCell:(UITableViewCell *)cell forRowAtIndexPath:(NSIndexPath *)indexPath
{	
	[[cell textLabel] setBackgroundColor:[UIColor clearColor]];
}

- (BOOL)textFieldShouldReturn:(UITextField *)textField
{	
	if ([@"" isEqualToString:usernameField.text]) {
		[usernameField becomeFirstResponder];
		return YES;
	} else {
		[passwordField becomeFirstResponder];		
	}
	
	if ([@"" isEqualToString:passwordField.text]) {
		[passwordField becomeFirstResponder];
		return YES;
	} else {
		[usernameField becomeFirstResponder];		
	}
	
	[self signin:nil];

	return YES;
}

- (NSString *)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section
{
	if (section == 0) {
		return @"Sign in using your Controller username and password";
	}
	return nil;
}

- (NSString *)tableView:(UITableView *)tableView titleForFooterInSection:(NSInteger)section
{
	if (section == 1) {
		return @"Commands and updates from Controller are secured. This requires user authentication.";
	}
	return nil;
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)toInterfaceOrientation
{
	return YES;
}

@end