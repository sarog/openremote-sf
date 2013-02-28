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

        self.updateModel = function() {
            for (var index in _updateListeners) {
				if (_updateListeners[index].updateView !== undefined) {
					_updateListeners[index].updateView();
				}
            }
        };

        self.deleteModel = function() {
            for (var index in _deleteListeners) {
                if (_deleteListeners[index].deleteView !== undefined) {
					_deleteListeners[index].deleteView();
				}
            }
        };

    }
    return Model;
} ();