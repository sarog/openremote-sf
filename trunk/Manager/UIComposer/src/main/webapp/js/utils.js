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


String.prototype.replaceAll = stringReplaceAll;

function stringReplaceAll(aFindText, aRepText) {
    var regexp = new RegExp(aFindText, "g");
    return this.replace(regexp, aRepText);
}

String.prototype.isNumber = isNumber;
function isNumber () {
	return (this.toString().match(/^[1-9]\d*$/) != null);
}

