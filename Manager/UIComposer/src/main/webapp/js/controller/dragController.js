function makeCommandBtnDraggable(items) {
    var btns;
    if (items === undefined) {
        btns = $(".blue_btn");
    } else {
        btns = items;
    }
    btns.draggable({
        zIndex: 2700,
        cursor: 'move',
        helper: 'clone',
        start: function(event, ui) {
            $(this).draggable('option', 'revert', false);
        }
    });
}



