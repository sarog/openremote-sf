/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2012, OpenRemote Inc.
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
#import <UIKit/UIKit.h>

extern NSString *const kControllerFetchCapabilitiesPath;
extern NSString *const kControllerControlPath;
extern NSString *const kControllerStatusPath;
extern NSString *const kControllerPollingPath;
extern NSString *const kControllerFetchPanelsPath;
extern NSString *const kControllerFetchGroupMembersPath;

/**
 * Infomations about controller server panel client use.
 */
@interface ServerDefinition : NSObject {
}

/**
 * Get the qualified Resources url controller server provide, such as "http://192.168.100.100:8080/controller/resources" 
 * or "https://192.168.100.100:8443/controller/resources" if the secured port user specified is 8443 and SSL is enabled.
 */
+ (NSString *)imageUrl;

/**
 * Get the qualified url of controller server, such as "http://192.169.100.100:8080/controller"
 * or "https://192.169.100.100:8443/controller" if the secured port user specified is 8443 and SSL is enabled.
 */
+ (NSString *)serverUrl;

/**
 * Get the qualified url of logout action, such as "http://192.168.100.100:8080/controller/logout"
 * or "https://192.168.100.100:8443/controller/logout" if the secured port user specified is 8443 and SSL is enabled.
 */
+ (NSString *)logoutUrl;

/**
 * Get the qualified RESTful url of panels request, such as "http://192.168.100.100:8080/controller/rest/panels"
 * or "https://192.168.100.100:8443/controller/rest/panels" if the secured port user specified is 8443 and SSL is enabled.
 */
+ (NSString *)panelsRESTUrl;

/**
 * Get the qualified RESTful url of panel request, such as "http://192.168.100.100:8080/controller/rest/panel/{currentPanelID}"
 * or "https://192.168.100.100:8443/controller/rest/panel/{currentPanelID}" if the secured port user specified is 8443 and SSL is enabled.
 */
+ (NSString *)panelXmlRESTUrl;

/**
 * Get the host name or ip address of currrent controller server panel client use.
 * It means if the qualified url of current controller server is "http://192.168.100.100:8080/controller" or "http://myhomecontroller.com:8080",
 * the host name "192.168.100.100" or "myhomecontroller.com" will be returned.
 */
+ (NSString *)hostName;

/**
 * Get the qualified RESTful url of severs request, such as "http://192.168.100.100:8080/controller/rest/servers"
 * or "http://192.168.100.100:8080/controller/rest/servers" if the secured port user specified is 8443 and SSL is enabled.
 */
+ (NSString *)serversXmlRESTUrl;

/**
 * Get the secured qualifed server url or raw qualified server url.
 * For example, secured qualified server url is like "https://192.168.100.100:8443/contoller, if the secured port user specified is 8443 and SSL is enabled.
 * And raw qualified server url is like "http://192.168.100.100:8080/controller".
 */
+ (NSString *)securedOrRawServerUrl;

@end
