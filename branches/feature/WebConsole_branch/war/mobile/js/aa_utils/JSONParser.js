/**
 * It's responsible for extracting into model instances from JSON object which is from controller server.
 * auther: handy.wang 2010-07-15
 */
JSONParser = (function() {
 
 return function(jsonDataParam, delegateParam) {
   
   var self = this;
   var jsonData = jsonDataParam;
   self.delegate = delegateParam;
   
   this.startParse = function() {
     recursiveParse(null, jsonData);
     self.delegate.didParseFinished();
   };
   
   this.setDelegate = function(delegateParam) {
     self.delegate = delegateParam;
   };
   
   function recursiveParse(nodeName, jsonData) {
     var properties = {};
     var isLeaf = true;
     for (var key in jsonData) {
       var value = jsonData[key];

       if (key.toString().indexOf("@") === 0 || key.toString().indexOf("#") === 0) {
         properties[key] = value;
       } else {
         isLeaf = false;
       }
     }
     if (nodeName != null) {
       self.delegate.didParse(self, nodeName, properties);         
     }
     
     if (isLeaf) {
       return;
     }

     for (var key in jsonData) {
       var value = jsonData[key];
       if (key.toString().indexOf("@") === 0) {
         continue;
       }else if (Object.prototype.toString.apply(value) === "[object Array]") {
         for(var index in value) {
           var oldDelegate = self.delegate;
           recursiveParse(key, value[index]);
           self.setDelegate(oldDelegate);
         }
       } else {
         var oldDelegate = self.delegate;
         recursiveParse(key, value);
         self.setDelegate(oldDelegate);
       }
     }
   }
   
 };
})();