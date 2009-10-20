/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2009, OpenRemote Inc.
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

function getClassName(instance) {
    var m = instance.constructor.toString().match(/^\s*function\s+([^\s\(]+)/);
    return m ? m[1] : "";
};


function isArray(item) {
    if (typeof(item) != "object") {
        return false;
    }
    return Object.prototype.toString.apply(item) === '[object Array]';
}

function getFileNameFromPath (path) {
	if (path.lastIndexOf("/") != -1) {
		return path.substr(path.lastIndexOf("/")+1,path.length -1);
	}
	return path;
}

String.prototype.replaceAll = stringReplaceAll;

function stringReplaceAll(aFindText, aRepText) {
    var regexp = new RegExp(aFindText, "g");
    return this.replace(regexp, aRepText);
}

String.prototype.isNumber = isNumber;
function isNumber () {
	return (this.toString().match(/^[0-9]\d*$/) != null);
}

String.prototype.trim = trim;
function trim(){
	return this.replace(/(^\s*)|(\s*$)/g, "");
} 

function getVersionLabel(){
	var headUrl = "$HeadURL$";
	var revision = "$Revision$";
	var result = "Untagged";
    var verStr = "";
    var tagStart = -1;
    if ((tagStart = headUrl.indexOf("tags")) >= 0) {
       var tagsEnd = headUrl.indexOf("/", tagStart + 5);
       if (tagsEnd >= 0) {
          verStr = headUrl.substring(tagStart + 5, tagsEnd);
       }
    } else if ((tagStart = headUrl.indexOf("branches")) >= 0) {
       var tagsEnd = headUrl.indexOf("/", tagStart + 9);
       if (tagsEnd >= 0) {
          verStr = headUrl.substring(tagStart + 9, tagsEnd);
          verStr = "Branch: " + verStr;
       }
    } else if (revision != null) {
       tagStart = revision.indexOf("$Revision:");
       var tagsEnd = revision.indexOf("$", tagStart + 10);
       if (tagsEnd >= 0) {
          verStr = " r" + revision.substring(tagStart + 10, tagsEnd).trim();
       }
    }
    if (verStr.length != 0) {
       result = verStr.replace('_', '.');
    }
    return result;
}

