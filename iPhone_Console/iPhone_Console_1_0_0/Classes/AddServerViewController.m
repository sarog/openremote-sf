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
/*
- (id)initWithStyle:(UITableViewStyle)style {
    // Override initWithStyle: if you create the controller programmatically and want to perform customization that is not appropriate for viewDidLoad.
    if (self = [super initWithStyle:style]) {
    }
    return self;
}
*/

/*
- (void)viewDidLoad {
    [super viewDidLoad];

    // Uncomment the following line to display an Edit button in the navigation bar for this view controller.
    // self.navigationItem.rightBarButtonItem = self.editButtonItem;
}
*/


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
- (BOOL)textFieldShouldReturn:(UITextField *)textField {
	NSLog(@"text field is %@",serverUrlFieldCell.textField.text);
	NSString *url = [NSString stringWithFormat:@"http://%@/controller",serverUrlFieldCell.textField.text];
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
/*
- (void)viewDidAppear:(BOOL)animated {
    [super viewDidAppear:animated];
}
*/



- (void)viewWillDisappear:(BOOL)animated {
	[serverUrlFieldCell.textField resignFirstResponder];
}

/*
- (void)viewDidDisappear:(BOOL)animated {
			[super viewDidDisappear:animated];
			
}
*/
/*
// Override to allow orientations other than the default portrait orientation.
- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation {
    // Return YES for supported orientations
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}
*/

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
	return @"Sample:192.168.1.2:8080";
}

- (NSString *)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section {
	return @"Controller URL:";
}

//- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
//    // Navigation logic may go here. Create and push another view controller.
//	// AnotherViewController *anotherViewController = [[AnotherViewController alloc] initWithNibName:@"AnotherView" bundle:nil];
//	// [self.navigationController pushViewController:anotherViewController];
//	// [anotherViewController release];
//}


/*
// Override to support conditional editing of the table view.
- (BOOL)tableView:(UITableView *)tableView canEditRowAtIndexPath:(NSIndexPath *)indexPath {
    // Return NO if you do not want the specified item to be editable.
    return YES;
}
*/


/*
// Override to support editing the table view.
- (void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath {
    
    if (editingStyle == UITableViewCellEditingStyleDelete) {
        // Delete the row from the data source
        [tableView deleteRowsAtIndexPaths:[NSArray arrayWithObject:indexPath] withRowAnimation:YES];
    }   
    else if (editingStyle == UITableViewCellEditingStyleInsert) {
        // Create a new instance of the appropriate class, insert it into the array, and add a new row to the table view
    }   
}
*/


/*
// Override to support rearranging the table view.
- (void)tableView:(UITableView *)tableView moveRowAtIndexPath:(NSIndexPath *)fromIndexPath toIndexPath:(NSIndexPath *)toIndexPath {
}
*/


/*
// Override to support conditional rearranging of the table view.
- (BOOL)tableView:(UITableView *)tableView canMoveRowAtIndexPath:(NSIndexPath *)indexPath {
    // Return NO if you do not want the item to be re-orderable.
    return YES;
}
*/


- (void)dealloc {
	[editingItem release];
	[serverUrlFieldCell release];
	[headerView release];
	[footerView release];
    [super dealloc];
}


@end

