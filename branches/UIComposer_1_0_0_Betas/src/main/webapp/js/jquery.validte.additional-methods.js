jQuery.validator.addMethod("isImage", function(value, element) {
    if(value==""){
        return false;
    }
    return this.optional(element) || /.+?\.(png|gif|jpg)/.test(value); 
}, "Please select an gif, jpg, png type image."); 