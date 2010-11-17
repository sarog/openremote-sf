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


#import "AddServerViewController.h"
#import "TextFieldCell.h"
#import "AppSettingsDefinition.h"

@implementation AddServerViewController

@synthesize editingItem,servers;

- (id)init {
	if (self = [super initWithStyle:UITableViewStyleGrouped]) {
		
	}
	return self;
}

// Customize view of users input custom controller server.
- (void)viewWillAppear:(BOOL)animated {
	if (editingItem == nil) {
		self.editingItem = [NSMutableDictionary dictionaryWithObjectsAndKeys:@"",@"url",[NSNumber numberWithBool:NO],@"choose",nil];
		newItem = YES;
		self.title = @"Add a Server";
	} else {
		self.title = [NSString stringWithFormat:@"Editing %@",[[editingItem valueForKey:@"url"] stringValue]];
	}
	if (!serverUrlFieldCell) {
		serverUrlFieldCell = [[TextFieldCell alloc] initWithFrame:CGRectZero reuseIdentifier:@"serverUrlCell"];
		[serverUrlFieldCell.textField setDelegate:self];
	}
	serverUrlFieldCell.textField.text = [editingItem valueForKey:@"url"];
	[serverUrlFieldCell.textField becomeFirstResponder];
	
}

// Delegate method of UITextFieldDelegate and is called when 'return' key pressed. return NO to ignore.
- (BOOL)textFieldShouldReturn:(UITextField *)textField {
	NSLog(@"text field is %@",serverUrlFieldCell.textField.text);
	NSString *url = [NSString stringWithFormat:@"http://%@",serverUrlFieldCell.textField.text];
	NSLog(@"set url to %@",url);
	[editingItem setValue:url forKey:@"url"];
	if (newItem) {
		if (servers.count == 0) {
			[editingItem setValue:[NSNumber numberWithBool:YES] forKey:@"choose"];
		}
		[servers addObject:editingItem];
		newItem = NO;
	}
	[AppSettingsDefinition writeToFile];
	[self.navigationController popViewControllerAnimated:YES];
	return YES;
}

- (void)viewWillDisappear:(BOOL)animated {
	[serverUrlFieldCell.textField resignFirstResponder];
}


- (void)tableView:(UITableView *)aTableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
	[aTableView deselectRowAtIndexPath:indexPath animated:YES];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning]; // Releases the view if it doesn't have a superview
    // Release anything that's not essential, such as cached data
}

#pragma mark Table view methods

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 1;
}


// Customize the number of rows in the table view.
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return 1;
}


// Customize the appearance of table view cells.
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


- (void)dealloc {
	[editingItem release];
	[serverUrlFieldCell release];
	[headerView release];
	[footerView release];
    [super dealloc];
}


@end

