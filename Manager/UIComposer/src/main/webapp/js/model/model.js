var Model = function() {
    function Model() {
        var self = this;

        self.id = -1;
        self.className = getClassName(self);

        var _updateListeners = [];
        var _deleteListeners = [];

        self.addUpdateListener = function(view) {
            _updateListeners.push(view);
        };

        self.addDeleteListener = function(view) {
            _deleteListeners.push(view);
        };

        self.removeUpdateListener = function(view) {
			for (var index in _updateListeners) {
				if (compare(_updateListeners[index],view)) {
					_updateListeners.splice(index);
				}
			}
        };



        self.removeDeleteListener = function(view) {
           	for (var index in _deleteListeners) {
				if (compare(_deleteListeners[index],view)) {
					_deleteListeners.splice(index);
				}
			}
        };

        function compare(obj1, obj2) {
            if (obj1 === null || obj1 === undefined || obj2 === null || obj2 === undefined) {
                if (obj1 === obj2) return true;
            } else
            if (obj1 == obj2 && obj1.constructor.toString() == obj2.constructor) return true;
            return false;
        }


        self.updateModel = function() {
            for (var index in _updateListeners) {
                if (_updateListeners[index] != null && _updateListeners[index].updateView !== undefined) {
                    _updateListeners[index].updateView(self);
                }
            }
        };

        self.deleteModel = function() {
            for (var index in _deleteListeners) {
                if (_deleteListeners[index] != null && _deleteListeners[index].deleteView !== undefined) {
                    _deleteListeners[index].deleteView(self);
                }
            }
        };

    }
    return Model;
} ();