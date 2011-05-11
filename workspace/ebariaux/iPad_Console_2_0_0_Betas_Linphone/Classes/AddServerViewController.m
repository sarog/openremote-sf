/* OpenRemote, the Home of the Digital Home.
 *  * Copyright 2008-2011, OpenRemote Inc-2009, OpenRemote Inc.
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


#import "AddServerViewController.h"
#import "TextFieldCell.h"
#import "AppSettingsDefinition.h"
#import "ViewHelper.h"

// TODO: customize keyboard with keys such as http://, https://, /controller, 8080,  and other std ports to help text entry

@implementation AddServerViewController

@synthesize urlToEdit, delegate;

- (id)init {
	self = [super initWithStyle:UITableViewStyleGrouped];
	return self;
}

- (void)dealloc {
	[urlToEdit release];
	[serverUrlFieldCell release];
    [super dealloc];
}

// Customize view of users input custom controller server.
- (void)viewWillAppear:(BOOL)animated {
	if (urlToEdit == nil) {
		self.title = @"Add a Server";
	} else {
		self.title = [NSString stringWithFormat:@"Editing %@", urlToEdit];
	}
	if (!serverUrlFieldCell) {
		serverUrlFieldCell = [[TextFieldCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:@"serverUrlCell"];
		[serverUrlFieldCell.textField setDelegate:self];
	}
    if (urlToEdit) {
        serverUrlFieldCell.textField.text = urlToEdit;
    }
	[serverUrlFieldCell.textField becomeFirstResponder];
}

// Delegate method of UITextFieldDelegate and is called when 'return' key pressed. return NO to ignore.
- (BOOL)textFieldShouldReturn:(UITextField *)textField {
    
	NSLog(@"text field is %@", textField.text);
    
    NSString *url = nil;
    if ([textField.text hasPrefix:@"http://"] || [textField.text hasPrefix:@"https://"]) {
        url = textField.text;
    } else {
        url = [NSString stringWithFormat:@"http://%@", textField.text];
    }
	NSURL *nsUrl = [NSURL URLWithString:url];
	if ([nsUrl scheme] == nil) {
		[ViewHelper showAlertViewWithTitle:@"" Message:@"URL is invalid."];
		return NO;
	}
	NSLog(@"set url to %@",url);

    [delegate didAddServerURL:url];
	return YES;
}

- (void)viewWillDisappear:(BOOL)animated {
	[serverUrlFieldCell.textField resignFirstResponder];
}

- (void)tableView:(UITableView *)aTableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
	[aTableView deselectRowAtIndexPath:indexPath animated:YES];
}

#pragma mark Table view methods

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return 1;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
	return serverUrlFieldCell;
 }

- (CGFloat)tableView:(UITableView *)aTableView heightForHeaderInSection:(NSInteger)section {
	return 40.0;
}

- (NSString *)tableView:(UITableView *)tableView titleForFooterInSection:(NSInteger)section {
	return @"Sample:192.168.1.2:8080/controller";
}

- (NSString *)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section {
	return @"Controller URL:";
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)toInterfaceOrientation {
	return YES;
}

@end

