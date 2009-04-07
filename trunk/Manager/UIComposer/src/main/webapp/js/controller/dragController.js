function initDraggableAndDroppable() {

}


function makeCommandBtnDraggable(items) {
    var btns;
    if (typeof(items) == 'undefined') {
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

function selectIphoneBtn(btn) {
    $(".iphone_btn").removeClass("selected");
    $(".iphone_btn .delete_icon").remove();
    btn.addClass("selected");

}


