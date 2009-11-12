/*
 * OpenRemote, the Home of the Digital Home. Copyright 2008, OpenRemote Inc.
 *
 * See the contributors.txt file in the distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3.0 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * You should have received a copy of the GNU General Public License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF site:
 * http://www.fsf.org.
 */

$(document).ready(function() {
    $('#uploadForm').ajaxForm(function(result) {
    	if(result == 'OK'){
    		message("upload successful");
    	}else if (result == 'disabled'){
    		error("upload is disabled");
    	}else{
    		error("upload failed");
    	}
    }); 
	$('#uploadSubmit').click(function(){
		var zipPath = $('#zip').val();
		if(zipPath == ''){
			error("Please select a zip first");
			return false;
		}else if(!/.+?\.zip/.test(zipPath)){
			error("Only zip is allowed");
			return false;
		}
	});
	$("#version").append(getVersionLabel());
});
function message(msg){
	$('#errMsg').text("");
	$('#msg').text(msg);
}
function error(msg){
	$('#errMsg').text(msg);
	$('#msg').text("");
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
          verStr = " r" + $.trim(revision.substring(tagStart + 10, tagsEnd));
       }
    }
    if (verStr.length != 0) {
       result = verStr.replace('_', '.');
    }
    return result;
}