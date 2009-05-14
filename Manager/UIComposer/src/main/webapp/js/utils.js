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
	return (this.toString().match(/^[1-9]\d*$/) != null);
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

