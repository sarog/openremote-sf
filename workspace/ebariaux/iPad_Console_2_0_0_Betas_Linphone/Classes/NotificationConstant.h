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


#import <Foundation/Foundation.h>

// This notification will be post when UpdateController have been finished to update.
extern NSString *const DefinitionUpdateDidFinishNotification;
extern NSString *const DefinationNeedNotUpdate;

// This notificaton will be post while needing to show appsetting view.
extern NSString *const NotificationShowSettingsView; 

// This notificaton will be post while needing to hide init view.
extern NSString *const NotificationHideInitView; 
extern NSString *const NotificationRefreshAcitivitiesView;

// This notificaton will be post while needing to refresh groups view.
extern NSString *const NotificationRefreshGroupsView;

// This notificaton will be post while status is changed.
extern NSString *const NotificationPollingStatusIdFormat;

// This notificaton will be post while triggering navigation actions.
extern NSString *const NotificationNavigateTo;

// This notificaton will be post while needing users to login.
extern NSString *const NotificationPopulateCredentialView;

// This notificaton will be post while needing to show appsetting view.
extern NSString *const NotificationPopulateSettingsView;

// This notificaton will be post when back navigation is triggered.
extern NSString *const NotificationNavigateBack;

// This notificaton will be post while needing to show loading view.
extern NSString *const NotificationShowLoading;

// This notificaton will be post while needing to hide loading view.
extern NSString *const NotificationHideLoading;

@interface NotificationConstant : NSObject {

}

@end
