/*
 * OpenRemote, the Home of the Digital Home. Copyright 2008-2009, OpenRemote Inc.
 *
 * See the contributors.txt file in the distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation; either version 3.0 of the
 *  License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * You should have received a copy of the GNU General Public License along with this software;
 * if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston,
 * MA 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

/*
 * TODO
 *
 * @author <a href="mailto:">Allen Wei</a>
 */
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
              if (obj1 === obj2)
                return true;
            }
            else
              if (obj1 == obj2 && obj1.constructor.toString() == obj2.constructor)
                return true;

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